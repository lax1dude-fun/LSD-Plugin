package net.radian628.lsd_crafting_system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class LabBenchRecipeDisplayer implements Listener {
	
	JavaPlugin plugin;
	Player player;
	Inventory labInventory;
	ArrayList<LabBenchRecipe> recipes;
	int recipeIndex;
	
	public boolean isSlotEditable(int index) {

		int x = index % 9;
		int y = index / 9;
		
		if ((x >= 3 && x <= 5 && y >= 1 && y <= 3) || (x == 7 && y == 2)) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getEditableSlotIndex(int index) {
		return (new int[]{ 12, 13, 14, 21, 22, 23, 30, 31, 32 })[index];
	}
	
	public LabBenchRecipeDisplayer(JavaPlugin javaPlugin, Player user, ArrayList<LabBenchRecipe> selectedRecipes, int indexOfRecipe) {
		plugin = javaPlugin;
		player = user;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		recipes = selectedRecipes;
		recipeIndex = indexOfRecipe;
		
		labInventory = plugin.getServer().createInventory(null, 54, "Lab Bench");
		
		for (int x = 0; 9 > x; x++) {
			for (int y = 0; 6 > y; y++) {
				Material mat;
				ItemMeta meta;
				
				int index = x + 9 * y;
				
				if (isSlotEditable(index)) {
					mat = Material.AIR;
				} else {
					if (index == 9 || index == 17) {
						mat = Material.GLASS_BOTTLE;
					} else {
						mat = Material.WHITE_STAINED_GLASS_PANE;
					}
				}
				
				ItemStack item = new ItemStack(mat);
				meta = item.getItemMeta();
				if (index == 9) {
					meta.setDisplayName("Previous Recipe");
				}
				if (index == 17) {
					meta.setDisplayName("Next Recipe");
				}
				
				item.setItemMeta(meta);
				
				labInventory.setItem(index, item);
			}
		}
		
		for (int i = 0; 9 > i; i++) {
			int index = getEditableSlotIndex(i);
			ItemStack ingredient = recipes.get(recipeIndex).ingredients[i];
			if (ingredient != null) {
				labInventory.setItem(index, ingredient);
			}
		}
		
		labInventory.setItem(25, recipes.get(recipeIndex).product);
		
		player.openInventory(labInventory);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getClickedInventory() == labInventory) {

			if (event.getSlot() == 9) {
				player.closeInventory();
				new LabBenchRecipeDisplayer(plugin, player, recipes, (recipeIndex + recipes.size() - 1) % recipes.size());
			} else if (event.getSlot() == 17) {
				player.closeInventory();
				new LabBenchRecipeDisplayer(plugin, player, recipes, (recipeIndex + recipes.size() + 1) % recipes.size());
			}
			
			event.setCancelled(true);
			
		}
	}
}
