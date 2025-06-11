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

/**
 * A plugin loader that automatically grabs and loads libraries from paper-libraries.json.
 * <p>
 * Created on 11/04/2025
 *
 * @author Preva1l
 */
@SuppressWarnings({"UnstableApiUsage", "unused"})
public class BaseLibraryLoader implements PluginLoader {
    private final Gson gson = new Gson();

    @Override
    public final void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        Libraries libraries = load();
        for (Repository repository : libraries.repositories().values()) {
            MavenLibraryResolver resolver = new MavenLibraryResolver();
            var eclipseRepo = new RemoteRepository.Builder(repository.name(), "default", repository.url()).build();
            resolver.addRepository(eclipseRepo);
            for (Dependency dependency : repository.dependencies()) {
                var eclipseDep = new org.eclipse.aether.graph.Dependency(new DefaultArtifact(dependency.asGavCoordinate()), null);

                if (dependency.remap()) {
                    MavenLibraryResolver remappingResolver = new MavenLibraryResolver();
                    remappingResolver.addDependency(eclipseDep);
                    remappingResolver.addRepository(eclipseRepo);
                    register(classpathBuilder, remappingResolver, true);
                    continue;
                }

                resolver.addDependency(eclipseDep);
                register(classpathBuilder, resolver, false);
            }
        }
    }

    private void register(PluginClasspathBuilder classpathBuilder, MavenLibraryResolver resolver, boolean remap) {
        if (!remap) {
            classpathBuilder.addLibrary(resolver);
            return;
        }
        classpathBuilder.addLibrary(store ->
                resolver.register(path -> {
                    Path modified = path;
                    try {
                        Path tempFile = Files.createTempFile("remapped-", ".jar");
                        modifyManifest(path, tempFile);
                        modified = tempFile;
                    } catch (IOException ignored) {}
                    store.addLibrary(modified);
                })
        );
    }

    private Libraries load() {
        Libraries libraries = new Libraries(new HashMap<>());
        libraries.merge(load("libraries.json"));
        libraries.merge(load("trashcan-libraries.json"));
        // re add programmatic loading
        return libraries;
    }

    private Libraries load(@NotNull String file) {
        Libraries libraries;
        try (var in = getClass().getResourceAsStream("/" + file)) {
            if (in != null)
                libraries = gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), Libraries.class);
            else
                libraries = new Libraries(new HashMap<>());
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage(), e);
            libraries = new Libraries(new HashMap<>());
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
}