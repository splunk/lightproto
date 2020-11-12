package io.lightproto.generator;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import io.protostuff.parser.Proto;
import io.protostuff.parser.ProtoUtil;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LightProtoGenerator {

    private static final Logger log = LoggerFactory.getLogger(LightProtoGenerator.class);

    public static final List<File> generate(List<File> inputs, File outputDirectory, String classPrefix) throws Exception {
        List<File> generatedFiles = new ArrayList<>();
        Set<String> javaPackages = new HashSet<>();

        for (File input : inputs) {
            Proto proto = new Proto();
            ProtoUtil.loadFrom(input, proto);

            String fileWithoutExtension = Splitter.on(".").splitToList(input.getName()).get(0);
            String className = Util.camelCaseFirstUpper(classPrefix, fileWithoutExtension);

            String javaPackageName = proto.getJavaPackageName();
            String javaDir = Joiner.on('/').join(javaPackageName.split("\\."));
            Path generatedFile = Paths.get(String.format("%s/%s/%s.java", outputDirectory, javaDir, className));
            generatedFile.toFile().getParentFile().mkdirs();

            StringWriter sw = new StringWriter();
            try (PrintWriter pw = new PrintWriter(sw)) {
                LightProto lightProto = new LightProto(proto, className);
                lightProto.generate(pw);
            }

            String formattedCode = Roaster.format(sw.toString());
            try (Writer w = Files.newBufferedWriter(generatedFile)) {
                w.write(formattedCode);
            }
            System.out.println("Wrote " + generatedFile);
            generatedFiles.add(generatedFile.toFile());
            javaPackages.add(javaPackageName);
        }

        // Include the coded class once per every generated java package
        for (String javaPackage : javaPackages) {
            try (InputStream is = LightProtoGenerator.class.getResourceAsStream("/io/lightproto/generator/LightProtoCodec.java")) {
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

    public static void main(String[] args) throws Exception {
        generate(Collections.singletonList(new File("./tests/src/main/proto/PulsarApi.proto")),
                new File("./tests/target/generated-sources/protobuf/java"), "Test");
    }
}
