package info.preva1l.trashcan;

import javax.annotation.Nullable;

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
        String version,
        @Nullable String snapshot,
        boolean remap
) {
    public Dependency remapped(boolean remap) {
        return new Dependency(group, artifact, version, snapshot, remap);
    }

    public String getFullVersion() {
        return version.replace("-SNAPSHOT", "") + (snapshot != null ? "-" + snapshot : "");
    }

    public String asGavCoordinate() {
        return group + ":" + artifact + ":" + getFullVersion();
    }
}
