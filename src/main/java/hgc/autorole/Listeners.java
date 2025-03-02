package hgc.autorole;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class Listeners implements Listener {

    private final JavaPlugin plugin;

    public Listeners (JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        Path logPath = Paths.get("..", "..", "CosmoplexNewLauncher", "logs", "latest.log");

        try {
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
                            return (lastJoinTimeMillis - logTimeMillis) <= 120000;
                        })
                        .collect(Collectors.toList());

                boolean isVip = false;
                String logTime = "";

                for (String logLine : recentLogs) {
                    if (logLine.contains("joinServer: " + playerName)) {
                        isVip = true;
                        String[] logLineParts = logLine.split(" ");
                        logTime = logLineParts[0] + " " + logLineParts[1];
                        break;
                    }
                }

                for (Player pl : Bukkit.getOnlinePlayers()) {
                    String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    if (isVip) {
                        pl.sendMessage(playerName + " uses CosmoplexNewLauncher");
//                        pl.sendMessage("Current time: " + currentTime);
//                        pl.sendMessage("Last log time for " + playerName + ": " + logTime);
                    } else {
//                        pl.sendMessage(playerName + " uses the standard launcher.");
//                        pl.sendMessage("Current time: " + currentTime);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long parseTimeToMillis(String time) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
            return dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(); // Преобразуем в миллисекунды
        } catch (Exception e) {
            return 0;
        }
    }

}
