package hgc.autorole;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
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

public class AutorolePlaceholderExpansion extends PlaceholderExpansion implements Relational {

    private final JavaPlugin plugin;
    public String groupInJoin = "meteor";

    public AutorolePlaceholderExpansion(JavaPlugin plugin) {
        this.plugin = plugin;
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
        return "0.1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, Player player1, String s) {
        return "";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            if (params.equalsIgnoreCase("check")) {
                boolean usesLauncher = checkIfUsesLauncher(player);
                return usesLauncher ? ChatColor.GREEN + "⚡" : ChatColor.RED + "§c⚡";
            }
        }

        return null;
    }

    private boolean checkIfUsesLauncher(Player player) {
        File dataFile = new File(plugin.getDataFolder(), "players.txt");
        if (!dataFile.exists()) return false;
        try {
            List<String> playerNames = Files.readAllLines(dataFile.toPath()).stream()
                    .map(String::trim)
                    .collect(Collectors.toList());

            boolean isUsingLauncher = playerNames.contains(player.getName());
            return isUsingLauncher;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}