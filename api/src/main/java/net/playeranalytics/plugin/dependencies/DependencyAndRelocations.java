package net.playeranalytics.plugin.dependencies;

import me.lucko.jarrelocator.Relocation;
import ninja.egg82.maven.Artifact;

import java.util.List;

public class DependencyAndRelocations {

    private final Artifact artifact;
    private final List<Relocation> relocations;

    public DependencyAndRelocations(Artifact artifact, List<Relocation> relocations) {
        this.artifact = artifact;
        this.relocations = relocations;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public List<Relocation> getRelocations() {
        return relocations;
    }
}
