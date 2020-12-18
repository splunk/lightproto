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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RepeatedNumbersTest {

    private byte[] b1 = new byte[4096];
    private ByteBuf bb1 = Unpooled.wrappedBuffer(b1);

    private byte[] b2 = new byte[4096];
    private ByteBuf bb2 = Unpooled.wrappedBuffer(b2);

    private static void assertException(Runnable r) {
        try {
            r.run();
            fail("Should raise exception");
        } catch (IndexOutOfBoundsException e) {
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
        Repeated lpn = new Repeated();
        RepeatedNumbers.Repeated.Builder pbn = RepeatedNumbers.Repeated.newBuilder();

        assertEquals(0, lpn.getEnum1sCount());
        assertEquals(0, lpn.getXBoolsCount());
        assertEquals(0, lpn.getXDoublesCount());
        assertEquals(0, lpn.getXFixed32sCount());
        assertEquals(0, lpn.getXFixed64sCount());
        assertEquals(0, lpn.getXSfixed32sCount());
        assertEquals(0, lpn.getXSfixed64sCount());
        assertEquals(0, lpn.getXFloatsCount());
        assertEquals(0, lpn.getXInt32sCount());
        assertEquals(0, lpn.getXInt64sCount());
        assertEquals(0, lpn.getXInt32sCount());
        assertEquals(0, lpn.getXUint64sCount());
        assertEquals(0, lpn.getXUint32sCount());
        assertEquals(0, lpn.getXSint32sCount());
        assertEquals(0, lpn.getXSint64sCount());

        assertException(() -> lpn.getEnum1At(0));
        assertException(() -> lpn.getXBoolAt(0));
        assertException(() -> lpn.getXDoubleAt(0));
        assertException(() -> lpn.getXFixed32At(0));
        assertException(() -> lpn.getXFixed64At(0));
        assertException(() -> lpn.getXSfixed32At(0));
        assertException(() -> lpn.getXSfixed64At(0));
        assertException(() -> lpn.getXFloatAt(0));
        assertException(() -> lpn.getXInt32At(0));
        assertException(() -> lpn.getXInt64At(0));
        assertException(() -> lpn.getXInt32At(0));
        assertException(() -> lpn.getXUint64At(0));
        assertException(() -> lpn.getXUint32At(0));
        assertException(() -> lpn.getXSint32At(0));
        assertException(() -> lpn.getXSint64At(0));

        lpn.addEnum1(Repeated.Enum.X2_1);
        lpn.addEnum1(Repeated.Enum.X2_2);
        lpn.addXBool(true);
        lpn.addXBool(false);
        lpn.addXDouble(1.0);
        lpn.addXDouble(2.0);
        lpn.addXFixed32(2);
        lpn.addXFixed32(3);
        lpn.addXFixed64(12345L);
        lpn.addXFixed64(12346L);
        lpn.addXSfixed32(-2);
        lpn.addXSfixed32(-3);
        lpn.addXSfixed64(-12345L);
        lpn.addXSfixed64(-12346L);
        lpn.addXFloat(1.2f);
        lpn.addXFloat(1.3f);
        lpn.addXInt32(4);
        lpn.addXInt32(5);
        lpn.addXInt64(126L);
        lpn.addXInt64(127L);
        lpn.addXUint32(40);
        lpn.addXUint32(41);
        lpn.addXUint64(1260L);
        lpn.addXUint64(1261L);
        lpn.addXSint32(-11);
        lpn.addXSint32(-12);
        lpn.addXSint64(-12L);
        lpn.addXSint64(-13L);

        pbn.addEnum1(RepeatedNumbers.Repeated.Enum.X2_1);
        pbn.addEnum1(RepeatedNumbers.Repeated.Enum.X2_2);
        pbn.addXBool(true);
        pbn.addXBool(false);
        pbn.addXDouble(1.0);
        pbn.addXDouble(2.0);
        pbn.addXFixed32(2);
        pbn.addXFixed32(3);
        pbn.addXFixed64(12345L);
        pbn.addXFixed64(12346L);
        pbn.addXSfixed32(-2);
        pbn.addXSfixed32(-3);
        pbn.addXSfixed64(-12345L);
        pbn.addXSfixed64(-12346L);
        pbn.addXFloat(1.2f);
        pbn.addXFloat(1.3f);
        pbn.addXInt32(4);
        pbn.addXInt32(5);
        pbn.addXInt64(126L);
        pbn.addXInt64(127L);
        pbn.addXUint32(40);
        pbn.addXUint32(41);
        pbn.addXUint64(1260L);
        pbn.addXUint64(1261L);
        pbn.addXSint32(-11);
        pbn.addXSint32(-12);
        pbn.addXSint64(-12L);
        pbn.addXSint64(-13L);

        assertEquals(2, lpn.getEnum1sCount());
        assertEquals(2, lpn.getXBoolsCount());
        assertEquals(2, lpn.getXDoublesCount());
        assertEquals(2, lpn.getXFixed32sCount());
        assertEquals(2, lpn.getXFixed64sCount());
        assertEquals(2, lpn.getXSfixed32sCount());
        assertEquals(2, lpn.getXSfixed64sCount());
        assertEquals(2, lpn.getXFloatsCount());
        assertEquals(2, lpn.getXInt32sCount());
        assertEquals(2, lpn.getXInt64sCount());
        assertEquals(2, lpn.getXInt32sCount());
        assertEquals(2, lpn.getXUint64sCount());
        assertEquals(2, lpn.getXUint32sCount());
        assertEquals(2, lpn.getXSint32sCount());
        assertEquals(2, lpn.getXSint64sCount());

        assertEquals(Repeated.Enum.X2_1, lpn.getEnum1At(0));
        assertEquals(Repeated.Enum.X2_2, lpn.getEnum1At(1));
        assertEquals(true, lpn.getXBoolAt(0));
        assertEquals(false, lpn.getXBoolAt(1));
        assertEquals(1.0, lpn.getXDoubleAt(0));
        assertEquals(2.0, lpn.getXDoubleAt(1));
        assertEquals(2, lpn.getXFixed32At(0));
        assertEquals(3, lpn.getXFixed32At(1));
        assertEquals(12345L, lpn.getXFixed64At(0));
        assertEquals(12346L, lpn.getXFixed64At(1));
        assertEquals(-2, lpn.getXSfixed32At(0));
        assertEquals(-3, lpn.getXSfixed32At(1));
        assertEquals(-12345L, lpn.getXSfixed64At(0));
        assertEquals(-12346L, lpn.getXSfixed64At(1));
        assertEquals(1.2f, lpn.getXFloatAt(0));
        assertEquals(1.3f, lpn.getXFloatAt(1));
        assertEquals(4, lpn.getXInt32At(0));
        assertEquals(5, lpn.getXInt32At(1));
        assertEquals(126L, lpn.getXInt64At(0));
        assertEquals(127L, lpn.getXInt64At(1));
        assertEquals(40, lpn.getXUint32At(0));
        assertEquals(41, lpn.getXUint32At(1));
        assertEquals(1260L, lpn.getXUint64At(0));
        assertEquals(1261L, lpn.getXUint64At(1));
        assertEquals(-11, lpn.getXSint32At(0));
        assertEquals(-12, lpn.getXSint32At(1));
        assertEquals(-12L, lpn.getXSint64At(0));
        assertEquals(-13L, lpn.getXSint64At(1));

        Assertions.assertEquals(pbn.build().getSerializedSize(), lpn.getSerializedSize());

        lpn.writeTo(bb1);
        assertEquals(lpn.getSerializedSize(), bb1.readableBytes());

        pbn.build().writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        Repeated parsed = new Repeated();
        parsed.parseFrom(bb1, bb1.readableBytes());

        assertEquals(Repeated.Enum.X2_1, parsed.getEnum1At(0));
        assertEquals(Repeated.Enum.X2_2, parsed.getEnum1At(1));
        assertEquals(true, parsed.getXBoolAt(0));
        assertEquals(false, parsed.getXBoolAt(1));
        assertEquals(1.0, parsed.getXDoubleAt(0));
        assertEquals(2.0, parsed.getXDoubleAt(1));
        assertEquals(2, parsed.getXFixed32At(0));
        assertEquals(3, parsed.getXFixed32At(1));
        assertEquals(12345L, parsed.getXFixed64At(0));
        assertEquals(12346L, parsed.getXFixed64At(1));
        assertEquals(-2, parsed.getXSfixed32At(0));
        assertEquals(-3, parsed.getXSfixed32At(1));
        assertEquals(-12345L, parsed.getXSfixed64At(0));
        assertEquals(-12346L, parsed.getXSfixed64At(1));
        assertEquals(1.2f, parsed.getXFloatAt(0));
        assertEquals(1.3f, parsed.getXFloatAt(1));
        assertEquals(4, parsed.getXInt32At(0));
        assertEquals(5, parsed.getXInt32At(1));
        assertEquals(126L, parsed.getXInt64At(0));
        assertEquals(127L, parsed.getXInt64At(1));
        assertEquals(40, parsed.getXUint32At(0));
        assertEquals(41, parsed.getXUint32At(1));
        assertEquals(1260L, parsed.getXUint64At(0));
        assertEquals(1261L, parsed.getXUint64At(1));
        assertEquals(-11, parsed.getXSint32At(0));
        assertEquals(-12, parsed.getXSint32At(1));
        assertEquals(-12L, parsed.getXSint64At(0));
        assertEquals(-13L, parsed.getXSint64At(1));
    }

    @Test
    public void testNumberFieldsPacked() throws Exception {
        RepeatedPacked lpn = new RepeatedPacked();
        RepeatedNumbers.RepeatedPacked.Builder pbn = RepeatedNumbers.RepeatedPacked.newBuilder();

        assertEquals(0, lpn.getEnum1sCount());
        assertEquals(0, lpn.getXBoolsCount());
        assertEquals(0, lpn.getXDoublesCount());
        assertEquals(0, lpn.getXFixed32sCount());
        assertEquals(0, lpn.getXFixed64sCount());
        assertEquals(0, lpn.getXSfixed32sCount());
        assertEquals(0, lpn.getXSfixed64sCount());
        assertEquals(0, lpn.getXFloatsCount());
        assertEquals(0, lpn.getXInt32sCount());
        assertEquals(0, lpn.getXInt64sCount());
        assertEquals(0, lpn.getXInt32sCount());
        assertEquals(0, lpn.getXUint64sCount());
        assertEquals(0, lpn.getXUint32sCount());
        assertEquals(0, lpn.getXSint32sCount());
        assertEquals(0, lpn.getXSint64sCount());

        assertException(() -> lpn.getEnum1At(0));
        assertException(() -> lpn.getXBoolAt(0));
        assertException(() -> lpn.getXDoubleAt(0));
        assertException(() -> lpn.getXFixed32At(0));
        assertException(() -> lpn.getXFixed64At(0));
        assertException(() -> lpn.getXSfixed32At(0));
        assertException(() -> lpn.getXSfixed64At(0));
        assertException(() -> lpn.getXFloatAt(0));
        assertException(() -> lpn.getXInt32At(0));
        assertException(() -> lpn.getXInt64At(0));
        assertException(() -> lpn.getXInt32At(0));
        assertException(() -> lpn.getXUint64At(0));
        assertException(() -> lpn.getXUint32At(0));
        assertException(() -> lpn.getXSint32At(0));
        assertException(() -> lpn.getXSint64At(0));


        lpn.addEnum1(RepeatedPacked.Enum.X2_1);
        lpn.addEnum1(RepeatedPacked.Enum.X2_2);
        lpn.addXBool(true);
        lpn.addXBool(false);
        lpn.addXDouble(1.0);
        lpn.addXDouble(2.0);
        lpn.addXFixed32(2);
        lpn.addXFixed32(3);
        lpn.addXFixed64(12345L);
        lpn.addXFixed64(12346L);
        lpn.addXSfixed32(-2);
        lpn.addXSfixed32(-3);
        lpn.addXSfixed64(-12345L);
        lpn.addXSfixed64(-12346L);
        lpn.addXFloat(1.2f);
        lpn.addXFloat(1.3f);
        lpn.addXInt32(4);
        lpn.addXInt32(5);
        lpn.addXInt64(126L);
        lpn.addXInt64(127L);
        lpn.addXUint32(40);
        lpn.addXUint32(41);
        lpn.addXUint64(1260L);
        lpn.addXUint64(1261L);
        lpn.addXSint32(-11);
        lpn.addXSint32(-12);
        lpn.addXSint64(-12L);
        lpn.addXSint64(-13L);

        pbn.addEnum1(RepeatedNumbers.RepeatedPacked.Enum.X2_1);
        pbn.addEnum1(RepeatedNumbers.RepeatedPacked.Enum.X2_2);
        pbn.addXBool(true);
        pbn.addXBool(false);
        pbn.addXDouble(1.0);
        pbn.addXDouble(2.0);
        pbn.addXFixed32(2);
        pbn.addXFixed32(3);
        pbn.addXFixed64(12345L);
        pbn.addXFixed64(12346L);
        pbn.addXSfixed32(-2);
        pbn.addXSfixed32(-3);
        pbn.addXSfixed64(-12345L);
        pbn.addXSfixed64(-12346L);
        pbn.addXFloat(1.2f);
        pbn.addXFloat(1.3f);
        pbn.addXInt32(4);
        pbn.addXInt32(5);
        pbn.addXInt64(126L);
        pbn.addXInt64(127L);
        pbn.addXUint32(40);
        pbn.addXUint32(41);
        pbn.addXUint64(1260L);
        pbn.addXUint64(1261L);
        pbn.addXSint32(-11);
        pbn.addXSint32(-12);
        pbn.addXSint64(-12L);
        pbn.addXSint64(-13L);

        assertEquals(2, lpn.getEnum1sCount());
        assertEquals(2, lpn.getXBoolsCount());
        assertEquals(2, lpn.getXDoublesCount());
        assertEquals(2, lpn.getXFixed32sCount());
        assertEquals(2, lpn.getXFixed64sCount());
        assertEquals(2, lpn.getXSfixed32sCount());
        assertEquals(2, lpn.getXSfixed64sCount());
        assertEquals(2, lpn.getXFloatsCount());
        assertEquals(2, lpn.getXInt32sCount());
        assertEquals(2, lpn.getXInt64sCount());
        assertEquals(2, lpn.getXInt32sCount());
        assertEquals(2, lpn.getXUint64sCount());
        assertEquals(2, lpn.getXUint32sCount());
        assertEquals(2, lpn.getXSint32sCount());
        assertEquals(2, lpn.getXSint64sCount());

        assertEquals(RepeatedPacked.Enum.X2_1, lpn.getEnum1At(0));
        assertEquals(RepeatedPacked.Enum.X2_2, lpn.getEnum1At(1));
        assertEquals(true, lpn.getXBoolAt(0));
        assertEquals(false, lpn.getXBoolAt(1));
        assertEquals(1.0, lpn.getXDoubleAt(0));
        assertEquals(2.0, lpn.getXDoubleAt(1));
        assertEquals(2, lpn.getXFixed32At(0));
        assertEquals(3, lpn.getXFixed32At(1));
        assertEquals(12345L, lpn.getXFixed64At(0));
        assertEquals(12346L, lpn.getXFixed64At(1));
        assertEquals(-2, lpn.getXSfixed32At(0));
        assertEquals(-3, lpn.getXSfixed32At(1));
        assertEquals(-12345L, lpn.getXSfixed64At(0));
        assertEquals(-12346L, lpn.getXSfixed64At(1));
        assertEquals(1.2f, lpn.getXFloatAt(0));
        assertEquals(1.3f, lpn.getXFloatAt(1));
        assertEquals(4, lpn.getXInt32At(0));
        assertEquals(5, lpn.getXInt32At(1));
        assertEquals(126L, lpn.getXInt64At(0));
        assertEquals(127L, lpn.getXInt64At(1));
        assertEquals(40, lpn.getXUint32At(0));
        assertEquals(41, lpn.getXUint32At(1));
        assertEquals(1260L, lpn.getXUint64At(0));
        assertEquals(1261L, lpn.getXUint64At(1));
        assertEquals(-11, lpn.getXSint32At(0));
        assertEquals(-12, lpn.getXSint32At(1));
        assertEquals(-12L, lpn.getXSint64At(0));
        assertEquals(-13L, lpn.getXSint64At(1));

        Assertions.assertEquals(pbn.build().getSerializedSize(), lpn.getSerializedSize());

        lpn.writeTo(bb1);
        assertEquals(lpn.getSerializedSize(), bb1.readableBytes());

        pbn.build().writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        RepeatedPacked parsed = new RepeatedPacked();
        parsed.parseFrom(bb1, bb1.readableBytes());

        assertEquals(RepeatedPacked.Enum.X2_1, parsed.getEnum1At(0));
        assertEquals(RepeatedPacked.Enum.X2_2, parsed.getEnum1At(1));
        assertEquals(true, parsed.getXBoolAt(0));
        assertEquals(false, parsed.getXBoolAt(1));
        assertEquals(1.0, parsed.getXDoubleAt(0));
        assertEquals(2.0, parsed.getXDoubleAt(1));
        assertEquals(2, parsed.getXFixed32At(0));
        assertEquals(3, parsed.getXFixed32At(1));
        assertEquals(12345L, parsed.getXFixed64At(0));
        assertEquals(12346L, parsed.getXFixed64At(1));
        assertEquals(-2, parsed.getXSfixed32At(0));
        assertEquals(-3, parsed.getXSfixed32At(1));
        assertEquals(-12345L, parsed.getXSfixed64At(0));
        assertEquals(-12346L, parsed.getXSfixed64At(1));
        assertEquals(1.2f, parsed.getXFloatAt(0));
        assertEquals(1.3f, parsed.getXFloatAt(1));
        assertEquals(4, parsed.getXInt32At(0));
        assertEquals(5, parsed.getXInt32At(1));
        assertEquals(126L, parsed.getXInt64At(0));
        assertEquals(127L, parsed.getXInt64At(1));
        assertEquals(40, parsed.getXUint32At(0));
        assertEquals(41, parsed.getXUint32At(1));
        assertEquals(1260L, parsed.getXUint64At(0));
        assertEquals(1261L, parsed.getXUint64At(1));
        assertEquals(-11, parsed.getXSint32At(0));
        assertEquals(-12, parsed.getXSint32At(1));
        assertEquals(-12L, parsed.getXSint64At(0));
        assertEquals(-13L, parsed.getXSint64At(1));
    }
}
