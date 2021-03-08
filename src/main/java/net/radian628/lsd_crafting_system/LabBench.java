package net.radian628.lsd_crafting_system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class LabBench implements Listener {
	
	JavaPlugin plugin;
	Player player;
	Inventory labInventory;
	ArrayList<LabBenchRecipe> recipes;
	
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
	
	public LabBench(JavaPlugin javaPlugin, Player user, ArrayList<LabBenchRecipe> allRecipes) {
		plugin = javaPlugin;
		player = user;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		recipes = allRecipes;
		
		labInventory = plugin.getServer().createInventory(null, 54, "Lab Bench");
		
		for (int x = 0; 9 > x; x++) {
			for (int y = 0; 6 > y; y++) {
				Material mat;
				ItemMeta meta;
				
				int index = x + 9 * y;
				
				if (isSlotEditable(index)) {
					mat = Material.AIR;
				} else {
					if (index == 40) {
						mat = Material.GLASS_BOTTLE;
					} else {
						mat = Material.WHITE_STAINED_GLASS_PANE;
					}
				}
				
				ItemStack item = new ItemStack(mat);
				meta = item.getItemMeta();
				if (index == 40) {
					meta.setDisplayName("Click to Synthesize All");
				}
				
				item.setItemMeta(meta);
				
				labInventory.setItem(index, item);
			}
		}
		
		player.openInventory(labInventory);
	}
	
	
	public LabBenchRecipe getMatchingRecipe(ItemStack[] ingredients) {
		
		for (int i = 0; recipes.size() > i; i++) {
			if (recipes.get(i).matches(ingredients)) {
				return recipes.get(i);
			}
		}
		
		return null;
	}
	
	public void synthesize() {
		ItemStack[] ingredients = {
			labInventory.getItem(12),
			labInventory.getItem(13),
			labInventory.getItem(14),
			labInventory.getItem(21),
			labInventory.getItem(22),
			labInventory.getItem(23),
			labInventory.getItem(30),
			labInventory.getItem(31),
			labInventory.getItem(32),
		};
		
		LabBenchRecipe recipe = getMatchingRecipe(ingredients);
		
		if (recipe != null) {
			while (recipe.isCraftableWithAmounts(ingredients)) {
				recipe.consume(ingredients, this);
				ItemStack result = labInventory.getItem(25);
				if (result != null && result.getType() == recipe.product.getType()) {
					result.setAmount(result.getAmount() + recipe.product.getAmount());
					labInventory.setItem(25, result);
				} else {
					labInventory.setItem(25, recipe.product);
				}
				if (recipe.byproducts != null) {
					for (ItemStack byproduct : recipe.byproducts) {
						player.getInventory().addItem(byproduct);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getClickedInventory() == labInventory) {
			
			
			if (isSlotEditable(event.getSlot())) {
			} else {
				if (event.getSlot() == 40) {
					
					ItemStack labItem = labInventory.getItem(25);
					if (labItem != null) {
						player.getInventory().addItem(labItem);
						labItem.setType(Material.AIR);
						labItem.setAmount(0);
						labInventory.setItem(25, labItem);
					}
					
					synthesize();
				}
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
