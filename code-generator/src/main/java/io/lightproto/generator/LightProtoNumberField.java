package io.lightproto.generator;

import com.google.common.collect.Maps;
import io.protostuff.parser.Field;

import java.io.PrintWriter;
import java.util.Map;

import static io.lightproto.generator.Util.camelCase;

public class LightProtoNumberField extends LightProtoField<Field<?>> {


    private static final Map<String, String> typeToTag = Maps.newHashMap();

    static {
        typeToTag.put("double", "LightProtoCodec.WIRETYPE_FIXED64");
        typeToTag.put("float", "LightProtoCodec.WIRETYPE_FIXED32");
        typeToTag.put("bool", "LightProtoCodec.WIRETYPE_VARINT");
        typeToTag.put("int32", "LightProtoCodec.WIRETYPE_VARINT");
        typeToTag.put("int64", "LightProtoCodec.WIRETYPE_VARINT");
        typeToTag.put("uint32", "LightProtoCodec.WIRETYPE_VARINT");
        typeToTag.put("uint64", "LightProtoCodec.WIRETYPE_VARINT");
        typeToTag.put("sint32", "LightProtoCodec.WIRETYPE_VARINT");
        typeToTag.put("sint64", "LightProtoCodec.WIRETYPE_VARINT");
        typeToTag.put("fixed32", "LightProtoCodec.WIRETYPE_FIXED32");
        typeToTag.put("fixed64", "LightProtoCodec.WIRETYPE_FIXED64");
        typeToTag.put("sfixed32", "LightProtoCodec.WIRETYPE_FIXED32");
        typeToTag.put("sfixed64", "LightProtoCodec.WIRETYPE_FIXED64");
    }

    public LightProtoNumberField(Field<?> field, int index) {
        super(field, index);
    }

    static void serializeNumber(PrintWriter w, Field<?> field, String name) {
        if (field.isEnumField()) {
            w.format("                LightProtoCodec.writeVarInt(_b, %s.getValue());\n", name);
        } else if (field.getProtoType().equals("bool")) {
            w.format("                _b.writeBoolean(%s);\n", name);
        } else if (field.getProtoType().equals("int32")) {
            w.format("                LightProtoCodec.writeVarInt(_b, %s);\n", name);
        } else if (field.getProtoType().equals("uint32")) {
            w.format("                LightProtoCodec.writeVarInt(_b, %s);\n", name);
        } else if (field.getProtoType().equals("sint32")) {
            w.format("                LightProtoCodec.writeSignedVarInt(_b, %s);\n", name);
        } else if (field.getProtoType().equals("sint64")) {
            w.format("                LightProtoCodec.writeSignedVarInt64(_b, %s);\n", name);
        } else if (field.getProtoType().equals("int64")) {
            w.format("                LightProtoCodec.writeVarInt64(_b, %s);\n", name);
        } else if (field.getProtoType().equals("uint64")) {
            w.format("                LightProtoCodec.writeVarInt64(_b, %s);\n", name);
        } else if (field.getProtoType().equals("fixed32")) {
            w.format("                LightProtoCodec.writeFixedInt32(_b, %s);\n", name);
        } else if (field.getProtoType().equals("fixed64")) {
            w.format("                LightProtoCodec.writeFixedInt64(_b, %s);\n", name);
        } else if (field.getProtoType().equals("sfixed32")) {
            w.format("                LightProtoCodec.writeFixedInt32(_b, %s);\n", name);
        } else if (field.getProtoType().equals("sfixed64")) {
            w.format("                LightProtoCodec.writeFixedInt64(_b, %s);\n", name);
        } else if (field.getProtoType().equals("double")) {
            w.format("                LightProtoCodec.writeDouble(_b, %s);\n", name);
        } else if (field.getProtoType().equals("float")) {
            w.format("                LightProtoCodec.writeFloat(_b, %s);\n", name);
        } else {
            throw new IllegalArgumentException("Failed to write serializer for field: " + field.getProtoType());
        }
    }

    static String parseNumber(Field<?> field) {
        if (field.isEnumField()) {
            return String.format("%s.valueOf(LightProtoCodec.readVarInt(_buffer))", field.getJavaType());
        } else if (field.getProtoType().equals("bool")) {
            return "LightProtoCodec.readVarInt(_buffer) == 1";
        } else if (field.getProtoType().equals("int32")) {
            return "LightProtoCodec.readVarInt(_buffer)";
        } else if (field.getProtoType().equals("uint32")) {
            return "LightProtoCodec.readVarInt(_buffer)";
        } else if (field.getProtoType().equals("sint32")) {
            return "LightProtoCodec.readSignedVarInt(_buffer)";
        } else if (field.getProtoType().equals("sint64")) {
            return "LightProtoCodec.readSignedVarInt64(_buffer)";
        } else if (field.getProtoType().equals("int64")) {
            return "LightProtoCodec.readVarInt64(_buffer)";
        } else if (field.getProtoType().equals("uint64")) {
            return "LightProtoCodec.readVarInt64(_buffer)";
        } else if (field.getProtoType().equals("fixed32")) {
            return "LightProtoCodec.readFixedInt32(_buffer)";
        } else if (field.getProtoType().equals("fixed64")) {
            return "LightProtoCodec.readFixedInt64(_buffer)";
        } else if (field.getProtoType().equals("sfixed32")) {
            return "LightProtoCodec.readFixedInt32(_buffer)";
        } else if (field.getProtoType().equals("sfixed64")) {
            return "LightProtoCodec.readFixedInt64(_buffer)";
        } else if (field.getProtoType().equals("double")) {
            return "LightProtoCodec.readDouble(_buffer)";
        } else if (field.getProtoType().equals("float")) {
            return "LightProtoCodec.readFloat(_buffer)";
        } else {
            throw new IllegalArgumentException("Failed to write parser for field: " + field.getProtoType());
        }
    }

