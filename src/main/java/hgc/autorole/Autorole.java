package hgc.autorole;

import org.bukkit.plugin.java.JavaPlugin;

public final class Autorole extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);

    }

    @Override
    public void onDisable() {

    }
}
