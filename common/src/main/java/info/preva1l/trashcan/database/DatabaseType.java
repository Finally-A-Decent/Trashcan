package info.preva1l.trashcan.database;

public enum DatabaseType {
    MONGO("mongodb", "MongoDB"),
    MYSQL("mysql", "MySQL"),
    MARIADB("mariadb", "MariaDB"),
    SQLITE("sqlite", "SQLite"),
    POSTGRES("postgres", "PostgreSQL"),
    ;

    private final String id;
    private final String friendlyName;

    DatabaseType(String id, String friendlyName) {
        this.id = id;
        this.friendlyName = friendlyName;
    }

    public String getId() {
        return id;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}