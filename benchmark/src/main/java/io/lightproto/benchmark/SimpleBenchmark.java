package io.lightproto.benchmark;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import com.google.protobuf.CodedOutputStream;

import io.lightproto.tests.LightProtoTest;
import io.lightproto.tests.Test;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;

@State(Scope.Benchmark)
@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Measurement(iterations = 3)
@Fork(value = 1)
public class SimpleBenchmark {

    byte[] data = new byte[1024];

    @Benchmark
    public void protobufSerialize(Blackhole bh) throws Exception {
        Test.Point.Builder b = Test.Point.newBuilder();
        b.setX(1);
        b.setY(2);
        b.setZ(3);

        Test.Frame.Builder frameBuilder = Test.Frame.newBuilder();
        frameBuilder.setName("xyz");
        frameBuilder.setPoint(b.build());

        Test.Frame frame = frameBuilder.build();

        CodedOutputStream s = CodedOutputStream.newInstance(data);
        frame.writeTo(s);
        bh.consume(b);
        bh.consume(s);
        bh.consume(frame);
    }

    LightProtoTest.Frame frame = new LightProtoTest.Frame();

    ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(1024);

    @Benchmark
    public void lightProtoSerialize(Blackhole bh) throws Exception {
        frame.clear();
        LightProtoTest.Point p = frame.setPoint();
        p.setX(1);
        p.setY(2);
        p.setZ(3);
        frame.setName("xyz");

        p.writeTo(buffer);
        buffer.clear();

        bh.consume(p);
    }

    final static byte[] serialized;

    static {
        Test.Point.Builder b = Test.Point.newBuilder();
        b.setX(1);
        b.setY(2);
        b.setZ(3);

        Test.Frame.Builder frameBuilder = Test.Frame.newBuilder();
        frameBuilder.setName("xyz");
        frameBuilder.setPoint(b.build());

        Test.Frame frame = frameBuilder.build();
        int size = frame.getSerializedSize();

        serialized = new byte[size];
        CodedOutputStream s = CodedOutputStream.newInstance(serialized);
        try {
            frame.writeTo(s);
        } catch (IOException e) {
        }
    }

    private ByteBuf serializeByteBuf = Unpooled.wrappedBuffer(serialized);

    @Benchmark
    public void protobufDeserialize(Blackhole bh) throws Exception {
        Test.Frame.Builder b = Test.Frame.newBuilder().mergeFrom(serialized);
        Test.Frame f = b.build();
        f.getName();
        bh.consume(f);
    }

    @Benchmark
    public void lightProtoDeserialize(Blackhole bh) throws Exception {
        frame.parseFrom(serializeByteBuf, serializeByteBuf.readableBytes());
        serializeByteBuf.resetReaderIndex();
        bh.consume(frame);
    }

    @Benchmark
    public void lightProtoDeserializeReadString(Blackhole bh) throws Exception {
        frame.parseFrom(serializeByteBuf, serializeByteBuf.readableBytes());
        bh.consume(frame.getName());
        serializeByteBuf.resetReaderIndex();
    }

}
