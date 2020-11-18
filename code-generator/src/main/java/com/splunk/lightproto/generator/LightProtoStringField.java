package com.splunk.lightproto.generator;

import io.protostuff.parser.Field;

import java.io.PrintWriter;

import static com.splunk.lightproto.generator.Util.camelCase;

public class LightProtoStringField extends LightProtoField<Field.String> {

    public LightProtoStringField(Field.String field, int index) {
        super(field, index);
    }

    @Override
    public void declaration(PrintWriter w) {
        if (field.isDefaultValueSet()) {
            w.format("private String %s = \"%s\";\n", ccName, field.getDefaultValue());
        } else {
            w.format("private String %s;\n", ccName);
        }
        w.format("private int _%sBufferIdx = -1;\n", ccName);
        w.format("private int _%sBufferLen = -1;\n", ccName);
    }

    @Override
    public void setter(PrintWriter w, String enclosingType) {
        w.format("public %s %s(%s %s) {\n", enclosingType, Util.camelCase("set", field.getName()), field.getJavaType(), camelCase(field.getName()));
        w.format("    this.%s = %s;\n", camelCase(field.getName()), camelCase(field.getName()));
        w.format("    _bitField%d |= %s;\n", bitFieldIndex(), fieldMask());
        w.format("    _%sBufferIdx = -1;\n", ccName);
        w.format("    _%sBufferLen = LightProtoCodec.computeStringUTF8Size(%s);\n", ccName, ccName);
        w.format("    _cachedSize = -1;\n");
        w.format("    return this;\n");
        w.format("}\n");
    }

    @Override
    public void copy(PrintWriter w) {
        w.format("%s(_other.%s());\n", Util.camelCase("set", ccName), Util.camelCase("get", ccName));
    }

    @Override
    public void getter(PrintWriter w) {
        w.format("public %s %s() {\n", field.getJavaType(), Util.camelCase("get", field.getName()));
        if (!field.isDefaultValueSet()) {
            w.format("    if (!%s()) {\n", Util.camelCase("has", ccName));
            w.format("        throw new IllegalStateException(\"Field '%s' is not set\");\n", field.getName());
            w.format("    }\n");
        }
        w.format("    if (%s == null) {\n", camelCase(field.getName()));
        w.format("        %s = LightProtoCodec.readString(_parsedBuffer, _%sBufferIdx, _%sBufferLen);\n", ccName, ccName, ccName);
        w.format("    }\n");
        w.format("    return %s;\n", camelCase(field.getName()));
        w.format("}\n");
    }

    @Override
    public void clear(PrintWriter w) {
        w.format("%s = %s;\n", ccName, field.getDefaultValue());
        w.format("_%sBufferIdx = -1;\n", ccName);
        w.format("_%sBufferLen = -1;\n", ccName);
    }

    @Override
    public void serializedSize(PrintWriter w) {
        w.format("_size += %s_SIZE;\n", tagName());
        w.format("_size += LightProtoCodec.computeVarIntSize(_%sBufferLen);\n", ccName);
        w.format("_size += _%sBufferLen;\n", ccName);
    }

    @Override
    public void serialize(PrintWriter w) {
        w.format("LightProtoCodec.writeVarInt(_b, %s);\n", tagName());
        w.format("LightProtoCodec.writeVarInt(_b, _%sBufferLen);\n", ccName);
        w.format("if (_%sBufferIdx == -1) {\n", ccName);
        w.format("    LightProtoCodec.writeString(_b, %s, _%sBufferLen);\n", ccName, ccName);
        w.format("} else {\n");
        w.format("    _parsedBuffer.getBytes(_%sBufferIdx, _b, _%sBufferLen);\n", ccName, ccName);
        w.format("}\n");
    }

    @Override
    public void parse(PrintWriter w) {
        w.format("_%sBufferLen = LightProtoCodec.readVarInt(_buffer);\n", ccName);
        w.format("_%sBufferIdx = _buffer.readerIndex();\n", ccName);
        w.format("_buffer.skipBytes(_%sBufferLen);\n", ccName);
    }

    @Override
    protected String typeTag() {
        return "LightProtoCodec.WIRETYPE_LENGTH_DELIMITED";
    }
}
