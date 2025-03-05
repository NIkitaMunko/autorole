package hgc.autorole;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerQuit implements Listener {

    private final JavaPlugin plugin;
    private final LuckPerms luckPerms;
    public String groupInJoin = "meteor";

    public PlayerQuit(JavaPlugin plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        setDefaultPlayer(player);
    }

    public void setDefaultPlayer(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null && user.getPrimaryGroup().equalsIgnoreCase(groupInJoin)) {
            user.data().remove(Node.builder("group.meteor").build());
            user.data().add(Node.builder("group.players").build());
            luckPerms.getUserManager().saveUser(user);

            removeFromDatabase(player.getName());

            plugin.getLogger().info("Player " + player.getName() + " was moved from '" + groupInJoin + "' to 'players' group on quit.");
        }
    }

    public void removeFromDatabase(String playerName) {
        File dataFile = new File(plugin.getDataFolder(), "players.txt");

        if (!dataFile.exists()) {
            return; // Файла нет — нечего удалять
        }

        try {
            // Читаем все строки и фильтруем те, что не равны playerName
            List<String> updatedLines = Files.readAllLines(dataFile.toPath())
                    .stream()
                    .filter(line -> !line.trim().equalsIgnoreCase(playerName))
                    .collect(Collectors.toList());

            // Перезаписываем файл без удалённого игрока
            Files.write(dataFile.toPath(), updatedLines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

            plugin.getLogger().info("Player " + playerName + " removed from database (players.txt).");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to remove player " + playerName + " from players.txt: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
