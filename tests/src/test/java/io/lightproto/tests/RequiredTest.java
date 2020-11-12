package io.lightproto.tests;

import com.google.protobuf.CodedOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RequiredTest {

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
    public void testMissingFields() throws Exception {
        LightProtoRequired.R lpr = new LightProtoRequired.R();
        try {
            lpr.writeTo(bb1);
            fail("Should fail to serialize");
        } catch (IllegalStateException e) {
            // Expected
        }

        assertFalse(lpr.hasA());
        assertFalse(lpr.hasB());
        assertFalse(lpr.hasC());

        assertEquals(5, lpr.getC()); // Default is set

        lpr.setA(2);

        Required.R pbr = Required.R.newBuilder()
                .setA(2)
                .build();

        verify(lpr, pbr);
    }

    @Test
    public void testDeserializeWithMissingFields() throws Exception {
        LightProtoRequired.NR lpnr = new LightProtoRequired.NR();
        lpnr.setB(3);

        lpnr.writeTo(bb1);

        LightProtoRequired.R lpr = new LightProtoRequired.R();
        try {
            lpr.parseFrom(bb1, bb1.readableBytes());
            fail("Should fail to de-serialize");
        } catch (IllegalStateException e) {
            // Expected
        }
    }

    @Test
    public void testIgnoreUnknownFields() throws Exception {
        LightProtoRequired.RExt lprext = new LightProtoRequired.RExt();
        lprext.setA(1);
        lprext.setB(3);
        lprext.setExtD(10);
        lprext.setExtE(11);
        lprext.setExtF(111L);
        lprext.setExtG("hello");
        int s1 = lprext.getSerializedSize();

        lprext.writeTo(bb1);

        LightProtoRequired.R lpr = new LightProtoRequired.R();
        lpr.parseFrom(bb1, bb1.readableBytes());

        assertEquals(1, lpr.getA());
        assertEquals(3, lpr.getB());
        assertTrue(lpr.getSerializedSize() < s1);
    }

    private void verify(LightProtoRequired.R lpr, Required.R pbr) throws Exception {
        assertEquals(pbr.getSerializedSize(), lpr.getSerializedSize());

        lpr.writeTo(bb1);
        assertEquals(lpr.getSerializedSize(), bb1.readableBytes());

        pbr.writeTo(CodedOutputStream.newInstance(b2));

        assertArrayEquals(b1, b2);

        LightProtoRequired.R parsed = new LightProtoRequired.R();
        parsed.parseFrom(bb1, bb1.readableBytes());

        assertEquals(pbr.hasA(), parsed.hasA());
        assertEquals(pbr.hasB(), parsed.hasB());
        assertEquals(pbr.hasC(), parsed.hasC());

        if (parsed.hasA()) {
            assertEquals(pbr.getA(), parsed.getA());
        }
        if (parsed.hasB()) {
            assertEquals(pbr.getB(), parsed.getB());
        }
        if (parsed.hasC()) {
            assertEquals(pbr.getC(), parsed.getC());
        }
    }
}
