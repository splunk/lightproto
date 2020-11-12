package io.lightproto.tests;

import com.google.protobuf.CodedOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessagesTest {

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
    public void testMessages() throws Exception {
        LightProtoMessages.M lpm = new LightProtoMessages.M();
        lpm.setX()
                .setA("a")
                .setB("b");

        assertEquals(Collections.emptyList(), lpm.getItemsList());

        lpm.addItem()
                .setK("k1")
                .setV("v1");

        lpm.addItem()
                .setK("k2")
                .setV("v2")
                .setXx().setN(5);

        assertTrue(lpm.hasX());
        assertTrue(lpm.hasItems());
        assertTrue(lpm.getX().hasA());
        assertTrue(lpm.getX().hasB());
        assertEquals(2, lpm.getItemsCount());

        assertEquals("k1", lpm.getItemAt(0).getK());
        assertEquals("v1", lpm.getItemAt(0).getV());
        assertEquals("k2", lpm.getItemAt(1).getK());
        assertEquals("v2", lpm.getItemAt(1).getV());
        assertEquals(5, lpm.getItemAt(1).getXx().getN());
        assertEquals("a", lpm.getX().getA());
        assertEquals("b", lpm.getX().getB());

        List<LightProtoMessages.M.KV> itemsList = lpm.getItemsList();
        assertEquals(2, itemsList.size());
        assertEquals("k1", itemsList.get(0).getK());
        assertEquals("v1", itemsList.get(0).getV());
        assertEquals("k2", itemsList.get(1).getK());
        assertEquals("v2", itemsList.get(1).getV());
        assertEquals(5, itemsList.get(1).getXx().getN());


        Messages.X pbmx = Messages.X.newBuilder()
                .setA("a")
                .setB("b")
                .build();

        Messages.M.KV.XX pbm_xx = Messages.M.KV.XX.newBuilder()
                .setN(5)
                .build();
        Messages.M.KV pbm_kv1 = Messages.M.KV.newBuilder()
                .setK("k1")
                .setV("v1")
                .build();
        Messages.M.KV pbm_kv2 = Messages.M.KV.newBuilder()
                .setK("k2")
                .setV("v2")
                .setXx(pbm_xx)
                .build();

        Messages.M pbm = Messages.M.newBuilder()
                .setX(pbmx)
                .addItems(pbm_kv1)
                .addItems(pbm_kv2)
                .build();

        assertEquals(pbm.getSerializedSize(), lpm.getSerializedSize());

        lpm.writeTo(bb1);
        assertEquals(lpm.getSerializedSize(), bb1.readableBytes());

        pbm.writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        LightProtoMessages.M parsed = new LightProtoMessages.M();
        parsed.parseFrom(bb1, bb1.readableBytes());

        assertTrue(parsed.hasX());
        assertTrue(parsed.hasItems());
        assertTrue(parsed.getX().hasA());
        assertTrue(parsed.getX().hasB());
        assertEquals(2, parsed.getItemsCount());

        assertEquals("k1", parsed.getItemAt(0).getK());
        assertEquals("v1", parsed.getItemAt(0).getV());
        assertEquals("k2", parsed.getItemAt(1).getK());
        assertEquals("v2", parsed.getItemAt(1).getV());
        assertEquals(5, parsed.getItemAt(1).getXx().getN());
        assertEquals("a", parsed.getX().getA());
        assertEquals("b", parsed.getX().getB());
    }
}
