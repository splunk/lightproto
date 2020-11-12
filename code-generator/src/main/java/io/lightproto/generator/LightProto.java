package io.lightproto.generator;

import io.protostuff.parser.Proto;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class LightProto {

    private final Proto proto;
    private final String className;

    private final List<LightProtoEnum> enums;
    private final List<LightProtoMessage> messages;

    public LightProto(Proto proto, String className) {
        this.proto = proto;
        this.className = className;
        this.enums = proto.getEnumGroups().stream().map(LightProtoEnum::new).collect(Collectors.toList());
        this.messages = proto.getMessages().stream().map(LightProtoMessage::new).collect(Collectors.toList());
    }

    public void generate(PrintWriter w) {
        w.format("package %s;\n", proto.getJavaPackageName());
        w.format("public final class %s {\n", className);
        w.format("   private %s() {}\n", className);

        enums.forEach(e -> e.generate(w));
        messages.forEach(m -> m.generate(w));

        w.println("    private static final class StringHolder {");
        w.println("        private String s;");
        w.println("        private int idx;");
        w.println("        private int len;");
        w.println("    }");
        w.println("    private static final class BytesHolder {");
        w.println("        private io.netty.buffer.ByteBuf b;");
        w.println("        private int idx;");
        w.println("        private int len;");
        w.println("    }");

        w.println("}");
    }
}
