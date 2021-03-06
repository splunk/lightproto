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

import com.google.protobuf.CodedOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddressBookTest {

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
    public void testAddressBook() throws Exception {
        AddressBook ab = new AddressBook();
        Person p1 = ab.addPerson()
                .setName("name 1")
                .setEmail("name1@example.com")
                .setId(5);
        p1.addPhone()
                .setNumber("xxx-zzz-1111")
                .setType(Person.PhoneType.HOME);
        p1.addPhone()
                .setNumber("xxx-zzz-2222")
                .setType(Person.PhoneType.MOBILE);

        Person p2 = ab.addPerson()
                .setName("name 2")
                .setEmail("name2@example.com")
                .setId(6);
        p2.addPhone()
                .setNumber("xxx-zzz-2222")
                .setType(Person.PhoneType.HOME);

        assertEquals(2, ab.getPersonsCount());
        assertEquals("name 1", ab.getPersonAt(0).getName());
        assertEquals("name1@example.com", ab.getPersonAt(0).getEmail());
        assertEquals(5, ab.getPersonAt(0).getId());
        assertEquals("xxx-zzz-1111", ab.getPersonAt(0).getPhoneAt(0).getNumber());
        assertEquals(Person.PhoneType.HOME, ab.getPersonAt(0).getPhoneAt(0).getType());
        assertEquals("name 2", ab.getPersonAt(1).getName());
        assertEquals("name2@example.com", ab.getPersonAt(1).getEmail());
        assertEquals(6, ab.getPersonAt(1).getId());
        assertEquals("xxx-zzz-2222", ab.getPersonAt(1).getPhoneAt(0).getNumber());
        assertEquals(Person.PhoneType.HOME, ab.getPersonAt(0).getPhoneAt(0).getType());

        AddressBookProtos.AddressBook.Builder pbab = AddressBookProtos.AddressBook.newBuilder();
        AddressBookProtos.Person.Builder pb_p1 = AddressBookProtos.Person.newBuilder();
        pb_p1.setName("name 1");
        pb_p1.setEmail("name1@example.com");
        pb_p1.setId(5);
        AddressBookProtos.Person.PhoneNumber.Builder pb1_pn1 = AddressBookProtos.Person.PhoneNumber.newBuilder();
        pb1_pn1.setNumber("xxx-zzz-1111");
        pb1_pn1.setType(AddressBookProtos.Person.PhoneType.HOME);

        AddressBookProtos.Person.PhoneNumber.Builder pb1_pn2 = AddressBookProtos.Person.PhoneNumber.newBuilder();
        pb1_pn2.setNumber("xxx-zzz-2222");
        pb1_pn2.setType(AddressBookProtos.Person.PhoneType.MOBILE);

        pb_p1.addPhone(pb1_pn1);
        pb_p1.addPhone(pb1_pn2);

        AddressBookProtos.Person.Builder pb_p2 = AddressBookProtos.Person.newBuilder();
        pb_p2.setName("name 2");
        pb_p2.setEmail("name2@example.com");
        pb_p2.setId(6);

        AddressBookProtos.Person.PhoneNumber.Builder pb2_pn1 = AddressBookProtos.Person.PhoneNumber.newBuilder();
        pb2_pn1.setNumber("xxx-zzz-2222");
        pb2_pn1.setType(AddressBookProtos.Person.PhoneType.HOME);

        pb_p2.addPhone(pb2_pn1);

        pbab.addPerson(pb_p1);
        pbab.addPerson(pb_p2);

        assertEquals(pbab.build().getSerializedSize(), ab.getSerializedSize());

        ab.writeTo(bb1);
        assertEquals(ab.getSerializedSize(), bb1.readableBytes());

        pbab.build().writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        AddressBook parsed = new AddressBook();
        parsed.parseFrom(bb1, bb1.readableBytes());

        assertEquals(2, parsed.getPersonsCount());
        assertEquals("name 1", parsed.getPersonAt(0).getName());
        assertEquals("name1@example.com", parsed.getPersonAt(0).getEmail());
        assertEquals(5, parsed.getPersonAt(0).getId());
        assertEquals("xxx-zzz-1111", parsed.getPersonAt(0).getPhoneAt(0).getNumber());
        assertEquals(Person.PhoneType.HOME, parsed.getPersonAt(0).getPhoneAt(0).getType());
        assertEquals("name 2", parsed.getPersonAt(1).getName());
        assertEquals("name2@example.com", parsed.getPersonAt(1).getEmail());
        assertEquals(6, parsed.getPersonAt(1).getId());
        assertEquals("xxx-zzz-2222", parsed.getPersonAt(1).getPhoneAt(0).getNumber());
        assertEquals(Person.PhoneType.HOME, parsed.getPersonAt(1).getPhoneAt(0).getType());
    }
}
