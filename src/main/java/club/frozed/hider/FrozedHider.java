package club.frozed.hider;

import club.frozed.hider.hook.WorldGuardHook;
import club.frozed.hider.listener.PlayerListener;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Elb1to
 * @since 7/14/2025
 */
@Getter
public final class FrozedHider extends JavaPlugin {

	private boolean debug;
	private WorldGuardHook worldGuardHook;
	private StateFlag hidePlayerFlag = new StateFlag("hide-player", false);

	@Override
	public void onLoad() {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try {
			registry.register(hidePlayerFlag);
		} catch (FlagConflictException e) {
			Flag<?> existing = registry.get("hide-player");
			if (existing instanceof StateFlag) {
				hidePlayerFlag = (StateFlag) existing;
			}
		}
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();

		if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
			worldGuardHook = new WorldGuardHook(this);
			worldGuardHook.init();
		} else {
			getLogger().severe("WorldGuard is not installed! Disabling FrozedHider...");
			getServer().getPluginManager().disablePlugin(this);
		}

		debug = getConfig().getBoolean("debug", false);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	}
}
