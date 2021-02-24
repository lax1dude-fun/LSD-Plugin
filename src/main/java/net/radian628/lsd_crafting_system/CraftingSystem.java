package net.radian628.lsd_crafting_system;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftingSystem implements Listener {
	
	JavaPlugin plugin;
	
	public CraftingSystem(JavaPlugin javaPlugin) {
		plugin = javaPlugin;
		plugin.getLogger().info("test");
	}
	
	public void openLab(Player player) {
		new LabBench(plugin, player);
	}
}
