package info.preva1l.trashcan.extension.libloader;

import java.util.Map;

public record Libraries(
        Map<String, Repository> repositories
) {
    public void merge(Libraries other) {
        other.repositories().forEach((name, repo) -> {
            this.repositories.compute(name, (currentName, currentRepo) -> {
                if (currentRepo == null) return repo;
                currentRepo.dependencies().addAll(repo.dependencies());
                return currentRepo;
            });
        });
    }
}