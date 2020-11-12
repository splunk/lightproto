package io.lightproto.generator;

import io.protostuff.parser.Field;

import java.io.PrintWriter;

import static io.lightproto.generator.Util.camelCase;

public class LightProtoBooleanField extends LightProtoNumberField {

    public LightProtoBooleanField(Field<?> field, int index) {
        super(field, index);
    }

    @Override
    public void getter(PrintWriter w) {
        w.format("        public %s %s() {\n", field.getJavaType(), camelCase("is", ccName));
        if (!field.isDefaultValueSet()) {
            w.format("            if (!%s()) {\n", camelCase("has", ccName));
            w.format("                throw new IllegalStateException(\"Field '%s' is not set\");\n", field.getName());
            w.format("            }\n");
        }
        w.format("            return %s;\n", ccName);
        w.format("        }\n");
    }
}
