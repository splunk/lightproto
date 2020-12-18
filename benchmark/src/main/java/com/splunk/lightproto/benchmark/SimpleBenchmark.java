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
package com.splunk.lightproto.benchmark;

import com.google.protobuf.CodedOutputStream;
import com.splunk.lightproto.tests.Frame;
import com.splunk.lightproto.tests.Point;
import com.splunk.lightproto.tests.Test;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Measurement(iterations = 3)
@Fork(value = 1)
public class SimpleBenchmark {

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

    byte[] data = new byte[1024];
    Frame frame = new Frame();
    ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(1024);
    private ByteBuf serializeByteBuf = Unpooled.wrappedBuffer(serialized);

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

    @Benchmark
    public void lightProtoSerialize(Blackhole bh) throws Exception {
        frame.clear();
        Point p = frame.setPoint();
        p.setX(1);
        p.setY(2);
        p.setZ(3);
        frame.setName("xyz");

        p.writeTo(buffer);
        buffer.clear();

        bh.consume(p);
    }

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
