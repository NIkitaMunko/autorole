package hgc.autorole;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final PlayerJoin playerJoin;
    private final PlayerQuit playerQuit;
    private final FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        playerJoin = new PlayerJoin(plugin);
        playerQuit = new PlayerQuit(plugin);
        plugin.getServer().getPluginManager().registerEvents(playerJoin, plugin);
        plugin.getServer().getPluginManager().registerEvents(playerQuit, plugin);

        configureData();
    }

    private void configureData() {
        if (!config.contains("textWhenJoin")) config.set("textWhenJoin", "%s uses CosmoplexNewLauncher.");
        else playerJoin.textWhenJoin = config.getString("textWhenJoin", "%s uses CosmoplexNewLauncher.");

        if (!config.contains("groupInJoin")) config.set("groupInJoin", "meteor");
        else playerJoin.groupInJoin = config.getString("groupInJoin", "meteor");

        playerQuit.groupInJoin = playerJoin.groupInJoin;

        if (!config.contains("color")) config.set("color", ChatColor.YELLOW.name());
        else playerJoin.color = ChatColor.valueOf(config.getString("color", ChatColor.YELLOW.name()).toUpperCase());

        plugin.saveConfig();
    }

}
