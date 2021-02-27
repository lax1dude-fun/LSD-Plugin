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
	
	public int getEditableSlotIndex(int index) {
		return (new int[]{ 12, 13, 14, 21, 22, 23, 30, 31, 32 })[index];
	}
	
	public LabBench(JavaPlugin javaPlugin, Player user) {
		plugin = javaPlugin;
		player = user;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
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
		Map<String, Object> recipes = plugin.getConfig().getConfigurationSection("recipes").getValues(false);
		
		String recipeName = null;
		
		Iterator<Entry<String, Object>> recipeIterator = recipes.entrySet().iterator();
		boolean recipeMatches = false;
		while (recipeIterator.hasNext()) {
			
			recipeMatches = true;
			
			Entry<String, Object> recipe = recipeIterator.next();
			
			recipeName = recipe.getKey();
			

			String configKey = "recipes." + recipeName + ".ingredients";
			Map<String, Object> requiredIngredients = plugin.getConfig().getConfigurationSection(configKey).getValues(true);
			
			for (int ii = 0; 9 > ii; ii++) {
				
				ItemStack requiredIngredient = (ItemStack)requiredIngredients.get(String.valueOf(ii));
				
				boolean ingredientMatches = false;
				
				if (!(ingredients[ii] == null ^ requiredIngredient == null)) {
					if (ingredients[ii] == null && requiredIngredient == null) {
						ingredientMatches = true;
					} else if (ingredients[ii].getType() == requiredIngredient.getType()) {
						if (!requiredIngredient.getItemMeta().hasCustomModelData() && !ingredients[ii].getItemMeta().hasCustomModelData()) {
							ingredientMatches = true;
						} else if (requiredIngredient.getItemMeta().hasCustomModelData() && ingredients[ii].getItemMeta().hasCustomModelData()) {
							if (requiredIngredient.getItemMeta().hasCustomModelData() == ingredients[ii].getItemMeta().hasCustomModelData()) {
								ingredientMatches = true;
							}
						}
					}
				}
				
				if (!ingredientMatches) {
					recipeMatches = false;
					break;
				}
			}
			
			if (recipeMatches) {
				break;
			}
			
		}
		
		if (recipeMatches && recipeName != null) {
			ItemStack[] requiredIngredients = new ItemStack[9];

			String configKey = "recipes." + recipeName + ".ingredients";
			Map<String, Object> requiredIngredientsFromConfig = plugin.getConfig().getConfigurationSection(configKey).getValues(true);
			
			for (int i = 0; 9 > i; i++) {
				
				ItemStack requiredIngredient = (ItemStack)requiredIngredientsFromConfig.get(String.valueOf(i));
			
				requiredIngredients[i] = requiredIngredient;
			}
			
			ItemStack product = plugin.getConfig().getItemStack("recipes." + recipeName + ".product");
			
			String byproductsKey = "recipes." + recipeName + ".byproducts";
			
			List<ItemStack> byproductsList = null;
			ItemStack[] byproducts = null;
			
			
			if (plugin.getConfig().isList(byproductsKey)) {
				/*byproductsList = plugin.getConfig().getMapList(byproductsKey).stream().map(val -> ItemStack.deserialize((Map<String, Object>)val)).collect(Collectors.toList());

				
				ItemStack[] byproducts2 = new ItemStack[byproductsList.size()];
				byproductsList.toArray(byproducts2);
				byproducts = byproducts2;*/
				
				byproductsList = (List<ItemStack>)plugin.getConfig().get(byproductsKey);
				byproducts = new ItemStack[byproductsList.size()];
				byproductsList.toArray(byproducts);
			}
			
			if (byproducts != null) {
				plugin.getLogger().info("noticed byproducts actually exist and has size " + String.valueOf(byproducts.length));
			}
			
			return new LabBenchRecipe(requiredIngredients, product, byproducts);
			
		} else {
			return null;
		}
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
			while (recipe.isCraftableWith(ingredients)) {
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
		} else {
			plugin.getLogger().info("no valid recipe");
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
