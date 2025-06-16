package info.preva1l.trashcan;

import java.util.Map;

public record Libraries(
        Map<String, Repository> repositories
) {}