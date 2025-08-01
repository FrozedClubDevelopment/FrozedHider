package club.frozed.hider.listener;

import club.frozed.hider.FrozedHider;
import club.frozed.hider.event.PlayerRegionEntryEvent;
import club.frozed.hider.event.PlayerRegionExitEvent;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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

		StateFlag flag = plugin.getWorldGuardHook().getHidePlayerFlag();
		for (ProtectedRegion region : plugin.getWorldGuardHook().getRegions(player.getUniqueId())) {
			if (region.getFlag(flag) == StateFlag.State.ALLOW) {
				plugin.getPlayerVisibilityManager().hidePlayer(player, region.getId());
				break;
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
		if (region.getFlag(plugin.getWorldGuardHook().getHidePlayerFlag()) == StateFlag.State.ALLOW) {
			plugin.getPlayerVisibilityManager().hidePlayer(player, region.getId());
		}
	}

	@EventHandler
	public void onRegionExit(PlayerRegionExitEvent event) {
		Player player = event.getPlayer();
		if (player == null || !player.isOnline()) {
			return;
		}

		StateFlag flag = plugin.getWorldGuardHook().getHidePlayerFlag();
		boolean inHideRegion = plugin.getWorldGuardHook().getRegions(player.getUniqueId()).stream().anyMatch(r -> r.getFlag(flag) == StateFlag.State.ALLOW);
		if (!inHideRegion) {
			plugin.getPlayerVisibilityManager().showPlayer(player, event.getRegion().getId());
		}
	}
}
