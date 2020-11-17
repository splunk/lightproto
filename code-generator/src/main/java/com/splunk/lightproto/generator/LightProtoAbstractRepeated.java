package com.splunk.lightproto.generator;

import io.protostuff.parser.Field;

import java.io.PrintWriter;

import static com.splunk.lightproto.generator.Util.camelCase;

public abstract class LightProtoAbstractRepeated<FieldType extends Field<?>> extends LightProtoField<FieldType> {

    protected final String pluralName;
    protected final String singularName;

    public LightProtoAbstractRepeated(FieldType field, int index) {
        super(field, index);
        this.pluralName = Util.plural(ccName);
        this.singularName = Util.singular(ccName);
    }

    public void has(PrintWriter w) {
    }

    public void fieldClear(PrintWriter w, String enclosingType) {
        w.format("        public %s %s() {\n", enclosingType, Util.camelCase("clear", field.getName()));
        clear(w);
        w.format("            return this;\n");
        w.format("        }\n");
    }
}
