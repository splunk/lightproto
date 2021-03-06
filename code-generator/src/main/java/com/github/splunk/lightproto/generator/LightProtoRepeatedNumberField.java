/**
 * Copyright 2020 Splunk Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.splunk.lightproto.generator;

import io.protostuff.parser.Field;

import java.io.PrintWriter;

public class LightProtoRepeatedNumberField extends LightProtoAbstractRepeated<Field<?>> {

    protected final String pluralName;
    protected final String singularName;

    public LightProtoRepeatedNumberField(Field<?> field, int index) {
        super(field, index);
        this.pluralName = Util.plural(ccName);
        this.singularName = Util.singular(ccName);
    }

    @Override
    public void declaration(PrintWriter w) {
        w.format("private %s[] %s = null;\n", field.getJavaType(), pluralName);
        w.format("private int _%sCount = 0;\n", pluralName);
    }

    @Override
    public void parse(PrintWriter w) {
        w.format("%s(%s);\n", Util.camelCase("add", singularName), LightProtoNumberField.parseNumber(field));
    }

    public void parsePacked(PrintWriter w) {
        w.format("int _%s = LightProtoCodec.readVarInt(_buffer);\n", Util.camelCase(singularName, "size"));
        w.format("int _%s = _buffer.readerIndex() + _%s;\n", Util.camelCase(singularName, "endIdx"), Util.camelCase(singularName, "size"));
        w.format("while (_buffer.readerIndex() < _%s) {\n", Util.camelCase(singularName, "endIdx"));
        w.format("%s(%s);\n", Util.camelCase("add", singularName), LightProtoNumberField.parseNumber(field));
        w.format("}\n");
    }

    @Override
    public void getter(PrintWriter w) {
        w.format("private static final int %s_PACKED = (%s << LightProtoCodec.TAG_TYPE_BITS) | LightProtoCodec.WIRETYPE_LENGTH_DELIMITED;\n", tagName(), fieldNumber());
        w.format("public int %s() {\n", Util.camelCase("get", pluralName, "count"));
        w.format("    return _%sCount;\n", pluralName);
        w.format("}\n");
        w.format("public %s %s(int idx) {\n", field.getJavaType(), Util.camelCase("get", singularName, "at"));
        w.format("    if (idx < 0 || idx >= _%sCount) {\n", pluralName);
        w.format("        throw new IndexOutOfBoundsException(\"Index \" + idx + \" is out of the list size (\" + _%sCount + \") for field '%s'\");\n", pluralName, field.getName());
        w.format("    }\n");
        w.format("    return %s[idx];\n", pluralName);
        w.format("}\n");
    }

    @Override
    public void serialize(PrintWriter w) {
        if (field.getOption("packed") == Boolean.TRUE) {
            w.format("    LightProtoCodec.writeVarInt(_b, %s_PACKED);\n", tagName());
            w.format("    int _%sSize = 0;\n", pluralName);
            w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
            w.format("    %s _item = %s[i];\n", field.getJavaType(), pluralName);
            w.format("    _%sSize += %s;\n", pluralName, LightProtoNumberField.serializedSizeOfNumber(field, "_item"));
            w.format("}\n");
            w.format("    LightProtoCodec.writeVarInt(_b, _%sSize);\n", pluralName);
            w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
            w.format("    %s _item = %s[i];\n", field.getJavaType(), pluralName);
            LightProtoNumberField.serializeNumber(w, field, "_item");
            w.format("}\n");
        } else {
            w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
            w.format("    %s _item = %s[i];\n", field.getJavaType(), pluralName);
            w.format("    LightProtoCodec.writeVarInt(_b, %s);\n", tagName());
            LightProtoNumberField.serializeNumber(w, field, "_item");
            w.format("}\n");
        }
    }

    @Override
    public void setter(PrintWriter w, String enclosingType) {
        w.format("public void %s(%s %s) {\n", Util.camelCase("add", singularName), field.getJavaType(), singularName);
        w.format("    if (%s == null) {\n", pluralName);
        w.format("        %s = new %s[4];\n", pluralName, field.getJavaType());
        w.format("    }\n");
        w.format("    if (%s.length == _%sCount) {\n", pluralName, pluralName);
        w.format("        %s = java.util.Arrays.copyOf(%s, _%sCount * 2);\n", pluralName, pluralName, pluralName);
        w.format("    }\n");
        w.format("    _cachedSize = -1;\n");
        w.format("    %s[_%sCount++] = %s;\n", pluralName, pluralName, singularName);
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

        if (field.getOption("packed") == Boolean.TRUE) {
            w.format("    _size += %s_SIZE;\n", tagName());
            w.format("    int _%sSize = 0;\n", pluralName);
            w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
            w.format("    %s _item = %s[i];\n", field.getJavaType(), pluralName);
            w.format("    _%sSize += %s;\n", pluralName, LightProtoNumberField.serializedSizeOfNumber(field, "_item"));
            w.format("}\n");
            w.format("    _size += LightProtoCodec.computeVarIntSize(_%sSize);\n", pluralName);
            w.format("    _size += _%sSize;\n", pluralName);
        } else {
            w.format("for (int i = 0; i < _%sCount; i++) {\n", pluralName);
            w.format("    %s _item = %s[i];\n", field.getJavaType(), pluralName);
            w.format("    _size += %s_SIZE;\n", tagName());
            w.format("    _size += %s;\n", LightProtoNumberField.serializedSizeOfNumber(field, "_item"));
            w.format("}\n");
        }
    }

    @Override
    public void clear(PrintWriter w) {
        w.format("_%sCount = 0;\n", pluralName);
    }

    @Override
    protected String typeTag() {
        return LightProtoNumberField.typeTag(field);
    }

}
