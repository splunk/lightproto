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

import io.protostuff.parser.Field;

import java.io.PrintWriter;

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
    public void copy(PrintWriter w) {
        w.format("%s(_other.%s());\n", Util.camelCase("set", ccName), Util.camelCase("get", ccName));
    }

    @Override
    public void setter(PrintWriter w, String enclosingType) {
        w.format("public %s %s(byte[] %s) {\n", enclosingType, Util.camelCase("set", ccName), ccName);
        w.format("    %s(io.netty.buffer.Unpooled.wrappedBuffer(%s));\n", Util.camelCase("set", ccName), ccName);
        w.format("    return this;\n");
        w.format("}\n");

        w.format("public %s %s(io.netty.buffer.ByteBuf %s) {\n", enclosingType, Util.camelCase("set", ccName), ccName);
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
        w.format("public int %s() {\n", Util.camelCase("get", ccName, "size"));
        w.format("    if (!%s()) {\n", Util.camelCase("has", ccName));
        w.format("        throw new IllegalStateException(\"Field '%s' is not set\");\n", field.getName());
        w.format("    }\n");
        w.format("    return _%sLen;\n", ccName);
        w.format("}\n");

        w.format("public byte[] %s() {\n", Util.camelCase("get", ccName));
        w.format("    io.netty.buffer.ByteBuf _b = %s();\n", Util.camelCase("get", ccName, "slice"));
        w.format("    byte[] res = new byte[_b.readableBytes()];\n");
        w.format("    _b.getBytes(0, res);\n", ccName);
        w.format("    return res;\n");
        w.format("}\n");

        w.format("public io.netty.buffer.ByteBuf %s() {\n", Util.camelCase("get", ccName, "slice"));
        w.format("    if (!%s()) {\n", Util.camelCase("has", ccName));
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
        w.format("_size += %s_SIZE;\n", tagName());
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
