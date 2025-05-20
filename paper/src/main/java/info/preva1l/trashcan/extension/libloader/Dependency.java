package info.preva1l.trashcan.extension.libloader;

import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;

/**
 * Represents a maven dependency.
 * <p>
 * Created on 11/04/2025
 *
 * @author Preva1l
 */
public record Dependency(
        String group,
        String artifact,
        String version
) {
    /**
     * Create a dependency reference from its general data.
     *
     * @param group the group of the dependency.
     * @param artifact the artifact of the dependency.
     * @param version the version of the dependency.
     * @return the dependency reference.
     */
    public static Dependency from(final String group, final String artifact, final String version) {
        return new Dependency(group, artifact, version);
    }

    /**
     * Create a dependency reference from a GAV (Group Artifact Version) coordinate/identifier.
     *
     * @param gavCoordinate the GAV coordinate.
     * @return the dependency reference.
     */
    public static Dependency gav(@RegExp @Pattern("([\\w.]+):([\\w\\-]+):([\\w\\-.]+)") final String gavCoordinate) {
        String[] split = gavCoordinate.split(":");
        return new Dependency(split[0], split[1], split[2]);
    }

    public String asGavCoordinate() {
        return group + ":" + artifact + ":" + version;
    }
}
