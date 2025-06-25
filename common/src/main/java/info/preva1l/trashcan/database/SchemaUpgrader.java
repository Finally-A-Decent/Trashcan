package info.preva1l.trashcan.database;

/**
 * Created on 25/06/2025
 *
 * @author Preva1l
 */
public interface SchemaUpgrader {
    void upgrade();

    boolean needsUpgrade();
}
