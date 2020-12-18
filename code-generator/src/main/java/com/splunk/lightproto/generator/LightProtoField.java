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
package com.splunk.lightproto.generator;

import io.protostuff.parser.Field;
import io.protostuff.parser.MessageField;

import java.io.PrintWriter;

public abstract class LightProtoField<FieldType extends Field<?>> {

    protected final FieldType field;
    protected final int index;
    protected final String ccName;

    protected LightProtoField(FieldType field, int index) {
        this.field = field;
        this.index = index;
        this.ccName = Util.camelCase(field.getName());
    }

    public static LightProtoField create(Field field, int index) {
        if (field.isRepeated()) {
            if (field.isMessageField()) {
                return new LightProtoRepeatedMessageField((MessageField) field, index);
            } else if (field.isStringField()) {
                return new LightProtoRepeatedStringField((Field.String) field, index);
            } else if (field.isNumberField() || field.isEnumField() || field.isBoolField()) {
                return new LightProtoRepeatedNumberField(field, index);
            } else if (field.isBytesField()) {
                return new LightProtoRepeatedBytesField((Field.Bytes) field, index);
            }
        } else if (field.isMessageField()) {
            return new LightProtoMessageField((MessageField) field, index);
        } else if (field.isBytesField()) {
            return new LightProtoBytesField((Field.Bytes) field, index);
        } else if (field.isStringField()) {
            return new LightProtoStringField((Field.String) field, index);
        } else if (field.isNumberField() || field.isEnumField()) {
            return new LightProtoNumberField(field, index);
        } else if (field.isBoolField()) {
            return new LightProtoBooleanField(field, index);
        }

        throw new IllegalArgumentException("Unknown field: " + field);
    }

    public int index() {
        return index;
    }

    public boolean isRepeated() {
        return field.isRepeated();
    }

    public boolean isRequired() {
        return field.isRequired();
    }

    public void docs(PrintWriter w) {
        field.getDocs().forEach(d -> {
            w.format("        // %s\n", d);
        });
    }

    abstract public void declaration(PrintWriter w);

    public void tags(PrintWriter w) {
        w.format("        private static final int %s = %d;\n", fieldNumber(), field.getNumber());
        w.format("        private static final int %s = (%s << LightProtoCodec.TAG_TYPE_BITS) | %s;\n", tagName(), fieldNumber(), typeTag());
        w.format("        private static final int %s_SIZE = LightProtoCodec.computeVarIntSize(%s);\n", tagName(), tagName());
        if (!field.isRepeated()) {
            w.format("        private static final int %s = 1 << (%d %% 32);\n", fieldMask(), index);
        }
    }

    public void has(PrintWriter w) {
        w.format("        public boolean %s() {\n", Util.camelCase("has", field.getName()));
        w.format("            return (_bitField%d & %s) != 0;\n", bitFieldIndex(), fieldMask());
        w.format("        }\n");
    }

    abstract public void clear(PrintWriter w);

    public void fieldClear(PrintWriter w, String enclosingType) {
        w.format("        public %s %s() {\n", enclosingType, Util.camelCase("clear", field.getName()));
        w.format("            _bitField%d &= ~%s;\n", bitFieldIndex(), fieldMask());
        clear(w);
        w.format("            return this;\n");
        w.format("        }\n");
    }

    abstract public void setter(PrintWriter w, String enclosingType);

    abstract public void getter(PrintWriter w);

    abstract public void serializedSize(PrintWriter w);

    abstract public void serialize(PrintWriter w);

    abstract public void parse(PrintWriter w);

    abstract public void copy(PrintWriter w);

    public boolean isPackable() {
        return field.isRepeated() && field.isPackable();
    }

    public void parsePacked(PrintWriter w) {
    }

    abstract protected String typeTag();

    protected String tagName() {
        return "_" + Util.upperCase(field.getName(), "tag");
    }

    protected String fieldNumber() {
        return "_" + Util.upperCase(field.getName(), "fieldNumber");
    }

    protected String fieldMask() {
        return "_" + Util.upperCase(field.getName(), "mask");
    }

    protected int bitFieldIndex() {
        return index / 32;
    }
}
