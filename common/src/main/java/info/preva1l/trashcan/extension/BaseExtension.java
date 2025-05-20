package info.preva1l.trashcan.extension;

import info.preva1l.trashcan.Version;

/**
 * The base interface for a game extension.
 * <p>
 *     When using either BasePlugin, BaseMod, BaseClientMod or BaseServerMod,
 *     you can access specific methods from your common module.
 * </p>
 * <p>
 * Created on 20/05/2025
 *
 * @author Preva1l
 */
public interface BaseExtension {
    /**
     * Get the instance of your extension.
     *
     * @return the instance.
     */
    static BaseExtension instance() {
        return InstanceHolder.modification;
    }

    /**
     * Reloads your plugin, runs any methods annotated with @PluginReload
     */
    void reload();

    /**
     * Get the current version of your modification.
     *
     * @return the current version.
     */
    Version getCurrentVersion();

    class InstanceHolder {
        static BaseExtension modification;
    }
}
