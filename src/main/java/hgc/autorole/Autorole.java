package hgc.autorole;

import org.bukkit.plugin.java.JavaPlugin;

public final class Autorole extends JavaPlugin {

    @Override
    public void onEnable() {
        new ConfigManager(this);
    }

    @Override
    public void onDisable() {

    }
}
