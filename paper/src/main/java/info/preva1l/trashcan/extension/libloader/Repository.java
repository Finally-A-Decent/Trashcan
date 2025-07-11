package info.preva1l.trashcan.extension.libloader;

import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a maven repository.
 * <p>
 * Created on 11/04/2025
 *
 * @author Preva1l
 */
public record Repository(
        String name,
        String url,
        List<Dependency> dependencies
) {
    private static int GENERIC_INDEX = 0;

    public static Repository named(
            final String name,
            @RegExp
            @Pattern("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)")
            final String url
    ) {
        return new Repository(name, url, new ArrayList<>());
    }

    public static Repository url(
            @RegExp
            @Pattern("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)")
            final String url
    ) {
        return new Repository(GENERIC_INDEX == 0 ? "maven" : "maven" + GENERIC_INDEX++, url, new ArrayList<>());
    }

    public Repository self() {
        return this;
    }
}
