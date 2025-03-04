package hgc.autorole;

import org.bukkit.plugin.java.JavaPlugin;

public final class Autorole extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
    }

    @Override
    public void onDisable() {
        configManager.onDisable();
    }

}
