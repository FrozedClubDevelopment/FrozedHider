package club.frozed.hider.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Elb1to
 * @since 7/14/2025
 */
public class PlayerRegionExitEvent extends Event implements Cancellable {

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public void setCancelled(boolean bool) {

	}

	@Override
	public HandlerList getHandlers() {
		return null;
	}
}
