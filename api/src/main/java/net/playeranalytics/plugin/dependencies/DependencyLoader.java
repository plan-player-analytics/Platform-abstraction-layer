package net.playeranalytics.plugin.dependencies;

import net.playeranalytics.plugin.PluginInformation;
import net.playeranalytics.plugin.server.PluginLogger;
import ninja.egg82.maven.Artifact;
import ninja.egg82.maven.Repository;
import ninja.egg82.services.ProxiedURLClassLoader;
import ninja.egg82.utils.InjectUtil;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.*;

public class DependencyLoader {

    private final URLClassLoader classLoader;
    private final PluginLogger pluginLogger;

    private final ExecutorService downloadPool = Executors.newWorkStealingPool(Math.max(4, Runtime.getRuntime().availableProcessors() / 2));
    private final Set<Artifact> dependencies = new HashSet<>();
    private final File dependencyCache;
    private final File libraryFolder;

    public DependencyLoader(URLClassLoader classLoader, PluginLogger pluginLogger, PluginInformation pluginInformation) {
        this.classLoader = new ProxiedURLClassLoader(classLoader);
        this.pluginLogger = pluginLogger;
        dependencyCache = pluginInformation.getDataDirectory().resolve("dependency_cache").toFile();
        libraryFolder = pluginInformation.getDataDirectory().resolve("libraries").toFile();
    }

    public DependencyLoader addDependency(String repositoryAddress, String group, String artifact, String version) throws IOException {
        try {
            Artifact dependency = Artifact.builder(group, artifact, version, dependencyCache)
                    .addRepository(Repository.builder(repositoryAddress).build())
                    .build();
            Stack<Artifact> dependencyLookup = new Stack<>();
            dependencyLookup.add(dependency);
            while (dependencyLookup.peek() != null) {
                Artifact current = dependencyLookup.pop();
                dependencyLookup.addAll(current.getDependencies());
                dependencies.add(current);
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

    public void executeWithDependencyClassloaderContext(Runnable runnable) {
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
        for (Artifact dependency : dependencies) {
            loading.add(scheduleLoading(downloadErrors, dependency));
        }
        CompletableFuture.allOf(loading.toArray(new CompletableFuture[0])).join();
        shutdownPool(downloadErrors);

        if (!downloadErrors.isEmpty()) {
            throwError(downloadErrors);
        }
    }

    private CompletableFuture<Void> scheduleLoading(List<Throwable> downloadErrors, Artifact dependency) {
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

    private void loadDependency(Artifact dependency) throws IOException {
        String artifactCoordinates = dependency.getGroupId() + "-" +
                dependency.getArtifactId() + "-" + dependency.getVersion();
        File artifactFile = libraryFolder.toPath().resolve(artifactCoordinates + ".jar"
        ).toFile();

        if (!dependency.fileExists(artifactFile)) {
            pluginLogger.info("Downloading library: " + dependency.getArtifactId() + "..");
            download(dependency, artifactCoordinates, artifactFile);
        }

        inject(artifactCoordinates, artifactFile);
    }

    private void inject(String artifactCoordinates, File artifactFile) throws IOException {
        try {
            InjectUtil.injectFile(artifactFile, classLoader);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IOException("Failed to load library " + artifactCoordinates + ", " + e.getMessage(), e);
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
