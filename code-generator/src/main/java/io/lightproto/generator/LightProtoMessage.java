package io.lightproto.generator;

import io.protostuff.parser.Message;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.lightproto.generator.Util.camelCase;

public class LightProtoMessage {

    private final Message message;
    private final List<LightProtoEnum> enums;
    private final List<LightProtoField> fields;
    private final List<LightProtoMessage> nestedMessages;

    public LightProtoMessage(Message message) {
        this.message = message;
        this.enums = message.getNestedEnumGroups().stream().map(LightProtoEnum::new).collect(Collectors.toList());
        this.nestedMessages = message.getNestedMessages().stream().map(LightProtoMessage::new).collect(Collectors.toList());

        this.fields = new ArrayList<>();
        for (int i = 0; i < message.getFields().size(); i++) {
            fields.add(LightProtoField.create(message.getFields().get(i), i));
        }
    }

    public void generate(PrintWriter w) {
        w.format("    public static class %s {\n", message.getName());

        enums.forEach(e -> e.generate(w));
        nestedMessages.forEach(nm -> nm.generate(w));
        fields.forEach(field -> {
            field.docs(w);
            field.declaration(w);
            field.tags(w);
            field.has(w);
            field.getter(w);
            field.setter(w, message.getName());
            w.println();
        });

        generateBitFields(w);
        generateSerialize(w);
        generateGetSerializedSize(w);
        generateParseFrom(w);
        generateCheckRequiredFields(w);
        generateClear(w);

        w.println("        private int _cachedSize;\n");
        w.println("        private io.netty.buffer.ByteBuf _parsedBuffer;\n");
        w.println("    }");
        w.println();
    }

    private void generateParseFrom(PrintWriter w) {
        w.format("        public void parseFrom(io.netty.buffer.ByteBuf _buffer, int _size) {\n");
        w.format("            clear();\n");
        w.format("            int _endIdx = _buffer.readerIndex() + _size;\n");
        w.format("            while (_buffer.readerIndex() < _endIdx) {\n");
        w.format("                int _tag = LightProtoCodec.readVarInt(_buffer);\n");
        w.format("                switch (_tag) {\n");

        for (LightProtoField field : fields) {
            w.format("                case %s:\n", field.tagName());
            w.format("                    _bitField%d |= %s;\n", field.bitFieldIndex(), field.fieldMask());
            field.parse(w);
            w.format("                    break;\n");
        }

        for (LightProtoField field : fields) {
            if (field.isPackable()) {
                w.format("                case %s_PACKED:\n", field.tagName());
                field.parsePacked(w);
                w.format("                    break;\n");
            }
        }

        w.format("                default:\n");
        w.format("                    LightProtoCodec.skipUnknownField(_tag, _buffer);\n");
        w.format("                }\n");
        w.format("            }\n");
        if (hasRequiredFields()) {
            w.format("            checkRequiredFields();\n");
        }
        w.format("            _parsedBuffer = _buffer;\n");
        w.format("        }\n");
    }

    private void generateClear(PrintWriter w) {
        w.format("        public %s clear() {\n", message.getName());
        w.format("            _parsedBuffer = null;\n");
        w.format("            _cachedSize = -1;\n");
        for (int i = 0; i < bitFieldsCount(); i++) {
            w.format("            _bitField%d = 0;\n", i);
        }

        for (LightProtoField f : fields) {
            f.clear(w);
        }

        w.format("            return this;\n");
        w.format("        }\n");
    }

    private void generateSerialize(PrintWriter w) {
        w.format("        public int writeTo(io.netty.buffer.ByteBuf _b) {\n");
        if (hasRequiredFields()) {
            w.format("            checkRequiredFields();\n");
        }
        w.format("            int _writeIdx = _b.writerIndex();\n");
        for (LightProtoField f : fields) {
            if (!f.isRequired()) {
                w.format("            if (%s()) {\n", camelCase("has", f.field.getName()));
                f.serialize(w);
                w.format("            }\n");
            } else {
                // If required, skip the has() check
                f.serialize(w);
            }
        }

        w.format("            return (_b.writerIndex() - _writeIdx);\n");
        w.format("        }\n");
    }

    private void generateGetSerializedSize(PrintWriter w) {
        w.format("public int getSerializedSize() {\n");
        w.format("    if (_cachedSize > -1) {\n");
        w.format("        return _cachedSize;\n");
        w.format("    }\n");
        w.format("\n");

        w.format("    int _size = 0;\n");
        fields.forEach(field -> {
            if (field.isRequired()) {
                field.serializedSize(w);
            } else {
                w.format("        if (%s()) {\n", camelCase("has", field.field.getName()));
                field.serializedSize(w);
                w.format("        }\n");
            }
        });

        w.format("            _cachedSize = _size;\n");
        w.format("            return _size;\n");
        w.format("        }\n");
    }

    private void generateBitFields(PrintWriter w) {
        for (int i = 0; i < bitFieldsCount(); i++) {
            w.format("private int _bitField%d;\n", i);
            w.format("private static final int _REQUIRED_FIELDS_MASK%d = 0", i);
            int idx = i;
            fields.forEach(f -> {
                if (f.isRequired() && f.index() / 32 == idx) {
                    w.format(" | %s", f.fieldMask());
                }
            });
            w.println(";");
        }
    }

    private void generateCheckRequiredFields(PrintWriter w) {
        if (!hasRequiredFields()) {
            return;
        }

        w.format("        private void checkRequiredFields() {\n");
        w.format("            if (");
        for (int i = 0; i < bitFieldsCount(); i++) {
            if (i != 0) {
                w.print("\n             || ");
            }

            w.format("(_bitField%d & _REQUIRED_FIELDS_MASK%d) != _REQUIRED_FIELDS_MASK%d", i, i, i);
        }

        w.format(")   {\n");
        w.format("      throw new IllegalStateException(\"Some required fields are missing\");\n");
        w.format("    }\n");
        w.format("}\n");
    }

    private int bitFieldsCount() {
        if (message.getFieldCount() == 0) {
            return 0;
        }

        return (int) Math.ceil(message.getFields().size() / 32.0);
    }


    private boolean hasRequiredFields() {
        for (LightProtoField field : fields) {
            if (field.isRequired()) {
                return true;
            }
        }

        return false;
    }
}
