package club.frozed.hider.hook;

import club.frozed.hider.FrozedHider;
import club.frozed.hider.event.PlayerRegionEntryEvent;
import club.frozed.hider.event.PlayerRegionExitEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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

	public WorldGuardHook(FrozedHider plugin) {
		this.plugin = plugin;
	}

	public void init() {
		if (!WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(Entry.factory, null)) {
			plugin.getLogger().severe("[WorldGuardEvents] Could not register the entry handler !");
			plugin.getLogger().severe("[WorldGuardEvents] Please report this error. The plugin will now be disabled.");
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

	public static class Entry extends Handler implements Listener {

		public static Factory factory = new Factory();

		public static class Factory extends Handler.Factory<Entry> {
			@Override
			public Entry create(Session session) {
				return new Entry(session);
			}
		}

		public Entry(Session session) {
			super(session);
		}

		@Override
		public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
			for (ProtectedRegion region : entered) {
				PlayerRegionEntryEvent event = new PlayerRegionEntryEvent(player.getUniqueId(), region);
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					if (FrozedHider.getInstance().isDebug()) {
						Bukkit.getServer().broadcastMessage("Player " + player.getName() + " was prevented from entering region: " + region.getId());
					}
					return false;
				}
			}
			for (ProtectedRegion region : exited) {
				PlayerRegionExitEvent event = new PlayerRegionExitEvent(player.getUniqueId(), region);
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					if (FrozedHider.getInstance().isDebug()) {
						Bukkit.getServer().broadcastMessage("Player " + player.getName() + " was prevented from exiting region: " + region.getId());
					}
					return false;
				}
			}

			return true;
		}
	}
}
