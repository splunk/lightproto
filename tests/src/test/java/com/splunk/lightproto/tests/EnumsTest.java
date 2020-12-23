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
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class EnumsTest {

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
    public void testUnknownRequiredEnum() throws Exception {
        EnumTest2 lpet2 = new EnumTest2()
                .setE(E2.D2);

        assertEquals(E2.D2, lpet2.getE());

        Enums.EnumTest2 pbet2 = Enums.EnumTest2.newBuilder()
                .setE(Enums.E2.D2)
                .build();

        assertEquals(pbet2.getSerializedSize(), lpet2.getSerializedSize());

        lpet2.writeTo(bb1);
        assertEquals(lpet2.getSerializedSize(), bb1.readableBytes());

        pbet2.writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        EnumTest1 parsed = new EnumTest1();
        try {
            parsed.parseFrom(bb1, bb1.readableBytes());
            fail("Should have failed");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("Some required fields are missing"));
        }
    }

    @Test
    public void testUnknownOptionalEnum() throws Exception {
        EnumTest2 lpet2 = new EnumTest2()
                .setE(E2.D2);

        assertEquals(E2.D2, lpet2.getE());

        Enums.EnumTest2 pbet2 = Enums.EnumTest2.newBuilder()
                .setE(Enums.E2.D2)
                .build();

        assertEquals(pbet2.getSerializedSize(), lpet2.getSerializedSize());

        lpet2.writeTo(bb1);
        assertEquals(lpet2.getSerializedSize(), bb1.readableBytes());

        pbet2.writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        EnumTest1Optional parsed = new EnumTest1Optional();
        parsed.parseFrom(bb1, bb1.readableBytes());
        assertFalse(parsed.hasE());
    }

    @Test
    public void testUnknownRepeatedEnum() throws Exception {
        EnumTest2Repeated lpet2 = new EnumTest2Repeated();
        lpet2.addE(E2.A2);
        lpet2.addE(E2.B2);
        lpet2.addE(E2.C2);
        lpet2.addE(E2.D2);

        assertEquals(4, lpet2.getEsCount());

        Enums.EnumTest2Repeated pbet2 = Enums.EnumTest2Repeated.newBuilder()
                .addE(Enums.E2.A2)
                .addE(Enums.E2.B2)
                .addE(Enums.E2.C2)
                .addE(Enums.E2.D2)
                .build();

        assertEquals(pbet2.getSerializedSize(), lpet2.getSerializedSize());

        lpet2.writeTo(bb1);
        assertEquals(lpet2.getSerializedSize(), bb1.readableBytes());

        pbet2.writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        EnumTest1Repeated parsed = new EnumTest1Repeated();
        parsed.parseFrom(bb1, bb1.readableBytes());
        assertEquals(3, parsed.getEsCount());
        assertEquals(E1.A1, parsed.getEAt(0));
        assertEquals(E1.B1, parsed.getEAt(1));
        assertEquals(E1.C1, parsed.getEAt(2));
    }

    @Test
    public void testUnknownRepeatedPackedEnum() throws Exception {
        EnumTest2Packed lpet2 = new EnumTest2Packed();
        lpet2.addE(E2.A2);
        lpet2.addE(E2.B2);
        lpet2.addE(E2.C2);
        lpet2.addE(E2.D2);

        assertEquals(4, lpet2.getEsCount());

        Enums.EnumTest2Packed pbet2 = Enums.EnumTest2Packed.newBuilder()
                .addE(Enums.E2.A2)
                .addE(Enums.E2.B2)
                .addE(Enums.E2.C2)
                .addE(Enums.E2.D2)
                .build();

        assertEquals(pbet2.getSerializedSize(), lpet2.getSerializedSize());

        lpet2.writeTo(bb1);
        assertEquals(lpet2.getSerializedSize(), bb1.readableBytes());

        pbet2.writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        EnumTest1Packed parsed = new EnumTest1Packed();
        parsed.parseFrom(bb1, bb1.readableBytes());
        assertEquals(3, parsed.getEsCount());
        assertEquals(E1.A1, parsed.getEAt(0));
        assertEquals(E1.B1, parsed.getEAt(1));
        assertEquals(E1.C1, parsed.getEAt(2));
    }
}
