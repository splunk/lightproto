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
package com.github.splunk.lightproto.tests;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LightProtoCodecTest {

    private byte[] b = new byte[4096];
    private ByteBuf bb = Unpooled.wrappedBuffer(b);

    @BeforeEach
    public void setup() {
        bb.clear();
        Arrays.fill(b, (byte) 0);
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1000, -100, -2, -1, 0, 1, 10, 100, 1000, (int) 1e4, (int) 1e5, (int) 1e7, Integer.MAX_VALUE})
    public void testVarInt(int i) throws Exception {
        LightProtoCodec.writeVarInt(bb, i);

        CodedInputStream is = CodedInputStream.newInstance(b);
        int res = is.readRawVarint32();
        assertEquals(i, res);

        res = LightProtoCodec.readVarInt(bb);
        assertEquals(i, res);

        assertEquals(CodedOutputStream.computeInt32SizeNoTag(i), LightProtoCodec.computeVarIntSize(i));
    }

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -10000000, -100, -2, -1, 0, 1, 10, 100, 10000000, (long) 2e18, (long) 2e32, (long) 2e43, (long) 2e57, Long.MAX_VALUE})
    public void testVarInt64(long i) throws Exception {
        LightProtoCodec.writeVarInt64(bb, i);

        CodedInputStream is = CodedInputStream.newInstance(b);
        long res = is.readRawVarint64();
        assertEquals(i, res);

        res = LightProtoCodec.readVarInt64(bb);
        assertEquals(i, res);

        assertEquals(CodedOutputStream.computeInt64SizeNoTag(i), LightProtoCodec.computeVarInt64Size(i));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1000, -100, -2, -1, 0, 1, 10, 100, 1000, Integer.MAX_VALUE})
    public void testSignedVarInt(int i) throws Exception {
        LightProtoCodec.writeSignedVarInt(bb, i);

        CodedInputStream is = CodedInputStream.newInstance(b);
        int res = is.readSInt32();
        assertEquals(i, res);

        res = LightProtoCodec.readSignedVarInt(bb);
        assertEquals(i, res);

        assertEquals(CodedOutputStream.computeSInt32SizeNoTag(i), LightProtoCodec.computeSignedVarIntSize(i));
    }

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -10000000, -100, -2, -1, 0, 1, 10, 100, 10000000, Long.MAX_VALUE})
    public void testSignedVarInt64(long i) throws Exception {
        LightProtoCodec.writeSignedVarInt64(bb, i);

        CodedInputStream is = CodedInputStream.newInstance(b);
        long res = is.readSInt64();
        assertEquals(i, res);

        res = LightProtoCodec.readSignedVarInt64(bb);
        assertEquals(i, res);

        assertEquals(CodedOutputStream.computeSInt64SizeNoTag(i), LightProtoCodec.computeSignedVarInt64Size(i));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1000, -100, -2, -1, 0, 1, 10, 100, 1000, Integer.MAX_VALUE})
    public void testFixedInt32(int i) throws Exception {
        LightProtoCodec.writeFixedInt32(bb, i);

        CodedInputStream is = CodedInputStream.newInstance(b);
        int res = is.readFixed32();
        assertEquals(i, res);

        res = LightProtoCodec.readFixedInt32(bb);
        assertEquals(i, res);
    }

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -10000000, -100, -2, -1, 0, 1, 10, 100, 10000000, Long.MAX_VALUE})
    public void testFixedInt64(long i) throws Exception {
        LightProtoCodec.writeFixedInt64(bb, i);

        CodedInputStream is = CodedInputStream.newInstance(b);
        long res = is.readFixed64();
        assertEquals(i, res);

        res = LightProtoCodec.readFixedInt64(bb);
        assertEquals(i, res);
    }

    @ParameterizedTest
    @ValueSource(floats = {Float.MIN_VALUE, -1000.0f, -100.0f, -2.f, -1.f, 0f, 1f, 10f, 100f, 1000f, Float.MAX_VALUE})
    public void testFloat(float i) throws Exception {
        LightProtoCodec.writeFloat(bb, i);

        CodedInputStream is = CodedInputStream.newInstance(b);
        float res = is.readFloat();
        assertEquals(i, res);

        res = LightProtoCodec.readFloat(bb);
        assertEquals(i, res);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.MIN_VALUE, -10000000.0, -100.0, -2.0, -1.0, 0.0, 1.0, 10.0, 100.0, 10000000.0, Double.MAX_VALUE})
    public void testDouble(double i) throws Exception {
        LightProtoCodec.writeDouble(bb, i);

        CodedInputStream is = CodedInputStream.newInstance(b);
        double res = is.readDouble();
        assertEquals(i, res);

        res = LightProtoCodec.readDouble(bb);
        assertEquals(i, res);
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "UTF16 Ελληνικά Русский 日本語", "Neque porro quisquam est qui dolorem ipsum"})
    public void testString(String s) throws Exception {
        byte[] sb = s.getBytes(StandardCharsets.UTF_8);
        assertEquals(sb.length, LightProtoCodec.computeStringUTF8Size(s));

        LightProtoCodec.writeVarInt(bb, sb.length);
        int idx = bb.writerIndex();
        LightProtoCodec.writeString(bb, s, sb.length);

        CodedInputStream is = CodedInputStream.newInstance(b);
        assertEquals(s, is.readString());

        assertEquals(sb.length, LightProtoCodec.readVarInt(bb));
        assertEquals(s, LightProtoCodec.readString(bb, idx, sb.length));

        assertEquals(CodedOutputStream.computeStringSizeNoTag(s), LightProtoCodec.computeVarIntSize(sb.length) + LightProtoCodec.computeStringUTF8Size(s));
    }
}
