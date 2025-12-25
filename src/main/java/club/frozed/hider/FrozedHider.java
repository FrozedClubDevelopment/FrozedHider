package club.frozed.hider;

import club.frozed.hider.adapter.PacketEventsAdapter;
import club.frozed.hider.hook.WorldGuardHook;
import club.frozed.hider.listener.PlayerListener;
import club.frozed.hider.manager.PlayerVisibilityManager;
import club.frozed.hider.nms.INetMinecraftServer;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Elb1to
 * @since 7/18/2025
 */
@Getter
public class FrozedHider extends JavaPlugin {

	private final WorldGuardHook worldGuardHook;
	private PlayerVisibilityManager playerVisibilityManager;
	private INetMinecraftServer INetMinecraftServer;
	private boolean debug;

	{
		worldGuardHook = new WorldGuardHook(this);
	}

	@Override
	public void onLoad() {
		worldGuardHook.registerFlag();
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();

		this.debug = getConfig().getBoolean("debug", false);
		this.INetMinecraftServer = new PacketEventsAdapter(this);

		this.worldGuardHook.init();
		this.playerVisibilityManager = new PlayerVisibilityManager(this);

		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	}
}
