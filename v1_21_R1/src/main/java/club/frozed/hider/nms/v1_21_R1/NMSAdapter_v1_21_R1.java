package club.frozed.hider.nms.v1_21_R1;

import club.frozed.hider.FrozedHider;
import club.frozed.hider.nms.NMSAdapter;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * NMS adapter for Minecraft 1.21.0-1.21.1 (v1_21_R1)
 * @author Elb1to
 * @since 8/1/2025
 */
public class NMSAdapter_v1_21_R1 implements NMSAdapter {

    private final FrozedHider plugin;

    public NMSAdapter_v1_21_R1(FrozedHider plugin) {
        this.plugin = plugin;
    }

    @Override
    public void keepOnTablist(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        ClientboundPlayerInfoUpdatePacket addPlayer = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, serverPlayer);
        ClientboundPlayerInfoUpdatePacket updateDisplayName = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, serverPlayer);
        ClientboundPlayerInfoUpdatePacket updateTablist = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, serverPlayer);

        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (onlinePlayer.equals(player)) {
                continue;
            }

            ((CraftPlayer) onlinePlayer).getHandle().connection.send(addPlayer);
            ((CraftPlayer) onlinePlayer).getHandle().connection.send(updateDisplayName);
            ((CraftPlayer) onlinePlayer).getHandle().connection.send(updateTablist);

            if (plugin.isDebug()) {
                plugin.getServer().broadcastMessage("Packet sent to keep player on tablist from: " + player.getName());
            }
        }
    }

    @Override
    public String getVersion() {
        return "v1_21_R1";
    }
}
