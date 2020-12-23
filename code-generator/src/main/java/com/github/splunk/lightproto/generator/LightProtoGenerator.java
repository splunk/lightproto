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
package com.github.splunk.lightproto.generator;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import io.protostuff.parser.Proto;
import io.protostuff.parser.ProtoUtil;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LightProtoGenerator {

    public static List<File> generate(List<File> inputs, File outputDirectory,
                                      String classPrefix, boolean useOuterClass) throws Exception {
        List<File> generatedFiles = new ArrayList<>();
        Set<String> javaPackages = new HashSet<>();

        for (File input : inputs) {
            Proto proto = new Proto();
            ProtoUtil.loadFrom(input, proto);

            String fileWithoutExtension = Splitter.on(".").splitToList(input.getName()).get(0);
            String outerClassName = Util.camelCaseFirstUpper(classPrefix, fileWithoutExtension);

            String javaPackageName = proto.getJavaPackageName();
            String javaDir = Joiner.on('/').join(javaPackageName.split("\\."));
            Path targetDir = Paths.get(String.format("%s/%s", outputDirectory, javaDir));

            LightProto lightProto = new LightProto(proto, outerClassName, useOuterClass);
            generatedFiles.addAll(lightProto.generate(targetDir.toFile()));

            javaPackages.add(javaPackageName);
        }

        // Include the coded class once per every generated java package
        for (String javaPackage : javaPackages) {
            try (InputStream is = LightProtoGenerator.class.getResourceAsStream("/com/github/splunk/lightproto/generator/LightProtoCodec.java")) {
                JavaClassSource codecClass = (JavaClassSource) Roaster.parse(is);
                codecClass.setPackage(javaPackage);

                String javaDir = Joiner.on('/').join(javaPackage.split("\\."));
                Path codecFile = Paths.get(String.format("%s/%s/LightProtoCodec.java", outputDirectory, javaDir));
                try (Writer w = Files.newBufferedWriter(codecFile)) {
                    w.write(codecClass.toString());
                }
            }
        }

        return generatedFiles;
    }
}
