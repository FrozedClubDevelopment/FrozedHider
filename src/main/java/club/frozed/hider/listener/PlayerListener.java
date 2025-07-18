package club.frozed.hider.listener;

import club.frozed.hider.FrozedHider;
import club.frozed.hider.event.PlayerRegionEntryEvent;
import club.frozed.hider.event.PlayerRegionExitEvent;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Elb1to
 * @since 7/14/2025
 */
public class PlayerListener implements Listener {

	private final FrozedHider plugin;

	public PlayerListener(FrozedHider plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!player.isOnline()) {
			return;
		}

		StateFlag flag = plugin.getHidePlayerFlag();
		for (ProtectedRegion region : plugin.getWorldGuardHook().getRegions(player.getUniqueId())) {
			if (region.getFlags().containsKey(flag) && region.getFlag(flag) == StateFlag.State.ALLOW) {
				for (Player online : Bukkit.getServer().getOnlinePlayers()) {
					if (online.hasPermission("frozedhider.view-all")) {
						continue;
					}
					if (online.hasPermission("frozedhider.view-staff") && player.hasPermission("frozedhider.view-staff")) {
						continue;
					}

					online.hidePlayer(plugin, player);
					keepOnTablist(player);

					if (plugin.isDebug()) {
						Bukkit.getServer().broadcastMessage("Player '" + player.getName() + "' has joined the server and is hidden due to region: " + region.getId());
					}
				}
			}
		}
	}

	@EventHandler
	public void onRegionEntry(PlayerRegionEntryEvent event) {
		Player player = event.getPlayer();
		if (player == null || !player.isOnline()) {
			return;
		}

		ProtectedRegion region = event.getRegion();
		StateFlag flag = plugin.getHidePlayerFlag();
		if (region.getFlag(flag) != StateFlag.State.ALLOW) {
			return;
		}

		for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
			if (onlinePlayer.hasPermission("frozedhider.view-all")) {
				continue;
			}
			if (onlinePlayer.hasPermission("frozedhider.view-staff") && player.hasPermission("frozedhider.view-staff")) {
				continue;
			}

			onlinePlayer.hidePlayer(plugin, player);
			keepOnTablist(player);

			if (plugin.isDebug()) {
				Bukkit.getServer().broadcastMessage("Player '" + player.getName() + "' is now hidden due to entering the region: " + region.getId());
			}
		}
	}

	@EventHandler
	public void onRegionExit(PlayerRegionExitEvent event) {
		Player player = event.getPlayer();
		if (player == null || !player.isOnline()) {
			return;
		}

		ProtectedRegion region = event.getRegion();
		StateFlag flag = plugin.getHidePlayerFlag();
		if (region.getFlag(flag) != StateFlag.State.ALLOW && player.hasPermission("frozedhider.stay-hidden")) {
			return;
		}

		for (Player online : Bukkit.getServer().getOnlinePlayers()) {
			online.showPlayer(plugin, player);

			if (plugin.isDebug()) {
				Bukkit.getServer().broadcastMessage("Player '" + player.getName() + "' is no longer hidden due to leaving the region: " + region.getId());
			}
		}
	}

	private void keepOnTablist(Player player) {
		if (player == null || !player.isOnline()) {
			return;
		}

		ServerPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		if (entityPlayer == null) {
			return;
		}

		ClientboundPlayerInfoUpdatePacket addPlayer = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, entityPlayer);
		ClientboundPlayerInfoUpdatePacket updateDisplayName = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, entityPlayer);
		ClientboundPlayerInfoUpdatePacket updateTablist = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, entityPlayer);

		if (entityPlayer.server != null) {
			entityPlayer.server.getPlayerList().broadcastAll(addPlayer);
			entityPlayer.server.getPlayerList().broadcastAll(updateDisplayName);
			entityPlayer.server.getPlayerList().broadcastAll(updateTablist);
		}

		if (FrozedHider.getInstance().isDebug()) {
			Bukkit.getServer().broadcastMessage("Packet sent to keep player on tablist from: " + player.getName());
		}
	}
}
