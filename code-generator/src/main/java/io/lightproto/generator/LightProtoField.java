package io.lightproto.generator;

import io.protostuff.parser.Field;
import io.protostuff.parser.MessageField;

import java.io.PrintWriter;

import static io.lightproto.generator.Util.camelCase;
import static io.lightproto.generator.Util.upperCase;

public abstract class LightProtoField<FieldType extends Field<?>> {

    protected final FieldType field;
    protected final int index;
    protected final String ccName;

    protected LightProtoField(FieldType field, int index) {
        this.field = field;
        this.index = index;
        this.ccName = camelCase(field.getName());
    }

    public static LightProtoField create(Field field, int index) {
        if (field.isRepeated()) {
            if (field.isMessageField()) {
                return new LightProtoRepeatedMessageField((MessageField) field, index);
            } else if (field.isStringField()) {
                return new LightProtoRepeatedStringField((Field.String) field, index);
            } else if (field.isNumberField() || field.isEnumField() || field.isBoolField()) {
                return new LightProtoRepeatedNumberField(field, index);
            } else if (field.isBytesField()) {
                return new LightProtoRepeatedBytesField((Field.Bytes) field, index);
            }
        } else if (field.isMessageField()) {
            return new LightProtoMessageField((MessageField) field, index);
        } else if (field.isBytesField()) {
            return new LightProtoBytesField((Field.Bytes) field, index);
        } else if (field.isStringField()) {
            return new LightProtoStringField((Field.String) field, index);
        } else if (field.isNumberField() || field.isEnumField()) {
            return new LightProtoNumberField(field, index);
        } else if (field.isBoolField()) {
            return new LightProtoBooleanField(field, index);
        }

        throw new IllegalArgumentException("Unknown field: " + field);
    }

    public int index() {
        return index;
    }

    public boolean isRepeated() {
        return field.isRepeated();
    }

    public boolean isRequired() {
        return field.isRequired();
    }

    public void docs(PrintWriter w) {
        field.getDocs().forEach(d -> {
            w.format("        // %s\n", d);
        });
    }

    abstract public void declaration(PrintWriter w);

    public void tags(PrintWriter w) {
        w.format("        private static final int %s = %d;\n", fieldNumber(), field.getNumber());
        w.format("        private static final int %s = (%s << LightProtoCodec.TAG_TYPE_BITS) | %s;\n", tagName(), fieldNumber(), typeTag());
        w.format("        private static final int %s = 1 << (%d %% 32);\n", fieldMask(), index);
    }

    public void has(PrintWriter w) {
        w.format("        public boolean %s() {\n", camelCase("has", field.getName()));
        w.format("            return (_bitField%d & %s) != 0;\n", bitFieldIndex(), fieldMask());
        w.format("        }\n");
    }

    abstract public void clear(PrintWriter w);

    abstract public void setter(PrintWriter w, String enclosingType);

    abstract public void getter(PrintWriter w);

    abstract public void serializedSize(PrintWriter w);

    abstract public void serialize(PrintWriter w);

    abstract public void parse(PrintWriter w);

    public boolean isPackable() {
        return field.isRepeated() && field.isPackable();
    }

    public void parsePacked(PrintWriter w) {
    }

    abstract protected String typeTag();

    protected String tagName() {
        return "_" + upperCase(field.getName(), "tag");
    }

    protected String fieldNumber() {
        return "_" + upperCase(field.getName(), "fieldNumber");
    }

    protected String fieldMask() {
        return "_" + upperCase(field.getName(), "mask");
    }

    protected int bitFieldIndex() {
        return index / 32;
    }
}
