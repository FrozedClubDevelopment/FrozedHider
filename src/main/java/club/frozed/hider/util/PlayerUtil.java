package club.frozed.hider.util;

import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author Elb1to
 * @since 7/14/2025
 */
public class PlayerUtil {

	public static void hidePlayer(Player player) {
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
	}
}
