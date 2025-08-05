package club.frozed.hider.manager;

import club.frozed.hider.FrozedHider;
import com.sk89q.worldguard.protection.flags.StateFlag;
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
			if (canSee(onlinePlayer, player, regionId)) {
				if (plugin.isDebug()) {
					plugin.getServer().broadcastMessage("Player '" + onlinePlayer.getName() + "' can see '" + player.getName() + "' due to permissions.");
				}
				continue;
			}
			onlinePlayer.hidePlayer(plugin, player);
		}

		plugin.getNmsAdapter().keepOnTablist(player);

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

	private boolean canSee(Player viewer, Player target, String regionId) {
		// Check if viewer has frozedhider.view-all permission (can see all hidden players)
		if (viewer.hasPermission("frozedhider.view-all")) {
			return true;
		}

		// Check if both players are staff and viewer has frozedhider.view-staff permission
		if (viewer.hasPermission("frozedhider.view-staff") && target.hasPermission("frozedhider.staff")) {
			return true;
		}

		// Check if the viewer is in the same hide region as the target
		return regionId != null && isPlayerInHideRegion(viewer, regionId);
	}

	private boolean isPlayerInHideRegion(Player player, String regionId) {
		StateFlag flag = plugin.getWorldGuardHook().getHidePlayerFlag();
		return plugin.getWorldGuardHook().getRegions(player.getUniqueId())
				.stream()
				.anyMatch(region -> region.getId().equals(regionId) && region.getFlag(flag) == StateFlag.State.ALLOW);
	}
}
