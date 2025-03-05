package hgc.autorole;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class PlayerJoin implements Listener {

    private final PlayerQuit playerQuit;
    private final JavaPlugin plugin;
    private final LuckPerms luckPerms;

    public String groupInJoin = "meteor";
    public String textWhenJoin = "%s uses CosmoplexNewLauncher.";
    public ChatColor color = ChatColor.YELLOW;

    public PlayerJoin(JavaPlugin plugin, LuckPerms luckPerms, PlayerQuit playerQuit) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
        this.playerQuit = playerQuit;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        Path logPath = Paths.get("D:", "CosmoplexNewLauncher", "logs", "latest.log");
//        Path logPath = Paths.get("/root", "CosmoplexNewLauncher", "logs", "latest.log");

        try {
            plugin.getLogger().info("Attempting to access log file at: " + logPath);

            if (Files.exists(logPath)) {
                plugin.getLogger().info("Log file exists. Attempting to read...");
                if (canReadWindowsFile(logPath)) {
                    plugin.getLogger().info("File is readable. Proceeding with reading...");
                    List<String> lines = Files.readAllLines(logPath);
                    if (!lines.isEmpty()) {
                        String lastLine = lines.get(lines.size() - 1);
                        String[] splitLastLine = lastLine.split(" ");
                        String joinTime = splitLastLine[0] + " " + splitLastLine[1];
                        long lastJoinTimeMillis = parseTimeToMillis(joinTime);
                        List<String> recentLogs = lines.stream()
                                .filter(line -> {
                                    String[] splitLine = line.split(" ");
                                    String logTime = splitLine[0] + " " + splitLine[1];
                                    long logTimeMillis = parseTimeToMillis(logTime);
                                    return (lastJoinTimeMillis - logTimeMillis) <= 120000; /// 120000ms == 2min
                                })
                                .toList();

                        boolean isCosmoplex = false;
                        for (String logLine : recentLogs) {
                            if (logLine.contains("joinServer: " + playerName)) {
                                isCosmoplex = true;
                                break;
                            }
                        }

                        for (Player pl : Bukkit.getOnlinePlayers()) {
                            if (isCosmoplex) pl.sendMessage(color + String.format(textWhenJoin, playerName));
                        }
                        if (isCosmoplex) {
                            playerSetGroup(player);
                            addToDataBase(player.getName());
                        } else {
                            playerQuit.removeFromDatabase(playerName);
                        }
                    }
                } else
                    plugin.getLogger().info("No read permissions for the file at: " + logPath);
            } else
                plugin.getLogger().info("Log file does not exist at: " + logPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean canReadUnixFile(Path path) {
        try {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(path);
            return permissions.contains(PosixFilePermission.OWNER_READ) ||
                    permissions.contains(PosixFilePermission.GROUP_READ) ||
                    permissions.contains(PosixFilePermission.OTHERS_READ);
        } catch (IOException e) {
            plugin.getLogger().info("Error checking permissions for: " + path);
            return false;
        }
    }

    private boolean canReadWindowsFile(Path path) {
        File file = path.toFile();
        return file.exists() && file.canRead();
    }

    private long parseTimeToMillis(String time) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
            return dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception e) {
            return 0;
        }
    }


    private void addToDataBase(String playerName) {
        // Папка плагина (на одном уровне с конфигом)
        File dataFile = new File(plugin.getDataFolder(), "players.txt");

        try {
            // Создаём файл, если его нет
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }

            // Добавляем ник в файл с новой строки
            try (FileWriter writer = new FileWriter(dataFile, true)) {
                writer.write(playerName + System.lineSeparator());
            }

            plugin.getLogger().info("Player " + playerName + " added to database (players.txt).");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player " + playerName + " to players.txt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void playerSetGroup(Player player)   {
        if (luckPerms == null) {
            plugin.getLogger().warning("LuckPerms API is not initialized.");
            return;
        }

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            plugin.getLogger().warning("Could not fetch user data for " + player.getName());
            return;
        }

        String primaryGroup = user.getPrimaryGroup();
        if (primaryGroup.equalsIgnoreCase("default") || primaryGroup.equalsIgnoreCase("players")) {
            plugin.getLogger().info(player.getName() + " is in group " + primaryGroup + ". Changing to meteor...");
            user.data().clear();
            InheritanceNode newGroup = InheritanceNode.builder(groupInJoin).build();
            user.data().add(newGroup);
            luckPerms.getUserManager().saveUser(user);
        } else {
            plugin.getLogger().info(player.getName() + " is already in group " + primaryGroup + ".");
        }
    }
}
