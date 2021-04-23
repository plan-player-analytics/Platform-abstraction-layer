package net.playeranalytics.plugin.dependencies;

import me.lucko.jarrelocator.Relocation;
import net.playeranalytics.plugin.PluginInformation;
import ninja.egg82.mvn.JarBuilder;
import ninja.egg82.mvn.JarInjector;
import ninja.egg82.mvn.classloaders.TransparentInjectableClassLoader;
import org.apache.maven.model.building.ModelBuildingException;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.List;

public class DependencyLoader {

    private final URLClassLoader classLoader;

    private final JarInjector jarInjector;

    public DependencyLoader(URLClassLoader classLoader, PluginInformation pluginInformation) {
        this.classLoader = classLoader;

        File dependencyCache = pluginInformation.getDataDirectory().resolve("dep_cache").toFile();

        jarInjector = new JarInjector(dependencyCache);
    }

    public DependencyLoader addDependency(
            String repositoryAddress, String group, String artifact, String version,
            List<Relocation> relocations
    ) {
        try {
            jarInjector.addBuilder(new JarBuilder(group, artifact, version, repositoryAddress));
            for (Relocation relocation : relocations) {
                jarInjector.addRelocation(relocation);
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Incorrect dependency definition", e);
        }
        return this;
    }

    public void load() throws IOException, ModelBuildingException {
        ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            jarInjector.inject(new TransparentInjectableClassLoader(classLoader));
        } finally {
            Thread.currentThread().setContextClassLoader(origClassLoader);
        }
    }

    @Deprecated
    public <T extends Runnable> void executeWithDependencyClassloaderContext(T runnable) {
        ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            runnable.run();
        } finally {
            Thread.currentThread().setContextClassLoader(origClassLoader);
        }
    }
}
