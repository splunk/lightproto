

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
Benchmark                                         Mode  Cnt   Score    Error   Units
ProtoBenchmark.lightProtoDeserialize             thrpt    3  7.711 ± 2.740  ops/us
ProtoBenchmark.lightProtoSerialize               thrpt    3  3.895 ± 1.255  ops/us
ProtoBenchmark.protobufDeserialize               thrpt    3  2.310 ± 0.391  ops/us
ProtoBenchmark.protobufSerialize                 thrpt    3  2.077 ± 0.696  ops/us
SimpleBenchmark.lightProtoDeserialize            thrpt    3  35.241 ±  7.328  ops/us
SimpleBenchmark.lightProtoDeserializeReadString  thrpt    3  15.976 ±  1.999  ops/us
SimpleBenchmark.lightProtoSerialize              thrpt    3  35.568 ± 15.120  ops/us
SimpleBenchmark.protobufDeserialize              thrpt    3   7.901 ±  0.472  ops/us
SimpleBenchmark.protobufSerialize                thrpt    3  18.551 ±  2.959  ops/us
StringEncodingBenchmark.jdkEncoding              thrpt   10  14.862 ±  0.538  ops/us
StringEncodingBenchmark.nettyEncoding            thrpt   10  25.161 ±  1.313  ops/us
```