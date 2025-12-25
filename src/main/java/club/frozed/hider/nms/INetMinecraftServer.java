package club.frozed.hider.nms;

import org.bukkit.entity.Player;

/**
 * @author Elb1to
 * @since 8/1/2025
 */
public interface INetMinecraftServer {

    /**
     * Sends packets to keep a player visible on the tablist
     * @param player The player to keep on tablist
     */
    void keepOnTablist(Player player);

    /**
     * Sends packets to remove a player from the tablist
     * @param player The player to remove from tablist
     */
    void removeFromTablist(Player player);
}
