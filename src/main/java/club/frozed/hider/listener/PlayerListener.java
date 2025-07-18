package club.frozed.hider.listener;

import club.frozed.hider.FrozedHider;
import club.frozed.hider.event.PlayerRegionEntryEvent;
import club.frozed.hider.event.PlayerRegionExitEvent;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Elb1to
 * @since 7/14/2025
 */
public class PlayerListener implements Listener {

	private final FrozedHider plugin;

	// We keep track of online players here to avoid
	// creating Collections unnecessarily on every event call.
	private final Set<Player> onlinePlayers = new HashSet<>();

	public PlayerListener(FrozedHider plugin) {
		this.plugin = plugin;
		onlinePlayers.addAll(plugin.getServer().getOnlinePlayers());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		onlinePlayers.add(player);
		if (!player.isOnline()) {
			return;
		}

		StateFlag flag = plugin.getHidePlayerFlag();
		for (ProtectedRegion region : plugin.getWorldGuardHook().getRegions(player.getUniqueId())) {
			if (region.getFlag(flag) == StateFlag.State.ALLOW) {
				hidePlayer(player, region.getId());
				break;
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		onlinePlayers.remove(event.getPlayer());
	}

	@EventHandler
	public void onRegionEntry(PlayerRegionEntryEvent event) {
		Player player = event.getPlayer();
		if (player == null || !player.isOnline()) {
			return;
		}

		ProtectedRegion region = event.getRegion();
		if (region.getFlag(plugin.getHidePlayerFlag()) == StateFlag.State.ALLOW) {
			hidePlayer(player, region.getId());
		}
	}

	@EventHandler
	public void onRegionExit(PlayerRegionExitEvent event) {
		Player player = event.getPlayer();
		if (player == null || !player.isOnline()) {
			return;
		}

		StateFlag flag = plugin.getHidePlayerFlag();
		boolean inHideRegion = plugin.getWorldGuardHook().getRegions(player.getUniqueId()).stream().anyMatch(r -> r.getFlag(flag) == StateFlag.State.ALLOW);
		if (!inHideRegion) {
			showPlayer(player, event.getRegion().getId());
		}
	}

	private void hidePlayer(Player player, String regionId) {
		for (Player onlinePlayer : onlinePlayers) {
			if (onlinePlayer == player || !onlinePlayer.isOnline()) continue;
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
			plugin.getServer().broadcastMessage("Player '" + player.getName() + "' is now hidden due to region: " + regionId);
		}
	}

	private void showPlayer(Player player, String regionId) {
		if (player.hasPermission("frozedhider.stay-hidden")) {
			if (plugin.isDebug()) {
				plugin.getServer().broadcastMessage("Player '" + player.getName() + "' is not shown due to permission 'frozedhider.stay-hidden'");
			}

			return;
		}

		for (Player onlinePlayer : onlinePlayers) onlinePlayer.showPlayer(plugin, player);

		if (plugin.isDebug()) {
			plugin.getServer().broadcastMessage("Player '" + player.getName() + "' is no longer hidden due to leaving region: " + regionId);
		}
	}

	private boolean canSee(Player viewer, Player target) {
		return viewer.hasPermission("frozedhider.view-all") || (viewer.hasPermission("frozedhider.view-staff") && target.hasPermission("frozedhider.view-staff"));
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

		if (plugin.isDebug()) {
			plugin.getServer().broadcastMessage("Packet sent to keep player on tablist from: " + player.getName());
		}
	}
}
