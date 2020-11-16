package com.splunk.lightproto.generator;

import io.protostuff.parser.EnumGroup;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class LightProtoEnum {
    private final EnumGroup eg;

    public LightProtoEnum(EnumGroup eg) {
        this.eg = eg;
    }

    public String getName() {
        return eg.getName();
    }

    public void generate(PrintWriter w) {
        w.format("    public enum %s {\n", eg.getName());
        eg.getSortedValues().forEach(v -> {
            w.format("        %s(%d),\n", v.getName(), v.getNumber());
        });
        w.println("        ;");
        w.println("        private final int value;");
        w.format("        private %s(int value) {\n", eg.getName());
        w.println("            this.value = value;");
        w.println("        }");
        w.println("        public int getValue() {");
        w.println("            return value;");
        w.println("        }");

        w.format("        public static %s valueOf(int n) {\n", eg.getName());
        w.format("            switch (n) {\n");
        eg.getSortedValues().forEach(v -> {
            w.format("                case %d: return %s;\n", v.getNumber(), v.getName());
        });
        w.format("                default: throw new IllegalArgumentException(\"Invalid value \" + n + \" for %s enum\");\n", eg.getName());
        w.println("            }");
        w.println("        }");
        w.println("    }");
        w.println();
    }
}
