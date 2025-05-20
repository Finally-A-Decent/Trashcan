package info.preva1l.trashcan.extension.libloader;

import com.google.gson.Gson;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A plugin loader that automatically grabs and loads libraries from paper-libraries.json.
 * <p>
 *     To add libraries programmatically override the {@link BaseLibraryLoader#customDependencies()} and {@link BaseLibraryLoader#customRepositories()}
 * </p>
 *
 * Created on 11/04/2025
 *
 * @author Preva1l
 */
@SuppressWarnings({"UnstableApiUsage", "unused"})
public class BaseLibraryLoader implements PluginLoader {
    private final Gson gson = new Gson();

    public Collection<Dependency> customDependencies() {
        return Collections.emptyList();
    }

    public Collection<Repository> customRepositories() {
        return Collections.emptyList();
    }

    protected Predicate<Path> remapPredicate() {
        return p -> true;
    }

//    @Override
//    public final void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
//        MavenLibraryResolver resolver = new MavenLibraryResolver();
//        PluginLibraries pluginLibraries = load();
//        pluginLibraries.asDependencies().forEach(resolver::addDependency);
//        pluginLibraries.asRepositories().forEach(resolver::addRepository);
//        resolver.register(path -> classpathBuilder.getContext().getLogger().info(path.toString()));
//        classpathBuilder.addLibrary(resolver);
//    }

    @Override
    public final void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        PluginLibraries pluginLibraries = load();

        pluginLibraries.asDependencies().forEach(resolver::addDependency);
        pluginLibraries.asRepositories().forEach(resolver::addRepository);

        classpathBuilder.addLibrary(store ->
                resolver.register(path -> {
                    Path modified = path;
                    if (remapPredicate().test(modified)) {
                        try {
                            Path tempFile = Files.createTempFile("remapped-", ".jar");
                            modifyManifest(path, tempFile);
                            modified = tempFile;
                        } catch (IOException ignored) {
                        }
                    }
                    store.addLibrary(modified);
                })
        );
    }

    private PluginLibraries load() {
        PluginLibraries libraries = new PluginLibraries(new HashMap<>(), new ArrayList<>());
        libraries.merge(load("trashcan-libraries.json"));
        libraries.merge(load("paper-libraries.json"));
        // Dumpster
        libraries.merge(load("dumpster-libraries.json"));
        libraries.merge(load("dumpster-mongo-libraries.json"));
        libraries.addRepos(customRepositories());
        libraries.addDeps(customDependencies());
        return libraries;
    }

    private PluginLibraries load(@NotNull String file) {
        PluginLibraries libraries;
        try (var in = getClass().getResourceAsStream("/" + file)) {
            if (in == null) return new PluginLibraries(new HashMap<>(), Collections.emptyList());
            libraries = gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), PluginLibraries.class);
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage(), e);
            libraries = new PluginLibraries(new HashMap<>(), Collections.emptyList());
        }
        return libraries;
    }

    private void modifyManifest(Path file, Path modifiedFile) throws IOException {
        Path tempJarPath = Files.createTempFile("temp-", ".jar");

        try (JarFile jarFile = new JarFile(file.toFile());
             JarOutputStream tempJarOutputStream = new JarOutputStream(Files.newOutputStream(tempJarPath))) {

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (JarFile.MANIFEST_NAME.equals(entry.getName())) continue;

                try (InputStream entryInputStream = jarFile.getInputStream(entry)) {
                    tempJarOutputStream.putNextEntry(new JarEntry(entry.getName()));
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = entryInputStream.read(buffer)) != -1) {
                        tempJarOutputStream.write(buffer, 0, bytesRead);
                    }
                    tempJarOutputStream.closeEntry();
                }
            }

            Manifest manifest = jarFile.getManifest();
            if (manifest == null) {
                manifest = new Manifest();
            }
            manifest.getMainAttributes().putValue("paperweight-mappings-namespace", "spigot");

            tempJarOutputStream.putNextEntry(new JarEntry(JarFile.MANIFEST_NAME));
            manifest.write(tempJarOutputStream);
            tempJarOutputStream.closeEntry();
        }

        Files.move(tempJarPath, modifiedFile, StandardCopyOption.REPLACE_EXISTING);
    }

    private record PluginLibraries(Map<String, String> repositories, List<String> dependencies) {
        public void addDeps(Collection<Dependency> dependencies) {
            this.dependencies.addAll(dependencies.stream().map(Dependency::asGavCoordinate).toList());
        }

        public void addRepos(Collection<Repository> repositories) {
            this.repositories.putAll(
                    repositories.stream()
                            .collect(Collectors.toMap(Repository::name, Repository::url))
            );
        }

        public void merge(PluginLibraries libraries) {
            repositories.putAll(libraries.repositories);
            dependencies.addAll(libraries.dependencies);
        }

        public Stream<org.eclipse.aether.graph.Dependency> asDependencies() {
            return dependencies.stream()
                    .map(d -> new org.eclipse.aether.graph.Dependency(new DefaultArtifact(d), null));
        }

        public Stream<RemoteRepository> asRepositories() {
            return repositories.entrySet().stream()
                    .map(e -> new RemoteRepository.Builder(e.getKey(), "default", e.getValue()).build());
        }
    }
}