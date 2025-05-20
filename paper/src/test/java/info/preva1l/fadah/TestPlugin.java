package info.preva1l.fadah;

import info.preva1l.trashcan.extension.BasePlugin;
import info.preva1l.trashcan.extension.annotations.PluginDisable;
import info.preva1l.trashcan.extension.annotations.PluginEnable;
import info.preva1l.trashcan.extension.annotations.PluginLoad;

/**
 * Created on 25/04/2025
 *
 * @author Preva1l
 */
public class TestPlugin extends BasePlugin {
    public static TestPlugin instance;

    boolean loaded = false;
    boolean enabled = false;
    boolean disabled = false;

    public TestPlugin() {
        instance = this;
    }

    @PluginLoad
    public void load() {
        loaded = true;
    }

    @PluginEnable
    public void enable() {
        enabled = true;
    }

    @PluginDisable
    public void disable() {
        disabled = true;
    }
}
