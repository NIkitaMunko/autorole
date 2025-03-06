package hgc.autorole;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final LuckPerms luckPerms;
    private final JavaPlugin plugin;
    private final PlayerJoin playerJoin;
    private final PlayerQuit playerQuit;
    private AutorolePlaceholderExpansion placeholderExpansion;

    private final FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) luckPerms = provider.getProvider();
        else throw new IllegalStateException("LuckPerms API is not available!");

        playerQuit = new PlayerQuit(plugin, luckPerms);
        plugin.getServer().getPluginManager().registerEvents(playerQuit, plugin);

        playerJoin = new PlayerJoin(plugin, luckPerms, playerQuit);
        plugin.getServer().getPluginManager().registerEvents(playerJoin, plugin);

        onEnable();
    }

    private void configureData() {
        if (!config.contains("textWhenJoin")) config.set("textWhenJoin", "%s uses CosmoplexNewLauncher.");
        else playerJoin.textWhenJoin = config.getString("textWhenJoin", "%s uses CosmoplexNewLauncher.");

        if (!config.contains("groupInJoin")) config.set("groupInJoin", "meteor");
        else playerJoin.groupInJoin = config.getString("groupInJoin", "meteor");

        playerQuit.groupInJoin = playerJoin.groupInJoin;
        placeholderExpansion.groupInJoin = playerJoin.groupInJoin;

        if (!config.contains("color")) config.set("color", ChatColor.YELLOW.name());
        else playerJoin.color = ChatColor.valueOf(config.getString("color", ChatColor.YELLOW.name()).toUpperCase());

        plugin.saveConfig();
    }


    public void onEnable() {
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            plugin.getLogger().info("registering placeholder");

            placeholderExpansion = new AutorolePlaceholderExpansion(plugin);
            placeholderExpansion.register();

        } else {
            plugin.getLogger().info("cant register placeholder");
        }
        configureData();
    }

    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerQuit.setDefaultPlayer(player);
            playerQuit.removeFromDatabase(player.getName());
        }
    }

}
