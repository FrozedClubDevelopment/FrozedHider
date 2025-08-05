package club.frozed.hider.nms;

import org.bukkit.entity.Player;

/**
 * @author Elb1to
 * @since 8/1/2025
 */
public interface NMSAdapter {

    /**
     * Sends packets to keep a player visible on the tablist
     * @param player The player to keep on tablist
     */
    void keepOnTablist(Player player);

    /**
     * Gets the NMS version this adapter supports
     * @return The NMS version string (e.g., "v1_21_R1")
     */
    String getVersion();
}
