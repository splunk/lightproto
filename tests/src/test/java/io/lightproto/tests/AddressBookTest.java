package io.lightproto.tests;

import com.example.tutorial.AddressBookProtos;
import com.example.tutorial.LightProtoAddressbook;
import com.google.protobuf.CodedOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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
        LightProtoAddressbook.AddressBook ab = new LightProtoAddressbook.AddressBook();
        LightProtoAddressbook.Person p1 = ab.addPerson();
        p1.setName("name 1");
        p1.setEmail("name1@example.com");
        p1.setId(5);
        LightProtoAddressbook.Person.PhoneNumber p1_pn1 = p1.addPhone();
        p1_pn1.setNumber("xxx-zzz-1111");
        p1_pn1.setType(LightProtoAddressbook.Person.PhoneType.HOME);

        LightProtoAddressbook.Person.PhoneNumber p1_pn2 = p1.addPhone();
        p1_pn2.setNumber("xxx-zzz-2222");
        p1_pn2.setType(LightProtoAddressbook.Person.PhoneType.MOBILE);

        LightProtoAddressbook.Person p2 = ab.addPerson();
        p2.setName("name 2");
        p2.setEmail("name2@example.com");
        p2.setId(6);

        LightProtoAddressbook.Person.PhoneNumber p2_pn1 = p2.addPhone();
        p2_pn1.setNumber("xxx-zzz-2222");
        p2_pn1.setType(LightProtoAddressbook.Person.PhoneType.HOME);


        assertEquals(2, ab.getPersonsCount());
        assertEquals("name 1", ab.getPersonAt(0).getName());
        assertEquals("name1@example.com", ab.getPersonAt(0).getEmail());
        assertEquals(5, ab.getPersonAt(0).getId());
        assertEquals("xxx-zzz-1111", ab.getPersonAt(0).getPhoneAt(0).getNumber());
        assertEquals(LightProtoAddressbook.Person.PhoneType.HOME, ab.getPersonAt(0).getPhoneAt(0).getType());
        assertEquals("name 2", ab.getPersonAt(1).getName());
        assertEquals("name2@example.com", ab.getPersonAt(1).getEmail());
        assertEquals(6, ab.getPersonAt(1).getId());
        assertEquals("xxx-zzz-2222", ab.getPersonAt(1).getPhoneAt(0).getNumber());
        assertEquals(LightProtoAddressbook.Person.PhoneType.HOME, ab.getPersonAt(0).getPhoneAt(0).getType());

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

        LightProtoAddressbook.AddressBook parsed = new LightProtoAddressbook.AddressBook();
        parsed.parseFrom(bb1, bb1.readableBytes());

        assertEquals(2, parsed.getPersonsCount());
        assertEquals("name 1", parsed.getPersonAt(0).getName());
        assertEquals("name1@example.com", parsed.getPersonAt(0).getEmail());
        assertEquals(5, parsed.getPersonAt(0).getId());
        assertEquals("xxx-zzz-1111", parsed.getPersonAt(0).getPhoneAt(0).getNumber());
        assertEquals(LightProtoAddressbook.Person.PhoneType.HOME, parsed.getPersonAt(0).getPhoneAt(0).getType());
        assertEquals("name 2", parsed.getPersonAt(1).getName());
        assertEquals("name2@example.com", parsed.getPersonAt(1).getEmail());
        assertEquals(6, parsed.getPersonAt(1).getId());
        assertEquals("xxx-zzz-2222", parsed.getPersonAt(1).getPhoneAt(0).getNumber());
        assertEquals(LightProtoAddressbook.Person.PhoneType.HOME, parsed.getPersonAt(1).getPhoneAt(0).getType());
    }
}