    static String serializedSizeOfNumber(Field<?> field, String name) {
        if (field.isEnumField()) {
            return String.format("LightProtoCodec.computeVarIntSize(%s.getValue())", name);
        } else if (field.getProtoType().equals("sint32")) {
            return String.format("LightProtoCodec.computeSignedVarIntSize(%s)", name);
        } else if (field.getProtoType().equals("sint64")) {
            return String.format("LightProtoCodec.computeSignedVarInt64Size(%s)", name);
        } else if (field.getProtoType().equals("int32")) {
            return String.format("LightProtoCodec.computeVarIntSize(%s)", name);
        } else if (field.getProtoType().equals("uint32")) {
            return String.format("LightProtoCodec.computeVarIntSize(%s)", name);
        } else if (field.getProtoType().equals("int64")) {
            return String.format("LightProtoCodec.computeVarInt64Size(%s)", name);
        } else if (field.getProtoType().equals("uint64")) {
            return String.format("LightProtoCodec.computeVarInt64Size(%s)", name);
        } else if (field.getProtoType().equals("fixed32")) {
            return "4";
        } else if (field.getProtoType().equals("fixed64")) {
            return "8";
        } else if (field.getProtoType().equals("sfixed32")) {
            return "4";
        } else if (field.getProtoType().equals("sfixed64")) {
            return "8";
        } else if (field.getProtoType().equals("bool")) {
            return "1";
        } else if (field.getProtoType().equals("double")) {
            return "8";
        } else if (field.getProtoType().equals("float")) {
            return "4";
        } else {
            throw new IllegalArgumentException("Failed to write serializer for field: " + field.getProtoType());
        }
    }

    static String typeTag(Field<?> field) {
        if (field.isEnumField()) {
            return "LightProtoCodec.WIRETYPE_VARINT";
        } else {
            return typeToTag.get(field.getProtoType());
        }
    }

    public void getter(PrintWriter w) {
        w.format("        public %s %s() {\n", field.getJavaType(), camelCase("get", field.getName()));
        if (!field.isDefaultValueSet()) {
            w.format("            if (!%s()) {\n", camelCase("has", ccName));
            w.format("                throw new IllegalStateException(\"Field '%s' is not set\");\n", field.getName());
            w.format("            }\n");
        }
        w.format("            return %s;\n", ccName);
        w.format("        }\n");
    }

    @Override
    public void parse(PrintWriter w) {
        w.format("%s = %s;\n", ccName, parseNumber(field));
    }

    @Override
    public void serialize(PrintWriter w) {
        w.format("LightProtoCodec.writeVarInt(_b, %s);\n", tagName());
        serializeNumber(w, field, ccName);
    }

    @Override
    public void setter(PrintWriter w, String enclosingType) {
        w.format("public %s %s(%s %s) {\n", enclosingType, camelCase("set", field.getName()), field.getJavaType(), camelCase(field.getName()));
        w.format("    this.%s = %s;\n", camelCase(field.getName()), camelCase(field.getName()));
        w.format("    _bitField%d |= %s;\n", bitFieldIndex(), fieldMask());
        w.format("    _cachedSize = -1;\n");
        w.format("    return this;\n");
        w.format("}\n");
    }

    @Override
    public void declaration(PrintWriter w) {
        if (field.isDefaultValueSet()) {
            w.format("private %s %s = %s;\n", field.getJavaType(), ccName, field.getDefaultValueAsString());
        } else {
            w.format("private %s %s;\n", field.getJavaType(), ccName);
        }
    }

    @Override
    public void copy(PrintWriter w) {
        w.format("%s(_other.%s);\n", camelCase("set", ccName), ccName);
    }

    @Override
    public void clear(PrintWriter w) {
        if (field.isDefaultValueSet()) {
            w.format("%s = %s;\n", ccName, field.getDefaultValueAsString());
        }
    }

    @Override
    public void serializedSize(PrintWriter w) {
        w.format("_size += LightProtoCodec.computeVarIntSize(%s);\n", tagName());
        w.format("_size += %s;\n", serializedSizeOfNumber(field, ccName));
    }

    @Override
    protected String typeTag() {
        return typeTag(field);
    }
}
