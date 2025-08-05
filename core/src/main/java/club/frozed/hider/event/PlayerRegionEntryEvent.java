package club.frozed.hider.event;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * @author Elb1to
 * @since 7/14/2025
 */
@Getter
public class PlayerRegionEntryEvent extends Event implements Cancellable {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	private final UUID uuid;
	private final ProtectedRegion region;
	private final String regionName;

	public boolean cancelled = false;

	public PlayerRegionEntryEvent(UUID playerUUID, ProtectedRegion region) {
		this.uuid = playerUUID;
		this.region = region;
		this.regionName = region.getId();
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	@NonNull
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	public Player getPlayer() {
		return Bukkit.getServer().getPlayer(this.uuid);
	}
}
