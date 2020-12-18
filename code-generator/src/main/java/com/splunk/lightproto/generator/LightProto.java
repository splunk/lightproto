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

import io.protostuff.parser.Proto;
import org.jboss.forge.roaster.Roaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LightProto {

    private static final Logger log = LoggerFactory.getLogger(LightProto.class);
    private final Proto proto;
    private final String outerClassName;
    private final boolean useOuterClass;
    private final List<LightProtoEnum> enums;
    private final List<LightProtoMessage> messages;

    public LightProto(Proto proto, String outerClassName, boolean useOuterClass) {
        this.proto = proto;
        this.outerClassName = outerClassName;
        this.useOuterClass = useOuterClass;
        this.enums = proto.getEnumGroups().stream().map(LightProtoEnum::new).collect(Collectors.toList());
        this.messages = proto.getMessages().stream().map(m -> new LightProtoMessage(m, useOuterClass)).collect(Collectors.toList());
    }

    public List<File> generate(File directory) throws IOException {
        directory.mkdirs();

        if (useOuterClass) {
            return generateWithSingleOuterClass(directory);
        } else {
            return generateIndividualClasses(directory);
        }
    }

    public List<File> generateIndividualClasses(File outDirectory) throws IOException {
        List<File> generatedFiles = new ArrayList<>();
        for (LightProtoEnum e : enums) {
            File file = new File(outDirectory, e.getName() + ".java");
            StringWriter sw = new StringWriter();
            try (PrintWriter pw = new PrintWriter(sw)) {
                pw.format("package %s;\n", proto.getJavaPackageName());
                e.generate(pw);
            }

            formatAndWrite(file, sw.toString());
            log.info("LightProto generated enum {}", file);
            generatedFiles.add(file);
        }

        for (LightProtoMessage m : messages) {
            File file = new File(outDirectory, m.getName() + ".java");
            StringWriter sw = new StringWriter();
            try (PrintWriter pw = new PrintWriter(sw)) {
                pw.format("package %s;\n", proto.getJavaPackageName());
                m.generate(pw);
            }

            formatAndWrite(file, sw.toString());
            log.info("LightProto generated class {}", file);
            generatedFiles.add(file);
        }

        return generatedFiles;
    }

    public List<File> generateWithSingleOuterClass(File outDirectory) throws IOException {
        File outFile = new File(outDirectory, outerClassName + ".java");

        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            pw.format("package %s;\n", proto.getJavaPackageName());
            pw.format("public final class %s {\n", outerClassName);
            pw.format("   private %s() {}\n", outerClassName);

            enums.forEach(e -> e.generate(pw));
            messages.forEach(m -> m.generate(pw));

            pw.println("}");
        }

        formatAndWrite(outFile, sw.toString());

        log.info("LightProto generated {}", outFile);
        return Collections.singletonList(outFile);
    }

    private void formatAndWrite(File file, String content) throws IOException {
        String formattedCode = Roaster.format(content);
        try (Writer w = Files.newBufferedWriter(file.toPath())) {
            w.write(formattedCode);
        }
    }
}
