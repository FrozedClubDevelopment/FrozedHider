package club.frozed.hider.hook;

import club.frozed.hider.FrozedHider;
import club.frozed.hider.event.PlayerRegionEntryEvent;
import club.frozed.hider.event.PlayerRegionExitEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * @author Elb1to
 * @since 7/17/2025
 */
@Getter
public class WorldGuardHook {

	private final FrozedHider plugin;
	private RegionContainer container;
	private StateFlag hidePlayerFlag;

	public WorldGuardHook(FrozedHider plugin) {
		this.plugin = plugin;
	}

	public void registerFlag() {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try {
			StateFlag flag = new StateFlag("hide-player", false);
			registry.register(flag);
			hidePlayerFlag = flag;
		} catch (FlagConflictException e) {
			Flag<?> existing = registry.get("hide-player");
			if (existing instanceof StateFlag) {
				hidePlayerFlag = (StateFlag) existing;
			} else {
				plugin.getLogger().severe("Could not register the 'hide-player' flag due to a conflict!");
			}
		}
	}

	public void init() {
		if (!WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(new FactoryHandler(plugin), null)) {
			plugin.getLogger().severe("[FrozedHider] Could not register the factory entry handler! Disabling plugin...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}

		container = WorldGuard.getInstance().getPlatform().getRegionContainer();
	}

	public Set<ProtectedRegion> getRegions(UUID uuid) {
		Player player = Bukkit.getServer().getPlayer(uuid);
		if (player == null || !player.isOnline()) {
			return Collections.emptySet();
		}

		return container.createQuery().getApplicableRegions(BukkitAdapter.adapt(player.getLocation())).getRegions();
	}

	public static class FactoryHandler extends Handler.Factory<EntryHandler> {

		private final FrozedHider plugin;

		public FactoryHandler(FrozedHider plugin) {
			this.plugin = plugin;
		}

		@Override
		public EntryHandler create(Session session) {
			return new EntryHandler(session, plugin);
		}
	}

	public static class EntryHandler extends Handler {

		private final FrozedHider plugin;

		public EntryHandler(Session session, FrozedHider plugin) {
			super(session);
			this.plugin = plugin;
		}

		@Override
		public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
			for (ProtectedRegion region : entered) {
				PlayerRegionEntryEvent event = new PlayerRegionEntryEvent(player.getUniqueId(), region);
				plugin.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					if (plugin.isDebug()) {
						plugin.getServer().broadcastMessage("Player " + player.getName() + " was prevented from entering region: " + region.getId());
					}

					return false;
				}
			}

			for (ProtectedRegion region : exited) {
				PlayerRegionExitEvent event = new PlayerRegionExitEvent(player.getUniqueId(), region);
				plugin.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					if (plugin.isDebug()) {
						plugin.getServer().broadcastMessage("Player " + player.getName() + " was prevented from exiting region: " + region.getId());
					}

					return false;
				}
			}

			return true;
		}
	}
}
