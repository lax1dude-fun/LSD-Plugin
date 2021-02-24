package net.radian628.lsd_crafting_system;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftingSystem {
	
	JavaPlugin plugin;
	
	public CraftingSystem(JavaPlugin javaPlugin) {
		plugin = javaPlugin;
		plugin.getLogger().info("test");
	}
	
	public void openLab(Player player) {
		Inventory labInventory = plugin.getServer().createInventory(null, 54, "Lab");
		
		for (int x = 0; 9 > x; x++) {
			for (int y = 0; 6 > y; y++) {
				/*Material mat;
				
				if (x >= 4 && x <= 6 && y >= 2 && y <= 4) {
					mat = Material.AIR;
				} else {
					mat = Material.WHITE_STAINED_GLASS_PANE;
				}
				
				ItemStack item = new ItemStack();
				
				labInventory.setItem(x + 9 * y, );*/
			}
		}
		
		player.openInventory(labInventory);
	}
}
