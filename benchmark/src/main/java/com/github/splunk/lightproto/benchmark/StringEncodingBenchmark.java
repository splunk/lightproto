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
package com.github.splunk.lightproto.benchmark;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;

@State(Scope.Benchmark)
@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Measurement(iterations = 10)
@Fork(value = 1)
public class StringEncodingBenchmark {

    private static final String testString = "UTF16 Ελληνικά Русский 日本語";
    private static final String testStringAscii = "Neque porro quisquam est qui dolorem ipsum";

    @Benchmark
    public void jdkEncoding(Blackhole bh) throws Exception {
        byte[] bytes = testString.getBytes(StandardCharsets.UTF_8);
        bh.consume(bytes);
    }

    ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(1024);

    @Benchmark
    public void nettyEncoding(Blackhole bh) throws Exception {
        buffer.clear();
        ByteBufUtil.writeUtf8(buffer, testString);
        bh.consume(buffer);
    }

    @Benchmark
    public void jdkEncodingAscii(Blackhole bh) throws Exception {
        byte[] bytes = testStringAscii.getBytes(StandardCharsets.UTF_8);
        bh.consume(bytes);
    }

    @Benchmark
    public void nettyEncodingAscii(Blackhole bh) throws Exception {
        buffer.clear();
        ByteBufUtil.writeUtf8(buffer, testStringAscii);
        bh.consume(buffer);
    }
}
