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
package com.splunk.lightproto.generator;

import io.protostuff.parser.MessageField;

import java.io.PrintWriter;

public class LightProtoMessageField extends LightProtoField<MessageField> {

    public LightProtoMessageField(MessageField field, int index) {
        super(field, index);
    }

    @Override
    public void declaration(PrintWriter w) {
        w.format("private %s %s;\n", field.getJavaType(), ccName);
    }

    @Override
    public void setter(PrintWriter w, String enclosingType) {
        w.format("public %s %s() {\n", field.getJavaType(), Util.camelCase("set", ccName));
        w.format("    if (%s == null) {\n", ccName);
        w.format("        %s = new %s();\n", ccName, field.getJavaType());
        w.format("    }\n");
        w.format("    _bitField%d |= %s;\n", bitFieldIndex(), fieldMask());
        w.format("    _cachedSize = -1;\n");
        w.format("    return %s;\n", ccName);
        w.format("}\n");
    }

    @Override
    public void copy(PrintWriter w) {
        w.format("%s().copyFrom(_other.%s);\n", Util.camelCase("set", ccName), ccName);
    }

    public void getter(PrintWriter w) {
        w.format("public %s %s() {\n", field.getJavaType(), Util.camelCase("get", field.getName()));
        w.format("    if (!%s()) {\n", Util.camelCase("has", ccName));
        w.format("        throw new IllegalStateException(\"Field '%s' is not set\");\n", field.getName());
        w.format("    }\n");
        w.format("    return %s;\n", ccName);
        w.format("}\n");
    }

    @Override
    public void parse(PrintWriter w) {
        w.format("int %sSize = LightProtoCodec.readVarInt(_buffer);\n", ccName);
        w.format("%s().parseFrom(_buffer, %sSize);\n", Util.camelCase("set", ccName), ccName);
    }

    @Override
    public void serializedSize(PrintWriter w) {
        String tmpName = Util.camelCase("_msgSize", ccName);
        w.format("_size += LightProtoCodec.computeVarIntSize(%s);\n", tagName());
        w.format("int %s = %s.getSerializedSize();\n", tmpName, ccName);
        w.format("_size += LightProtoCodec.computeVarIntSize(%s) + %s;\n", tmpName, tmpName);
    }

    @Override
    public void serialize(PrintWriter w) {
        w.format("LightProtoCodec.writeVarInt(_b, %s);\n", tagName());
        w.format("LightProtoCodec.writeVarInt(_b, %s.getSerializedSize());\n", ccName);
        w.format("%s.writeTo(_b);\n", ccName);
    }

    @Override
    public void clear(PrintWriter w) {
        w.format("if (%s()){\n", Util.camelCase("has", ccName));
        w.format("    %s.clear();\n", ccName);
        w.format("}\n");
    }

    @Override
    protected String typeTag() {
        return "LightProtoCodec.WIRETYPE_LENGTH_DELIMITED";
    }
}
