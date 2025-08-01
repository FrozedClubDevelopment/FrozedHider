package club.frozed.hider.manager;

import club.frozed.hider.FrozedHider;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author Elb1to
 * @since 7/18/2025
 */
public class PlayerVisibilityManager {

    private final FrozedHider plugin;

    public PlayerVisibilityManager(FrozedHider plugin) {
        this.plugin = plugin;
    }

    public void hidePlayer(Player player, String regionId) {
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (onlinePlayer == player || !onlinePlayer.isOnline()) {
                continue;
            }
            if (canSee(onlinePlayer, player)) {
                if (plugin.isDebug()) {
                    plugin.getServer().broadcastMessage("Player '" + onlinePlayer.getName() + "' can see '" + player.getName() + "' due to permissions.");
                }
                continue;
            }
            onlinePlayer.hidePlayer(plugin, player);
        }

        keepOnTablist(player);

        if (plugin.isDebug()) {
            plugin.getServer().broadcastMessage("Hiding player '" + player.getName() + "' in region '" + regionId + "'.");
        }
    }

    public void showPlayer(Player player, String regionId) {
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (onlinePlayer == player || !onlinePlayer.isOnline()) {
                continue;
            }
            onlinePlayer.showPlayer(plugin, player);
        }

        if (plugin.isDebug()) {
            plugin.getServer().broadcastMessage("Showing player '" + player.getName() + "' from region '" + regionId + "'.");
        }
    }

    private void keepOnTablist(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, serverPlayer);
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (onlinePlayer.equals(player)) continue;
            ((CraftPlayer) onlinePlayer).getHandle().connection.send(packet);
        }
    }

    private boolean canSee(Player viewer, Player target) {
        return viewer.hasPermission("frozedhider.bypass");
    }
}

