package hgc.autorole;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collection;

public class AutorolePlaceholderExpansion extends PlaceholderExpansion implements Relational {

    private final LuckPerms luckPerms;
    private final JavaPlugin plugin;
    public String groupInJoin = "meteor";

    public AutorolePlaceholderExpansion(JavaPlugin plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }


    @Override
    public @NotNull String getIdentifier() {
        return "autorole";
    }

    @Override
    public @NotNull String getAuthor() {
        return "hgc";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.0.4";
    }

    @Override
    public String onPlaceholderRequest(Player player, Player player1, String s) {
        return "";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();

            // Логирование
            plugin.getLogger().info("Player " + player.getName() + " is online.");

            if (params.equalsIgnoreCase("check")) {
                // Логирование
                plugin.getLogger().info("Parameter 'check' received for player: " + player.getName());

                boolean usesLauncher = checkIfUsesLauncher(player);

                // Логирование результата
                plugin.getLogger().info("Uses Launcher: " + usesLauncher + " for player: " + player.getName());

                return usesLauncher ? ChatColor.GREEN + "⚡" : ChatColor.RED + "§c⚡";
            }
        }
        // Логирование на случай, если игрок не онлайн или параметр не 'check'
        plugin.getLogger().info("Either player is offline or parameter is not 'check'.");

        return null;
    }

    private boolean checkIfUsesLauncher(Player player) {
        File dataFile = new File(plugin.getDataFolder(), "players.txt");

        // Проверяем, существует ли файл
        if (!dataFile.exists()) {
            plugin.getLogger().info("Database file not found. Assuming player is not using the launcher.");
            return false;
        }

        try {
            // Читаем все строки и проверяем, есть ли там ник игрока
            List<String> playerNames = Files.readAllLines(dataFile.toPath()).stream()
                    .map(String::trim)
                    .collect(Collectors.toList());

            boolean isUsingLauncher = playerNames.contains(player.getName());

            plugin.getLogger().info("Checking if player " + player.getName() + " uses the launcher: " + isUsingLauncher);
            return isUsingLauncher;
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to read players.txt: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }




}