package info.preva1l.trashcan.extension;

import info.preva1l.hooker.Hooker;
import info.preva1l.trashcan.Version;
import info.preva1l.trashcan.extension.annotations.*;
import info.preva1l.trashcan.flavor.Flavor;
import info.preva1l.trashcan.flavor.FlavorOptions;
import info.preva1l.trashcan.flavor.PackageIndexer;
import info.preva1l.trashcan.flavor.binder.defaults.DefaultPluginBinder;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The base plugin class to extend if you want the full function of Trashcan.
 * Obviously you don't need to use this and can cherry-pick its contents if need be.
 * <p>
 * Created on 11/04/2025
 *
 * @author Preva1l
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class BasePlugin extends JavaPlugin implements BaseExtension {
    protected final Version currentVersion =
            Version.fromString(getPluginMeta() == null ? "1.0.0" : getPluginMeta().getVersion());

    protected Flavor flavor;
    protected PackageIndexer packageIndexer;

    @Override
    public final void onLoad() {
        // Resolve all plugin annotations here, preventing class not found exceptions on shutdown
        try {
            Class.forName("info.preva1l.trashcan.extension.annotations.PluginLoad");
            Class.forName("info.preva1l.trashcan.extension.annotations.PluginEnable");
            Class.forName("info.preva1l.trashcan.extension.annotations.PluginDisable");
            Class.forName("info.preva1l.trashcan.extension.annotations.ExtensionReload");
            Class.forName("info.preva1l.hooker.annotations.OnStop");
        } catch (ClassNotFoundException ignored) {}

        this.flavor = Flavor.create(
                this.getClass(),
                new FlavorOptions(
                        this.getLogger(),
                        this.getClass().getPackageName()
                )
        );

        this.packageIndexer = flavor.reflections;

        this.packageIndexer.invokeMethodsAnnotatedWith(PluginLoad.class);

        this.flavor.inherit(new DefaultPluginBinder(this));

        try {
            var hooker = Hooker.class.getDeclaredField("instance");
            hooker.setAccessible(true);
            if (hooker.get(null) == null) {
                Hooker.register(this, "info.preva1l");
            }
        } catch (Exception ignored) {}
        Hooker.load();
        InstanceHolder.modification = this;
    }

    @Override
    public final void onEnable() {
        flavor.startup();

        Hooker.enable();

        this.packageIndexer.invokeMethodsAnnotatedWith(PluginEnable.class);
    }

    @Override
    public final void onDisable() {
        Hooker.disable();

        this.packageIndexer.invokeMethodsAnnotatedWith(PluginDisable.class);

        flavor.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void reload() {
        this.packageIndexer.invokeMethodsAnnotatedWith(ExtensionReload.class);
        Hooker.reload();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Version getCurrentVersion() {
        return currentVersion;
    }
}
