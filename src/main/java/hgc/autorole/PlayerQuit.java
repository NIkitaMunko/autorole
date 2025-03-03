package hgc.autorole;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerQuit implements Listener {

    private final JavaPlugin plugin;
    private final LuckPerms luckPerms;
    public String groupInJoin = "meteor";

    public PlayerQuit(JavaPlugin plugin) {
        this.plugin = plugin;

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) this.luckPerms = provider.getProvider();
        else throw new IllegalStateException("LuckPerms API is not available!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        if (user != null && user.getPrimaryGroup().equalsIgnoreCase(groupInJoin)) {
            user.data().remove(Node.builder("group.meteor").build());
            user.data().add(Node.builder("group.player").build());
            luckPerms.getUserManager().saveUser(user);
            plugin.getLogger().info("Player " + player.getName() + " was moved from '" + groupInJoin + "' to 'player' group on quit.");
        }
    }
}
