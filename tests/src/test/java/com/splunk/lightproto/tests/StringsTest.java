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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class StringsTest {

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
    public void testStrings() throws Exception {
        S lps = new S()
                .setId("id");
        lps.addName("a");
        lps.addName("b");
        lps.addName("c");

        assertEquals("id", lps.getId());
        assertEquals(3, lps.getNamesCount());
        assertEquals("a", lps.getNameAt(0));
        assertEquals("b", lps.getNameAt(1));
        assertEquals("c", lps.getNameAt(2));

        Strings.S pbs = Strings.S.newBuilder()
                .setId("id")
                .addNames("a")
                .addNames("b")
                .addNames("c")
                .build();

        assertEquals(pbs.getSerializedSize(), lps.getSerializedSize());

        lps.writeTo(bb1);
        assertEquals(lps.getSerializedSize(), bb1.readableBytes());

        pbs.writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        S parsed = new S();
        parsed.parseFrom(bb1, bb1.readableBytes());

        assertEquals("id", parsed.getId());
        assertEquals(3, parsed.getNamesCount());
        assertEquals("a", parsed.getNameAt(0));
        assertEquals("b", parsed.getNameAt(1));
        assertEquals("c", parsed.getNameAt(2));
    }

    @Test
    public void testAddAllStrings() throws Exception {
        Set<String> strings = new TreeSet<>();
        strings.add("a");
        strings.add("b");
        strings.add("c");

        S lps = new S()
                .setId("id")
                .addAllNames(strings);

        assertEquals("id", lps.getId());
        assertEquals(3, lps.getNamesCount());
        assertEquals("a", lps.getNameAt(0));
        assertEquals("b", lps.getNameAt(1));
        assertEquals("c", lps.getNameAt(2));
        assertEquals(new ArrayList<>(strings), lps.getNamesList());
    }

    @Test
    public void testClearStrings() throws Exception {
        S lps = new S();
        lps.addName("a");
        lps.addName("b");
        lps.addName("c");

        lps.clear();
        lps.addName("d");
        lps.addName("e");
        assertEquals(2, lps.getNamesCount());
        assertEquals("d", lps.getNameAt(0));
        assertEquals("e", lps.getNameAt(1));
    }
}
