package net.playeranalytics.plugin.dependencies;

import me.lucko.jarrelocator.JarRelocator;
import me.lucko.jarrelocator.Relocation;
import net.playeranalytics.plugin.PluginInformation;
import net.playeranalytics.plugin.server.PluginLogger;
import ninja.egg82.maven.Artifact;
import ninja.egg82.maven.Repository;
import ninja.egg82.maven.Scope;
import ninja.egg82.utils.InjectUtil;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class DependencyLoader {

    private final URLClassLoader classLoader;
    private final PluginLogger pluginLogger;

    private final ExecutorService downloadPool = Executors.newWorkStealingPool(Math.max(4, Runtime.getRuntime().availableProcessors() / 2));
    private final Set<DependencyAndRelocations> dependencies = new HashSet<>();
    private final File dependencyCache;
    private final File libraryFolder;

    public DependencyLoader(URLClassLoader classLoader, PluginLogger pluginLogger, PluginInformation pluginInformation) {
        this.classLoader = classLoader;
        this.pluginLogger = pluginLogger;
        dependencyCache = pluginInformation.getDataDirectory().resolve("dependency_cache").toFile();
        libraryFolder = pluginInformation.getDataDirectory().resolve("libraries").toFile();
    }

    public DependencyLoader addDependency(
            String repositoryAddress, String group, String artifact, String version,
            List<Relocation> relocations
    ) throws IOException {
        try {
            Artifact dependency = Artifact.builder(group, artifact, version, dependencyCache, Scope.COMPILE)
                    .addRepository(Repository.builder(repositoryAddress).build())
                    .build();
            Stack<DependencyAndRelocations> dependencyLookup = new Stack<>();
            dependencyLookup.add(new DependencyAndRelocations(dependency, relocations));
            while (!dependencyLookup.isEmpty() && dependencyLookup.peek() != null) {
                DependencyAndRelocations current = dependencyLookup.pop();
                Artifact currentArtifact = current.getArtifact();
                if (currentArtifact.getScope() == Scope.COMPILE) {
                    currentArtifact.getDependencies().stream()
                            .map(dependencyArtifact ->
                                    new DependencyAndRelocations(dependencyArtifact, relocations))
                            .forEach(dependencyLookup::add);
                    dependencies.add(current);
                }
            }
        } catch (URISyntaxException | XPathExpressionException | SAXException e) {
            throw new IllegalArgumentException("Incorrect dependency definition", e);
        }
        return this;
    }

    public void load() throws IOException {
        ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            loadDependencies();
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

    private void loadDependencies() throws IOException {
        List<CompletableFuture<?>> loading = new ArrayList<>();
        List<Throwable> downloadErrors = new CopyOnWriteArrayList<>();
        for (DependencyAndRelocations dependency : dependencies) {
            loading.add(scheduleLoading(downloadErrors, dependency));
        }
        CompletableFuture.allOf(loading.toArray(new CompletableFuture[0])).join();
        shutdownPool(downloadErrors);

        if (!downloadErrors.isEmpty()) {
            throwError(downloadErrors);
        }
    }

    private CompletableFuture<Void> scheduleLoading(List<Throwable> downloadErrors, DependencyAndRelocations dependency) {
        return CompletableFuture.runAsync(() -> {
            try {
                loadDependency(dependency);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }, downloadPool).exceptionally(throwable -> {
            downloadErrors.add(throwable);
            return null;
        });
    }

    private void shutdownPool(List<Throwable> downloadErrors) {
        downloadPool.shutdown();
        try {
            if (!downloadPool.awaitTermination(1, TimeUnit.HOURS)) {
                downloadErrors.add(0, new IOException("Download timeout exceeded"));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void throwError(List<Throwable> downloadErrors) throws IOException {
        IOException firstError = new IOException("Failed to download all dependencies (see suppressed exceptions for why it failed)");
        for (Throwable downloadError : downloadErrors) {
            firstError.addSuppressed(downloadError.getCause());
        }
        throw firstError;
    }

    private void loadDependency(DependencyAndRelocations toLoad) throws IOException {
        Artifact dependency = toLoad.getArtifact();
        String artifactCoordinates = dependency.getGroupId() + "-" +
                dependency.getArtifactId() + "-" + dependency.getVersion();
        Files.createDirectories(libraryFolder.toPath());
        File artifactFile = libraryFolder.toPath().resolve(artifactCoordinates + ".jar").toFile();
        File relocatedArtifactFile = libraryFolder.toPath().resolve(artifactCoordinates + "-relocated.jar").toFile();

        if (!relocatedArtifactFile.exists() && !toLoad.getRelocations().isEmpty()) {
            if (!dependency.fileExists(artifactFile)) {
                pluginLogger.info("Downloading library: " + dependency.getArtifactId() + "..");
                download(dependency, artifactCoordinates, artifactFile);
            }
            new JarRelocator(artifactFile, relocatedArtifactFile, toLoad.getRelocations()).run();

            inject(relocatedArtifactFile);
        } else if (relocatedArtifactFile.exists()) {
            inject(relocatedArtifactFile);
        } else {
            if (!dependency.fileExists(artifactFile)) {
                pluginLogger.info("Downloading library: " + dependency.getArtifactId() + "..");
                download(dependency, artifactCoordinates, artifactFile);
            }

            inject(artifactFile);
        }
    }

    private void inject(File artifactFile) throws IOException {
        try {
            InjectUtil.injectFile(artifactFile, classLoader);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IOException("Failed to download " + artifactFile + ", " + e.getMessage(), e);
        }
    }

    private void download(Artifact dependency, String artifactCoordinates, File artifactFile) throws IOException {
        try {
            dependency.downloadJar(artifactFile);
        } catch (IOException e) {
            StringBuilder repositories = new StringBuilder();
            for (Repository repository : dependency.getRepositories()) {
                repositories.append(" ").append(repository.getURL());
            }
            pluginLogger.warn("Failed to download " + artifactCoordinates + " from repositories (" + repositories + "), " + e.getMessage());
            throw e;
        }
    }
}
