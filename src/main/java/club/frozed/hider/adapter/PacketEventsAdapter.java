package club.frozed.hider.adapter;

import club.frozed.hider.FrozedHider;
import club.frozed.hider.nms.INetMinecraftServer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo.PlayerData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * @author Elb1to
 * @since 8/1/2025
 */
public class PacketEventsAdapter implements INetMinecraftServer {

	private final FrozedHider plugin;

	public PacketEventsAdapter(FrozedHider plugin) {
		this.plugin = plugin;
	}

	@Override
	public void keepOnTablist(Player player) {
		UserProfile profile = PacketEvents.getAPI().getPlayerManager().getUser(player).getProfile();

		WrapperPlayServerPlayerInfo addPlayerPacket = getWrapperPlayServerPlayerInfo(player, profile);
		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
			if (onlinePlayer.equals(player)) {
				continue;
			}

			PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayer, addPlayerPacket);

			if (plugin.isDebug()) {
				plugin.getServer().broadcastMessage("Packet sent to keep player on tablist: " + player.getName());
			}
		}
	}

	@Override
	public void removeFromTablist(Player player) {
		WrapperPlayServerPlayerInfoRemove removePacket = new WrapperPlayServerPlayerInfoRemove(
				Collections.singletonList(player.getUniqueId())
		);

		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
			if (onlinePlayer.equals(player)) {
				continue;
			}

			PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayer, removePacket);

			if (plugin.isDebug()) {
				plugin.getServer().broadcastMessage("Packet sent to remove player from tablist: " + player.getName());
			}
		}
	}

	private @NotNull WrapperPlayServerPlayerInfo getWrapperPlayServerPlayerInfo(Player player, UserProfile profile) {
		GameMode gameMode;
		switch (player.getGameMode()) {
			case CREATIVE -> gameMode = GameMode.CREATIVE;
			case ADVENTURE -> gameMode = GameMode.ADVENTURE;
			case SPECTATOR -> gameMode = GameMode.SPECTATOR;
			default -> gameMode = GameMode.SURVIVAL;
		}

		PlayerData playerData = new PlayerData(
				Component.text(player.getName()),
				profile,
				gameMode,
				player.getPing()
		);

		return new WrapperPlayServerPlayerInfo(
				WrapperPlayServerPlayerInfo.Action.ADD_PLAYER,
				Collections.singletonList(playerData)
		);
	}
}
