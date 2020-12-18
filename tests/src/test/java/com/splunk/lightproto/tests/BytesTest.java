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
package com.splunk.lightproto.tests;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class BytesTest {

    private byte[] b1 = new byte[4096];
    private ByteBuf bb1 = Unpooled.wrappedBuffer(b1);

    private byte[] b2 = new byte[4096];

    @BeforeEach
    public void setup() {
        bb1.clear();
        Arrays.fill(b1, (byte) 0);
        Arrays.fill(b2, (byte) 0);
    }

    @Test
    public void testBytes() throws Exception {
        B lpb = new B()
                .setPayload(new byte[]{1, 2, 3});

        assertTrue(lpb.hasPayload());
        assertEquals(3, lpb.getPayloadSize());
        assertArrayEquals(new byte[]{1, 2, 3}, lpb.getPayload());

        Bytes.B pbb = Bytes.B.newBuilder()
                .setPayload(ByteString.copyFrom(new byte[]{1, 2, 3}))
                .build();

        assertEquals(pbb.getSerializedSize(), lpb.getSerializedSize());
        lpb.writeTo(bb1);
        assertEquals(lpb.getSerializedSize(), bb1.readableBytes());

        pbb.writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        B parsed = new B();
        parsed.parseFrom(bb1, bb1.readableBytes());

        assertTrue(parsed.hasPayload());
        assertEquals(3, parsed.getPayloadSize());
        assertArrayEquals(new byte[]{1, 2, 3}, parsed.getPayload());
    }

    @Test
    public void testBytesBuf() throws Exception {
        B lpb = new B();
        ByteBuf b = Unpooled.directBuffer(3);
        b.writeBytes(new byte[]{1, 2, 3});
        lpb.setPayload(b);

        assertTrue(lpb.hasPayload());
        assertEquals(3, lpb.getPayloadSize());
        assertArrayEquals(new byte[]{1, 2, 3}, lpb.getPayload());
        assertEquals(Unpooled.wrappedBuffer(new byte[]{1, 2, 3}), lpb.getPayloadSlice());

        Bytes.B pbb = Bytes.B.newBuilder()
                .setPayload(ByteString.copyFrom(new byte[]{1, 2, 3}))
                .build();

        assertEquals(pbb.getSerializedSize(), lpb.getSerializedSize());
        lpb.writeTo(bb1);
        assertEquals(lpb.getSerializedSize(), bb1.readableBytes());

        pbb.writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        B parsed = new B();
        parsed.parseFrom(bb1, bb1.readableBytes());

        assertTrue(parsed.hasPayload());
        assertEquals(3, parsed.getPayloadSize());
        assertEquals(Unpooled.wrappedBuffer(new byte[]{1, 2, 3}), parsed.getPayloadSlice());
        assertArrayEquals(new byte[]{1, 2, 3}, parsed.getPayload());

        bb1.clear();
        parsed.writeTo(bb1);

        assertEquals(lpb.getSerializedSize(), bb1.readableBytes());
        assertArrayEquals(b1, b2);
    }

    @Test
    public void testRepeatedBytes() throws Exception {
        B lpb = new B();
        lpb.addExtraItem(new byte[]{1, 2, 3});
        lpb.addExtraItem(new byte[]{4, 5, 6, 7});

        assertEquals(2, lpb.getExtraItemsCount());
        assertEquals(3, lpb.getExtraItemSizeAt(0));
        assertEquals(4, lpb.getExtraItemSizeAt(1));
        assertArrayEquals(new byte[]{1, 2, 3}, lpb.getExtraItemAt(0));
        assertArrayEquals(new byte[]{4, 5, 6, 7}, lpb.getExtraItemAt(1));

        Bytes.B pbb = Bytes.B.newBuilder()
                .addExtraItems(ByteString.copyFrom(new byte[]{1, 2, 3}))
                .addExtraItems(ByteString.copyFrom(new byte[]{4, 5, 6, 7}))
                .build();

        assertEquals(pbb.getSerializedSize(), lpb.getSerializedSize());
        lpb.writeTo(bb1);
        assertEquals(lpb.getSerializedSize(), bb1.readableBytes());

        pbb.writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        B parsed = new B();
        parsed.parseFrom(bb1, bb1.readableBytes());

        assertEquals(2, parsed.getExtraItemsCount());
        assertEquals(3, parsed.getExtraItemSizeAt(0));
        assertEquals(4, parsed.getExtraItemSizeAt(1));
        assertArrayEquals(new byte[]{1, 2, 3}, parsed.getExtraItemAt(0));
        assertArrayEquals(new byte[]{4, 5, 6, 7}, parsed.getExtraItemAt(1));
    }
}
