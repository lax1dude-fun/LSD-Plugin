package net.radian628.lsd_crafting_system;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class LabBench implements Listener {
	
	JavaPlugin plugin;
	Player player;
	Inventory labInventory;
	
	public boolean isSlotEditable(int index) {

		int x = index % 9;
		int y = index / 9;
		
		if ((x >= 3 && x <= 5 && y >= 1 && y <= 3) || (x == 7 && y == 2)) {
			return true;
		} else {
			return false;
		}
	}
	
	public LabBench(JavaPlugin javaPlugin, Player user) {
		plugin = javaPlugin;
		player = user;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		labInventory = plugin.getServer().createInventory(null, 54, "Lab Bench");
		
		for (int x = 0; 9 > x; x++) {
			for (int y = 0; 6 > y; y++) {
				Material mat;
				
				int index = x + 9 * y;
				
				if (isSlotEditable(index)) {
					mat = Material.AIR;
				} else {
					mat = Material.WHITE_STAINED_GLASS_PANE;
				}
				
				ItemStack item = new ItemStack(mat);
				
				labInventory.setItem(index, item);
			}
		}
		
		player.openInventory(labInventory);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getClickedInventory() == labInventory) {
			
			
			if (isSlotEditable(event.getSlot())) {
			} else {
				event.setCancelled(true);
			}
			
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory() == labInventory) {
			for (int i = 0; 54 > i; i++) {
				if (isSlotEditable(i)) {
					ItemStack labItem = labInventory.getItem(i);
					if (labItem != null) {
						player.getInventory().addItem(labItem);
					}
				}
			}
		}
	}
}
