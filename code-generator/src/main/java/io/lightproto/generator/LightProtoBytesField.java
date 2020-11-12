package io.lightproto.generator;

import io.protostuff.parser.Field;

import java.io.PrintWriter;

import static io.lightproto.generator.Util.camelCase;

public class LightProtoBytesField extends LightProtoField<Field.Bytes> {

    public LightProtoBytesField(Field.Bytes field, int index) {
        super(field, index);
    }

    @Override
    public void declaration(PrintWriter w) {
        w.format("private io.netty.buffer.ByteBuf %s = null;\n", ccName);
        w.format("private int _%sIdx = -1;\n", ccName);
        w.format("private int _%sLen = -1;\n", ccName);
    }

    @Override
    public void parse(PrintWriter w) {
        w.format("_%sLen = LightProtoCodec.readVarInt(_buffer);\n", ccName);
        w.format("_%sIdx = _buffer.readerIndex();\n", ccName);
        w.format("_buffer.skipBytes(_%sLen);\n", ccName);
    }

    @Override
    public void setter(PrintWriter w, String enclosingType) {
        w.format("public %s %s(byte[] %s) {\n", enclosingType, camelCase("set", ccName), ccName);
        w.format("    %s(io.netty.buffer.Unpooled.wrappedBuffer(%s));\n", camelCase("set", ccName), ccName);
        w.format("    return this;\n");
        w.format("}\n");

        w.format("public %s %s(io.netty.buffer.ByteBuf %s) {\n", enclosingType, camelCase("set", ccName), ccName);
        w.format("    this.%s = %s;\n", ccName, ccName);
        w.format("    _bitField%d |= %s;\n", bitFieldIndex(), fieldMask());
        w.format("    _%sIdx = -1;\n", ccName);
        w.format("    _%sLen = %s.readableBytes();\n", ccName, ccName);
        w.format("    _cachedSize = -1;\n");
        w.format("    return this;\n");
        w.format("}\n");
    }

    @Override
    public void getter(PrintWriter w) {
        w.format("public int %s() {\n", camelCase("get", ccName, "size"));
        w.format("    if (!%s()) {\n", camelCase("has", ccName));
        w.format("        throw new IllegalStateException(\"Field '%s' is not set\");\n", field.getName());
        w.format("    }\n");
        w.format("    return _%sLen;\n", ccName);
        w.format("}\n");

        w.format("public byte[] %s() {\n", camelCase("get", ccName));
        w.format("    io.netty.buffer.ByteBuf _b = %s();\n", camelCase("get", ccName, "slice"));
        w.format("    byte[] res = new byte[_b.readableBytes()];\n");
        w.format("    _b.getBytes(0, res);\n", ccName);
        w.format("    return res;\n");
        w.format("}\n");

        w.format("public io.netty.buffer.ByteBuf %s() {\n", camelCase("get", ccName, "slice"));
        w.format("    if (!%s()) {\n", camelCase("has", ccName));
        w.format("        throw new IllegalStateException(\"Field '%s' is not set\");\n", field.getName());
        w.format("    }\n");
        w.format("    if (%s == null) {\n", ccName);
        w.format("        return _parsedBuffer.slice(_%sIdx, _%sLen);\n", ccName, ccName);
        w.format("    } else {\n");
        w.format("        return %s.slice(0, _%sLen);\n", ccName, ccName);
        w.format("    }\n");
        w.format("}\n");
    }

    @Override
    public void clear(PrintWriter w) {
        w.format("%s = null;\n", ccName);
        w.format("_%sIdx = -1;\n", ccName);
        w.format("_%sLen = -1;\n", ccName);
    }

    @Override
    public void serializedSize(PrintWriter w) {
        w.format("_size += LightProtoCodec.computeVarIntSize(%s);\n", tagName());
        w.format("_size += LightProtoCodec.computeVarIntSize(_%sLen) + _%sLen;\n", ccName, ccName);
    }

    @Override
    public void serialize(PrintWriter w) {
        w.format("LightProtoCodec.writeVarInt(_b, %s);\n", tagName());
        w.format("LightProtoCodec.writeVarInt(_b, _%sLen);\n", ccName);

        w.format("if (_%sIdx == -1) {\n", ccName);
        w.format("    _b.writeBytes(%s);\n", ccName);
        w.format("} else {\n");
        w.format("    _parsedBuffer.getBytes(_%sIdx, _b, _%sLen);\n", ccName, ccName);
        w.format("}\n");
    }


    @Override
    protected String typeTag() {
        return "LightProtoCodec.WIRETYPE_LENGTH_DELIMITED";
    }
}
