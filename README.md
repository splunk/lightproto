

### Light Proto serialization

### Features

 1. Generate the fastest possible Java code for Protobuf SerDe
 1. 100% Compatible with proto2 definition and wire protocol
 1. Zero-copy deserialization using Netty ByteBuf
 1. Deserialize from direct memory
 1. Zero heap allocations in serialization / deserialization
 1. Lazy deserialization of strings and bytes
 1. Reusable mutable objects
 1. No runtime dependency library
 1. Java based code generator with Maven plugin

### Benchmark

```
java -jar benchmark/target/benchmarks.jar

Benchmark                                         Mode  Cnt   Score    Error   Units
ProtoBenchmark.lightProtoDeserialize             thrpt    3   8.445 ±  1.734  ops/us
ProtoBenchmark.lightProtoSerialize               thrpt    3   4.056 ±  1.628  ops/us
ProtoBenchmark.protobufDeserialize               thrpt    3   2.465 ±  0.682  ops/us
ProtoBenchmark.protobufSerialize                 thrpt    3   2.242 ±  0.186  ops/us
SimpleBenchmark.lightProtoDeserialize            thrpt    3  37.414 ±  4.980  ops/us
SimpleBenchmark.lightProtoDeserializeReadString  thrpt    3  17.367 ±  1.790  ops/us
SimpleBenchmark.lightProtoSerialize              thrpt    3  35.473 ±  6.325  ops/us
SimpleBenchmark.protobufDeserialize              thrpt    3   8.255 ±  1.104  ops/us
SimpleBenchmark.protobufSerialize                thrpt    3  18.960 ±  4.626  ops/us
StringEncodingBenchmark.jdkEncoding              thrpt   10  14.031 ±  0.394  ops/us
StringEncodingBenchmark.jdkEncodingAscii         thrpt   10  19.279 ±  0.448  ops/us
StringEncodingBenchmark.nettyEncoding            thrpt   10  27.273 ±  0.988  ops/us
StringEncodingBenchmark.nettyEncodingAscii       thrpt   10  36.140 ±  0.693  ops/us
```