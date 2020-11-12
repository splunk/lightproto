package io.lightproto.maven.plugin;

import io.lightproto.generator.LightProtoGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Goal which generates Java code from the proto definition
 */
@Mojo(
        name = "generate",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        threadSafe = true
)
public class LightProtoMojo extends AbstractMojo {

    @Parameter(property = "classPrefix", defaultValue = "", required = false)
    private String classPrefix;

    @Parameter(property = "sources", required = false)
    private List<File> sources;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    private void generate(List<File> protoFiles, File outputDirectory) throws MojoExecutionException {
        try {
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }

            List<File> generatedFiles = LightProtoGenerator.generate(protoFiles, outputDirectory, classPrefix);
            getLog().info("Generated Java files: " + generatedFiles);
        } catch (Exception e) {
            getLog().error("Failed to generate lightproto code for " + protoFiles + ": " + e.getMessage(), e);
            throw new MojoExecutionException("Failed to generate lightproto code for " + protoFiles, e);

        }
    }

    public void execute() throws MojoExecutionException {
        File baseDir = project.getBasedir();
        File targetDir = new File(project.getBuild().getDirectory());

        if (sources == null || sources.isEmpty()) {
            File[] mainFilesArray = new File(baseDir, "src/main/proto").listFiles((dir, name) -> name.endsWith(".proto"));
            if (mainFilesArray != null && mainFilesArray.length > 0) {
                List<File> mainFiles = Arrays.asList(mainFilesArray);
                File generatedSourcesDir = new File(targetDir, "generated-sources/protobuf/java");
                generate(mainFiles, new File(targetDir, "generated-sources/protobuf/java"));

                project.addCompileSourceRoot(generatedSourcesDir.toString());
            }

            File[] testFilesArray = new File(baseDir, "src/test/proto").listFiles((dir, name) -> name.endsWith(".proto"));
            if (testFilesArray != null && testFilesArray.length > 0) {
                List<File> testFiles = Arrays.asList(testFilesArray);
                File generatedTestSourcesDir = new File(targetDir, "generated-test-sources/protobuf/java");
                generate(testFiles, generatedTestSourcesDir);

                project.addTestCompileSourceRoot(generatedTestSourcesDir.toString());
            }
        } else {
            File generatedSourcesDir = new File(targetDir, "generated-sources/protobuf/java");
            generate(sources, generatedSourcesDir);
            project.addCompileSourceRoot(generatedSourcesDir.toString());
        }
    }
}