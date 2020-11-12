package io.lightproto.generator;

import io.protostuff.parser.MessageField;

import java.io.PrintWriter;

import static io.lightproto.generator.Util.camelCase;

public class LightProtoRepeatedMessageField extends LightProtoField<MessageField> {

    protected final String pluralName;
    protected final String singularName;

    public LightProtoRepeatedMessageField(MessageField field, int index) {
        super(field, index);
        this.pluralName = Util.plural(ccName);
        this.singularName = Util.singular(ccName);
    }

    @Override
    public void declaration(PrintWriter w) {
        w.format("private java.util.List<%s> %s = null;\n", field.getJavaType(), pluralName);
        w.format("private int _%sCount = 0;\n", pluralName);
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
        w.format("    return %s.get(idx);\n", pluralName);
        w.format("}\n");

        w.format("public java.util.List<%s> %s() {\n", field.getJavaType(), camelCase("get", pluralName, "list"));
        w.format("    if (_%sCount == 0) {\n", pluralName);
        w.format("        return java.util.Collections.emptyList();\n");
        w.format("    } else {\n");
        w.format("        return %s.subList(0, _%sCount);\n", pluralName, pluralName);
        w.format("    }\n");
        w.format("}\n");
    }

    @Override
    public void parse(PrintWriter w) {
        w.format("int _%sSize = LightProtoCodec.readVarInt(_buffer);\n", ccName);
        w.format("%s().parseFrom(_buffer, _%sSize);\n", camelCase("add", singularName), ccName);
    }

    @Override
    public void serialize(PrintWriter w) {
        w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
        w.format("    %s _item = %s.get(i);\n", field.getJavaType(), pluralName);
        w.format("    LightProtoCodec.writeVarInt(_b, %s);\n", tagName());
        w.format("    LightProtoCodec.writeVarInt(_b, _item.getSerializedSize());\n");
        w.format("    _item.writeTo(_b);\n");
        w.format("}\n");
    }

    @Override
    public void setter(PrintWriter w) {
        w.format("public %s %s() {\n", field.getJavaType(), camelCase("add", singularName));
        w.format("    if (%s == null) {\n", pluralName);
        w.format("        %s = new java.util.ArrayList<%s>();\n", pluralName, field.getJavaType());
        w.format("    }\n");
        w.format("    if (%s.size() == _%sCount) {\n", pluralName, pluralName);
        w.format("        %s.add(new %s());\n", pluralName, field.getJavaType());
        w.format("    }\n");
        w.format("    _bitField%d |= %s;\n", bitFieldIndex(), fieldMask());
        w.format("    _cachedSize = -1;\n");
        w.format("    return %s.get(_%sCount++);\n", pluralName, pluralName);
        w.format("}\n");
    }

    @Override
    public void serializedSize(PrintWriter w) {
        String tmpName = camelCase("_msgSize", field.getName());

        w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
        w.format("     %s _item = %s.get(i);\n", field.getJavaType(), pluralName);
        w.format("     _size += LightProtoCodec.computeVarIntSize(%s);\n", tagName());
        w.format("     int %s = _item.getSerializedSize();\n", tmpName);
        w.format("     _size += LightProtoCodec.computeVarIntSize(%s) + %s;\n", tmpName, tmpName);
        w.format("}\n");
    }

    @Override
    public void clear(PrintWriter w) {
        w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
        w.format("    %s.get(i).clear();\n", pluralName);
        w.format("}\n");
        w.format("_%sCount = 0;\n", pluralName);
    }

    @Override
    protected String typeTag() {
        return "LightProtoCodec.WIRETYPE_LENGTH_DELIMITED";
    }
}
