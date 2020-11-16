package com.splunk.lightproto.generator;

import io.protostuff.parser.Field;

import java.io.PrintWriter;

public class LightProtoRepeatedBytesField extends LightProtoField<Field.Bytes> {

    protected final String pluralName;
    protected final String singularName;

    public LightProtoRepeatedBytesField(Field.Bytes field, int index) {
        super(field, index);
        this.pluralName = Util.plural(ccName);
        this.singularName = Util.singular(ccName);
    }

    @Override
    public void declaration(PrintWriter w) {
        w.format("private java.util.List<LightProtoCodec.BytesHolder> %s = null;\n", pluralName);
        w.format("private int _%sCount = 0;\n", pluralName);
    }

    @Override
    public void parse(PrintWriter w) {
        w.format("LightProtoCodec.BytesHolder _%sBh = _%sBytesHolder();\n", ccName, Util.camelCase("new", singularName));
        w.format("_%sBh.len = LightProtoCodec.readVarInt(_buffer);\n", ccName);
        w.format("_%sBh.idx = _buffer.readerIndex();\n", ccName);
        w.format("_buffer.skipBytes(_%sBh.len);\n", ccName);
    }

    @Override
    public void getter(PrintWriter w) {
        w.format("public int %s() {\n", Util.camelCase("get", pluralName, "count"));
        w.format("    return _%sCount;\n", pluralName);
        w.format("}\n");

        w.format("public int %s(int idx) {\n", Util.camelCase("get", singularName, "size", "at"));
        w.format("    if (idx < 0 || idx >= _%sCount) {\n", pluralName);
        w.format("        throw new IndexOutOfBoundsException(\"Index \" + idx + \" is out of the list size (\" + _%sCount + \") for field '%s'\");\n", pluralName, field.getName());
        w.format("    }\n");
        w.format("    return %s.get(idx).len;\n", pluralName);
        w.format("}\n");


        w.format("public byte[] %s(int idx) {\n", Util.camelCase("get", singularName, "at"));
        w.format("    io.netty.buffer.ByteBuf _b = %s(idx);\n", Util.camelCase("get", singularName, "slice", "at"));
        w.format("    byte[] res = new byte[_b.readableBytes()];\n");
        w.format("    _b.getBytes(0, res);\n", ccName);
        w.format("    return res;\n");
        w.format("}\n");

        w.format("public io.netty.buffer.ByteBuf %s(int idx) {\n", Util.camelCase("get", singularName, "slice", "at"));
        w.format("    if (idx < 0 || idx >= _%sCount) {\n", pluralName);
        w.format("        throw new IndexOutOfBoundsException(\"Index \" + idx + \" is out of the list size (\" + _%sCount + \") for field '%s'\");\n", pluralName, field.getName());
        w.format("    }\n");
        w.format("    LightProtoCodec.BytesHolder _bh = %s.get(idx);\n", pluralName);
        w.format("    if (_bh.b == null) {\n");
        w.format("        return _parsedBuffer.slice(_bh.idx, _bh.len);\n");
        w.format("    } else {\n");
        w.format("        return _bh.b.slice(0, _bh.len);\n");
        w.format("    }\n");
        w.format("}\n");
    }

    @Override
    public void serialize(PrintWriter w) {
        w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
        w.format("    LightProtoCodec.BytesHolder _bh = %s.get(i);\n", pluralName);
        w.format("    LightProtoCodec.writeVarInt(_b, %s);\n", tagName());
        w.format("    LightProtoCodec.writeVarInt(_b, _bh.len);\n");
        w.format("    if (_bh.idx == -1) {\n");
        w.format("        _bh.b.getBytes(0, _b, _bh.len);\n");
        w.format("    } else {\n");
        w.format("        _parsedBuffer.getBytes(_bh.idx, _b, _bh.len);\n");
        w.format("    }\n");
        w.format("}\n");
    }

    @Override
    public void setter(PrintWriter w, String enclosingType) {
        w.format("public void %s(byte[] %s) {\n", Util.camelCase("add", singularName), singularName);
        w.format("    %s(io.netty.buffer.Unpooled.wrappedBuffer(%s));\n", Util.camelCase("add", singularName), singularName);
        w.format("}\n");

        w.format("public void %s(io.netty.buffer.ByteBuf %s) {\n", Util.camelCase("add", singularName), singularName);
        w.format("    if (%s == null) {\n", pluralName);
        w.format("        %s = new java.util.ArrayList<LightProtoCodec.BytesHolder>();\n", pluralName);
        w.format("    }\n");
        w.format("    LightProtoCodec.BytesHolder _bh = _%sBytesHolder();\n", Util.camelCase("new", singularName));
        w.format("    _bitField%d |= %s;\n", bitFieldIndex(), fieldMask());
        w.format("    _cachedSize = -1;\n");
        w.format("    _bh.b = %s;\n", singularName);
        w.format("    _bh.idx = -1;\n");
        w.format("    _bh.len = %s.readableBytes();\n", singularName);
        w.format("}\n");


        w.format("private LightProtoCodec.BytesHolder _%sBytesHolder() {\n", Util.camelCase("new", singularName));
        w.format("    if (%s == null) {\n", pluralName);
        w.format("         %s = new java.util.ArrayList<LightProtoCodec.BytesHolder>();\n", pluralName);
        w.format("    }\n");
        w.format("    LightProtoCodec.BytesHolder _bh;\n");
        w.format("    if (%s.size() == _%sCount) {\n", pluralName, pluralName);
        w.format("        _bh = new LightProtoCodec.BytesHolder();\n");
        w.format("        %s.add(_bh);\n", pluralName);
        w.format("    } else {\n");
        w.format("        _bh = %s.get(_%sCount - 1);\n", pluralName, pluralName);
        w.format("    }\n");
        w.format("    _%sCount++;\n", pluralName);
        w.format("    return _bh;\n");
        w.format("}\n");
    }

    @Override
    public void copy(PrintWriter w) {
        w.format("for (int i = 0; i < _other.%s(); i++) {\n", Util.camelCase("get", pluralName, "count"));
        w.format("    %s(_other.%s(i));\n", Util.camelCase("add", singularName), Util.camelCase("get", singularName, "at"));
        w.format("}\n");
    }

    @Override
    public void serializedSize(PrintWriter w) {
        w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
        w.format("    LightProtoCodec.BytesHolder _bh = %s.get(i);\n", pluralName);
        w.format("    _size += LightProtoCodec.computeVarIntSize(%s);\n", tagName());
        w.format("    _size += LightProtoCodec.computeVarIntSize(_bh.len) + _bh.len;\n");
        w.format("}\n");
    }

    @Override
    public void clear(PrintWriter w) {
        w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
        w.format("    LightProtoCodec.BytesHolder _bh = %s.get(i);\n", pluralName);
        w.format("    _bh.b = null;\n");
        w.format("    _bh.idx = -1;\n");
        w.format("    _bh.len = -1;\n");
        w.format("}\n");
        w.format("_%sCount = 0;\n", pluralName);
    }

    @Override
    protected String typeTag() {
        return "LightProtoCodec.WIRETYPE_LENGTH_DELIMITED";
    }
}
