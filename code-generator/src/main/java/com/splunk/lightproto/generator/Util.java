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

import org.jibx.schema.codegen.extend.DefaultNameConverter;
import org.jibx.schema.codegen.extend.NameConverter;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

public class Util {

    public static String camelCase(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String s = parts[i];
            if (s == null || s.isEmpty()) {
                continue;
            }
            if (s.contains("_")) {
                s = LOWER_UNDERSCORE.to(LOWER_CAMEL, s);
            }

            if (i != 0) {
                sb.append(Character.toUpperCase(s.charAt(0)));
                sb.append(s.substring(1));
            } else {
                sb.append(s);
            }
        }

        return sb.toString();
    }

    public static String camelCaseFirstUpper(String... parts) {
        String s = camelCase(parts);
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(s.charAt(0)));
        sb.append(s.substring(1));
        return sb.toString();
    }

    public static String upperCase(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String s = LOWER_CAMEL.to(LOWER_UNDERSCORE, parts[i]);
            if (i != 0) {
                sb.append('_');
            }

            sb.append(s);
        }

        return sb.toString().toUpperCase();
    }

    private static final NameConverter nameTools = new DefaultNameConverter();

    public static String plural(String s) {
        return nameTools.pluralize(s);
    }

    public static String singular(String s) {
        return nameTools.depluralize(s);
    }
}
