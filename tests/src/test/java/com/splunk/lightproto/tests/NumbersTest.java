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

import com.google.protobuf.CodedOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class NumbersTest {

    private byte[] b1 = new byte[4096];
    private ByteBuf bb1 = Unpooled.wrappedBuffer(b1);

    private byte[] b2 = new byte[4096];
    private ByteBuf bb2 = Unpooled.wrappedBuffer(b2);

    private static void assertException(Runnable r) {
        try {
            r.run();
            fail("Should raise exception");
        } catch (IllegalStateException e) {
            // Expected
        }
    }

    @BeforeEach
    public void setup() {
        bb1.clear();
        Arrays.fill(b1, (byte) 0);

        bb2.clear();
        Arrays.fill(b2, (byte) 0);
    }

    @Test
    public void testEnumValue() throws Exception {
        assertEquals(Enum1.X1_0_VALUE, Enum1.X1_0.getValue());
        assertEquals(Enum1.X1_1_VALUE, Enum1.X1_1.getValue());
        assertEquals(Enum1.X1_2_VALUE, Enum1.X1_2.getValue());
    }

    @Test
    public void testEmpty() throws Exception {
        Numbers lpn = new Numbers();
        NumbersOuterClass.Numbers pbn = NumbersOuterClass.Numbers.newBuilder().build();
        verify(lpn, pbn);
    }

    private void verify(Numbers lpn, NumbersOuterClass.Numbers pbn) throws Exception {
        assertEquals(pbn.getSerializedSize(), lpn.getSerializedSize());

        lpn.writeTo(bb1);
        assertEquals(lpn.getSerializedSize(), bb1.readableBytes());

        pbn.writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        Numbers parsed = new Numbers();
        parsed.parseFrom(bb1, bb1.readableBytes());

        assertEquals(pbn.hasEnum1(), parsed.hasEnum1());
        assertEquals(pbn.hasEnum2(), parsed.hasEnum2());
        assertEquals(pbn.hasXBool(), parsed.hasXBool());
        assertEquals(pbn.hasXDouble(), parsed.hasXDouble());
        assertEquals(pbn.hasXFixed32(), parsed.hasXFixed32());
        assertEquals(pbn.hasXFixed64(), parsed.hasXFixed64());
        assertEquals(pbn.hasXFixed32(), parsed.hasXSfixed32());
        assertEquals(pbn.hasXSfixed64(), parsed.hasXSfixed64());
        assertEquals(pbn.hasXFloat(), parsed.hasXFloat());
        assertEquals(pbn.hasXInt32(), parsed.hasXInt32());
        assertEquals(pbn.hasXInt64(), parsed.hasXInt64());
        assertEquals(pbn.hasXSint32(), parsed.hasXSint32());
        assertEquals(pbn.hasXSint64(), parsed.hasXSint64());

        if (parsed.hasEnum1()) {
            assertEquals(pbn.getEnum1().getNumber(), parsed.getEnum1().getValue());
        }
        if (parsed.hasEnum2()) {
            assertEquals(pbn.getEnum2().getNumber(), parsed.getEnum2().getValue());
        }
        if (parsed.hasXBool()) {
            assertEquals(pbn.getXBool(), parsed.isXBool());
        }
        if (parsed.hasXDouble()) {
            assertEquals(pbn.getXDouble(), parsed.getXDouble());
        }
        if (parsed.hasXFixed32()) {
            assertEquals(pbn.getXFixed32(), parsed.getXFixed32());
        }
        if (parsed.hasXFixed64()) {
            assertEquals(pbn.getXFixed64(), parsed.getXFixed64());
        }
        if (parsed.hasXSfixed32()) {
            assertEquals(pbn.getXSfixed32(), parsed.getXSfixed32());
        }
        if (parsed.hasXSfixed64()) {
            assertEquals(pbn.getXSfixed64(), parsed.getXSfixed64());
        }
        if (parsed.hasXFloat()) {
            assertEquals(pbn.getXFloat(), parsed.getXFloat());
        }
        if (parsed.hasXInt32()) {
            assertEquals(pbn.getXInt32(), parsed.getXInt32());
        }
        if (parsed.hasXInt64()) {
            assertEquals(pbn.getXInt64(), parsed.getXInt64());
        }
        if (parsed.hasXSint32()) {
            assertEquals(pbn.getXSint32(), parsed.getXSint32());
        }
        if (parsed.hasXSint64()) {
            assertEquals(pbn.getXSint64(), parsed.getXSint64());
        }
    }

    @Test
    public void testNumberFields() throws Exception {
        Numbers lpn = new Numbers();
        NumbersOuterClass.Numbers.Builder pbn = NumbersOuterClass.Numbers.newBuilder();

        assertFalse(lpn.hasEnum1());
        assertFalse(lpn.hasEnum2());
        assertFalse(lpn.hasXBool());
        assertFalse(lpn.hasXDouble());
        assertFalse(lpn.hasXFixed32());
        assertFalse(lpn.hasXFixed64());
        assertFalse(lpn.hasXSfixed32());
        assertFalse(lpn.hasXSfixed64());
        assertFalse(lpn.hasXFloat());
        assertFalse(lpn.hasXInt32());
        assertFalse(lpn.hasXInt64());
        assertFalse(lpn.hasXInt32());
        assertFalse(lpn.hasXUint64());
        assertFalse(lpn.hasXUint32());
        assertFalse(lpn.hasXSint32());
        assertFalse(lpn.hasXSint64());

        assertException(() -> lpn.getEnum1());
        assertException(() -> lpn.getEnum2());
        assertException(() -> lpn.isXBool());
        assertException(() -> lpn.getXDouble());
        assertException(() -> lpn.getXFixed32());
        assertException(() -> lpn.getXFixed64());
        assertException(() -> lpn.getXSfixed32());
        assertException(() -> lpn.getXSfixed64());
        assertException(() -> lpn.getXFloat());
        assertException(() -> lpn.getXInt32());
        assertException(() -> lpn.getXInt64());
        assertException(() -> lpn.getXInt32());
        assertException(() -> lpn.getXUint64());
        assertException(() -> lpn.getXUint32());
        assertException(() -> lpn.getXSint32());
        assertException(() -> lpn.getXSint64());


        lpn.setEnum1(Enum1.X1_1);
        lpn.setEnum2(Numbers.Enum2.X2_1);
        lpn.setXBool(true);
        lpn.setXDouble(1.0);
        lpn.setXFixed32(2);
        lpn.setXFixed64(12345L);
        lpn.setXSfixed32(-2);
        lpn.setXSfixed64(-12345L);
        lpn.setXFloat(1.2f);
        lpn.setXInt32(4);
        lpn.setXInt64(126L);
        lpn.setXUint32(40);
        lpn.setXUint64(1260L);
        lpn.setXSint32(-11);
        lpn.setXSint64(-12L);

        pbn.setEnum1(NumbersOuterClass.Enum1.X1_1);
        pbn.setEnum2(NumbersOuterClass.Numbers.Enum2.X2_1);
        pbn.setXBool(true);
        pbn.setXDouble(1.0);
        pbn.setXFixed32(2);
        pbn.setXFixed64(12345L);
        pbn.setXSfixed32(-2);
        pbn.setXSfixed64(-12345L);
        pbn.setXFloat(1.2f);
        pbn.setXInt32(4);
        pbn.setXInt64(126L);
        pbn.setXUint32(40);
        pbn.setXUint64(1260L);
        pbn.setXSint32(-11);
        pbn.setXSint64(-12L);

        assertTrue(lpn.hasEnum1());
        assertTrue(lpn.hasEnum2());
        assertTrue(lpn.hasXBool());
        assertTrue(lpn.hasXDouble());
        assertTrue(lpn.hasXFixed32());
        assertTrue(lpn.hasXFixed64());
        assertTrue(lpn.hasXSfixed32());
        assertTrue(lpn.hasXSfixed64());
        assertTrue(lpn.hasXFloat());
        assertTrue(lpn.hasXInt32());
        assertTrue(lpn.hasXInt64());
        assertTrue(lpn.hasXSint32());
        assertTrue(lpn.hasXSint64());

        assertEquals(Enum1.X1_1, lpn.getEnum1());
        assertEquals(Numbers.Enum2.X2_1, lpn.getEnum2());
        assertEquals(true, lpn.isXBool());
        assertEquals(1.0, lpn.getXDouble());
        assertEquals(2, lpn.getXFixed32());
        assertEquals(12345L, lpn.getXFixed64());
        assertEquals(-2, lpn.getXSfixed32());
        assertEquals(-12345L, lpn.getXSfixed64());
        assertEquals(1.2f, lpn.getXFloat());
        assertEquals(4, lpn.getXInt32());
        assertEquals(126L, lpn.getXInt64());
        assertEquals(40, lpn.getXUint32());
        assertEquals(1260L, lpn.getXUint64());
        assertEquals(-11, lpn.getXSint32());
        assertEquals(-12L, lpn.getXSint64());

        verify(lpn, pbn.build());
    }
}
