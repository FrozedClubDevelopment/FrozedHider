package club.frozed.hider;

import club.frozed.hider.hook.WorldGuardHook;
import club.frozed.hider.listener.PlayerListener;
import club.frozed.hider.manager.PlayerVisibilityManager;
import club.frozed.hider.nms.NMSAdapter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Elb1to
 * @since 7/18/2025
 */
@Getter
public class FrozedHider extends JavaPlugin {

	private WorldGuardHook worldGuardHook;
	private PlayerVisibilityManager playerVisibilityManager;
	private NMSAdapter nmsAdapter;
	private boolean debug;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		this.debug = getConfig().getBoolean("debug", false);

		// Detect server version and load appropriate NMS adapter
		String version = getServerVersion();
		NMSAdapter adapter = createNMSAdapter(version);

		if (adapter == null) {
			getLogger().severe("Unsupported server version: " + version);
			getLogger().severe("Supported versions: 1.21.1 - 1.21.8");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		this.nmsAdapter = adapter;
		getLogger().info("Loaded NMS adapter for version: " + version);

		this.worldGuardHook = new WorldGuardHook(this);
		this.playerVisibilityManager = new PlayerVisibilityManager(this);

		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

		getLogger().info("FrozedHider has been enabled using NMS version: " + nmsAdapter.getVersion());
	}

	private String getServerVersion() {
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		return packageName.substring(packageName.lastIndexOf('.') + 1);
	}

	private NMSAdapter createNMSAdapter(String version) {
		try {
			return switch (version) {
				case "v1_21_R1" -> {
					Class<?> adapterClass1 = Class.forName("club.frozed.hider.nms.v1_21_R1.NMSAdapter_v1_21_R1");
					yield (NMSAdapter) adapterClass1.getConstructor(FrozedHider.class).newInstance(this);
				}
				case "v1_21_R2" -> {
					Class<?> adapterClass2 = Class.forName("club.frozed.hider.nms.v1_21_R2.NMSAdapter_v1_21_R2");
					yield (NMSAdapter) adapterClass2.getConstructor(FrozedHider.class).newInstance(this);
				}
				case "v1_21_R3" -> {
					Class<?> adapterClass3 = Class.forName("club.frozed.hider.nms.v1_21_R3.NMSAdapter_v1_21_R3");
					yield (NMSAdapter) adapterClass3.getConstructor(FrozedHider.class).newInstance(this);
				}
				default -> null;
			};
		} catch (Exception e) {
			getLogger().severe("Failed to load NMS adapter for version " + version + ": " + e.getMessage());
			return null;
		}
	}
}
