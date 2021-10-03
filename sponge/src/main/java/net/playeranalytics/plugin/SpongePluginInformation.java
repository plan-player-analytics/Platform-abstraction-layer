package net.playeranalytics.plugin;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.spongepowered.plugin.PluginContainer;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpongePluginInformation implements PluginInformation {

    private final PluginContainer plugin;
    private final File dataFolder;

    public SpongePluginInformation(PluginContainer plugin, File dataFolder) {
        this.plugin = plugin;
        this.dataFolder = dataFolder;
    }

    @Override
    public InputStream getResourceFromJar(String byName) {
        return getClass().getResourceAsStream("/" + byName);
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public String getVersion() {
        // Implementation to convert a ArtifactVersion to String, regardless of implementation
        ArtifactVersion artifactVersion = plugin.metadata().version();

        int major = artifactVersion.getMajorVersion();
        int minor = artifactVersion.getMinorVersion();
        int incremental = artifactVersion.getIncrementalVersion();

        String main = Stream.of(
                        major == 0 ? null : major,
                        minor,
                        incremental == 0 ? null : incremental
                )
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining("."));

        StringBuilder stringBuilder = new StringBuilder(main);

        int build = artifactVersion.getBuildNumber();
        if (build != 0) {
            stringBuilder.append('-').append(build);
        }

        String qualifier = artifactVersion.getQualifier();
        if (qualifier != null) {
            stringBuilder.append('-').append(qualifier);
        }

        return stringBuilder.toString();
    }
}
