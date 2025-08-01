package club.frozed.hider;

import club.frozed.hider.hook.WorldGuardHook;
import club.frozed.hider.listener.PlayerListener;
import club.frozed.hider.manager.PlayerVisibilityManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Elb1to
 * @since 7/14/2025
 */
@Getter
public final class FrozedHider extends JavaPlugin {

	private boolean debug;
	private final WorldGuardHook worldGuardHook;
	private PlayerVisibilityManager playerVisibilityManager;

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

		if (getServer().getPluginManager().getPlugin("WorldGuard") == null) {
			getLogger().severe("[FrozedHider] WorldGuard is not installed! Disabling FrozedHider...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		debug = getConfig().getBoolean("debug", false);

		worldGuardHook.init();
		playerVisibilityManager = new PlayerVisibilityManager(this);

		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	}

	@Override
	public void onDisable() {
		playerVisibilityManager = null;
	}
}
