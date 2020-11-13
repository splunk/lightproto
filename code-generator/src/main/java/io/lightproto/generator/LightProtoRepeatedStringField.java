package io.lightproto.generator;

import io.protostuff.parser.Field;
import io.protostuff.parser.MessageField;

import java.io.PrintWriter;

import static io.lightproto.generator.Util.camelCase;

public class LightProtoRepeatedStringField extends LightProtoField<Field.String> {

    protected final String pluralName;
    protected final String singularName;

    public LightProtoRepeatedStringField(Field.String field, int index) {
        super(field, index);
        this.pluralName = Util.plural(ccName);
        this.singularName = Util.singular(ccName);
    }

    @Override
    public void declaration(PrintWriter w) {
        w.format("private java.util.List<StringHolder> %s = null;\n", pluralName);
        w.format("private int _%sCount = 0;\n", pluralName);
    }

    @Override
    public void parse(PrintWriter w) {
        w.format("StringHolder _%sSh = _%sStringHolder();\n", ccName, camelCase("new", singularName));
        w.format("_%sSh.len = LightProtoCodec.readVarInt(_buffer);\n", ccName);
        w.format("_%sSh.idx = _buffer.readerIndex();\n", ccName);
        w.format("_buffer.skipBytes(_%sSh.len);\n", ccName);
    }

    @Override
    public void getter(PrintWriter w) {
        w.format("public int %s() {\n", camelCase("get", pluralName, "count"));
        w.format("    return _%sCount;\n", pluralName);
        w.format("}\n");
        w.format("public %s %s(int idx) {\n", field.getJavaType(), camelCase("get", singularName, "at"));
        w.format("    if (idx < 0 || idx >= _%sCount) {\n", pluralName);
        w.format("        throw new IndexOutOfBoundsException(\"Index \" + idx + \" is out of the list size (\" + _%sCount + \") for field '%s'\");\n", pluralName, field.getName());
        w.format("    }\n");
        w.format("    StringHolder _sh = %s.get(idx);\n", pluralName);
        w.format("    if (_sh.s == null) {\n");
        w.format("        _sh.s = LightProtoCodec.readString(_parsedBuffer, _sh.idx, _sh.len);\n");
        w.format("    }\n");
        w.format("    return _sh.s;\n");
        w.format("}\n");
    }

    @Override
    public void serialize(PrintWriter w) {
        w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
        w.format("    StringHolder _sh = %s.get(i);\n", pluralName);
        w.format("    LightProtoCodec.writeVarInt(_b, %s);\n", tagName());
        w.format("    LightProtoCodec.writeVarInt(_b, _sh.len);\n");
        w.format("    if (_sh.idx == -1) {\n");
        w.format("         LightProtoCodec.writeString(_b, _sh.s, _sh.len);\n");
        w.format("    } else {\n");
        w.format("        _parsedBuffer.getBytes(_sh.idx, _b, _sh.len);\n");
        w.format("    }\n");
        w.format("}\n");
    }

    @Override
    public void copy(PrintWriter w) {
        w.format("for (int i = 0; i < _other.%s(); i++) {\n", camelCase("get", pluralName, "count"));
        w.format("    %s(_other.%s(i));\n", camelCase("add", singularName), camelCase("get", singularName, "at"));
        w.format("}\n");
    }

    @Override
    public void setter(PrintWriter w, String enclosingType) {
        w.format("public void %s(String %s) {\n", camelCase("add", singularName), singularName);
        w.format("    if (%s == null) {\n", pluralName);
        w.format("        %s = new java.util.ArrayList<StringHolder>();\n", pluralName);
        w.format("    }\n");
        w.format("    StringHolder _sh = _%sStringHolder();\n", camelCase("new", singularName));
        w.format("    _bitField%d |= %s;\n", bitFieldIndex(), fieldMask());
        w.format("    _cachedSize = -1;\n");
        w.format("    _sh.s = %s;\n", singularName);
        w.format("    _sh.idx = -1;\n");
        w.format("    _sh.len = LightProtoCodec.computeStringUTF8Size(_sh.s);\n");
        w.format("}\n");

        w.format("public %s %s(Iterable<String> %s) {\n", enclosingType, camelCase("addAll", pluralName), pluralName);
        w.format("    for (String _s : %s) {\n", pluralName);
        w.format("        %s(_s);\n", camelCase("add", singularName));
        w.format("    }\n");
        w.format("    return this;\n");
        w.format("}\n");


        w.format("private StringHolder _%sStringHolder() {\n", camelCase("new", singularName));
        w.format("    if (%s == null) {\n", pluralName);
        w.format("         %s = new java.util.ArrayList<StringHolder>();\n", pluralName);
        w.format("    }\n");
        w.format("    StringHolder _sh;\n");
        w.format("    if (%s.size() == _%sCount) {\n", pluralName, pluralName);
        w.format("        _sh = new StringHolder();\n");
        w.format("        %s.add(_sh);\n", pluralName);
        w.format("    } else {\n");
        w.format("        _sh = %s.get(_%sCount - 1);\n", pluralName, pluralName);
        w.format("    }\n");
        w.format("    _%sCount++;\n", pluralName);
        w.format("    return _sh;\n");
        w.format("}\n");
    }

    @Override
    public void serializedSize(PrintWriter w) {
        w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
        w.format("    StringHolder _sh = %s.get(i);\n", pluralName);
        w.format("    _size += LightProtoCodec.computeVarIntSize(%s);\n", tagName());
        w.format("    _size += LightProtoCodec.computeVarIntSize(_sh.len) + _sh.len;\n");
        w.format("}\n");
    }

    @Override
    public void clear(PrintWriter w) {
        w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
        w.format("    StringHolder _sh = %s.get(i);\n", pluralName);
        w.format("    _sh.s = null;\n");
        w.format("    _sh.idx = -1;\n");
        w.format("    _sh.len = -1;\n");
        w.format("}\n");
        w.format("_%sCount = 0;\n", pluralName);
    }

    @Override
    protected String typeTag() {
        return "LightProtoCodec.WIRETYPE_LENGTH_DELIMITED";
    }
}
