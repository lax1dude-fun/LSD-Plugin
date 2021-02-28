package net.radian628.lsd_crafting_system;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class CraftingSystem implements Listener {
	
	JavaPlugin plugin;
	Random rand;
	ArrayList<LabBenchRecipe> recipes;
	
	public CraftingSystem(JavaPlugin javaPlugin) {
		rand = new Random();
		plugin = javaPlugin;
		plugin.getLogger().info("test");
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		recipes = new ArrayList<LabBenchRecipe>();
		getAllRecipes();
	}
	

	public void getAllRecipes() {
		recipes.clear();
		Map<String, Object> recipesInConfig = plugin.getConfig().getConfigurationSection("recipes").getValues(false);

		Iterator<Entry<String, Object>> recipeIterator = recipesInConfig.entrySet().iterator();
		while (recipeIterator.hasNext()) {
			Entry<String, Object> recipe = recipeIterator.next();
			String recipeName = recipe.getKey();
			

			String ingredientKey = "recipes." + recipeName + ".ingredients";
			ItemStack[] requiredIngredients = new ItemStack[9];
	
			Map<String, Object> requiredIngredientsFromConfig = plugin.getConfig().getConfigurationSection(ingredientKey).getValues(true);
			
			for (int i = 0; 9 > i; i++) {
				
				ItemStack requiredIngredient = (ItemStack)requiredIngredientsFromConfig.get(String.valueOf(i));
			
				requiredIngredients[i] = requiredIngredient;
			}
			
			ItemStack product = plugin.getConfig().getItemStack("recipes." + recipeName + ".product");
			
			String byproductsKey = "recipes." + recipeName + ".byproducts";
			
			List<ItemStack> byproductsList = null;
			ItemStack[] byproducts = null;
			
			
			if (plugin.getConfig().isList(byproductsKey)) {
				
				byproductsList = (List<ItemStack>)plugin.getConfig().get(byproductsKey);
				byproducts = new ItemStack[byproductsList.size()];
				byproductsList.toArray(byproducts);
			}
			
			if (byproducts != null) {
				plugin.getLogger().info("noticed byproducts actually exist and has size " + String.valueOf(byproducts.length));
			}
			
			String shapelessKey = "recipes." + recipeName + ".shapeless";
			
			if (plugin.getConfig().isBoolean(shapelessKey) && plugin.getConfig().getBoolean(shapelessKey)) {
				recipes.add(new ShapelessLabBenchRecipe(requiredIngredients, product, byproducts, recipeName));
			} else {
				recipes.add(new LabBenchRecipe(requiredIngredients, product, byproducts, recipeName));
			}
		}
	}
	
	public void openLab(Player player) {
		new LabBench(plugin, player, recipes);
	}
	
	public void displayRecipe(Player player, String recipeName) {
		int chosenRecipeIndex = -1;
		for (int i = 0; recipes.size() > i; i++) {
			LabBenchRecipe recipe = recipes.get(i);
			if (recipe.name.equalsIgnoreCase(recipeName)) {
				chosenRecipeIndex = i;
				break;
			}
		}
		
		if (chosenRecipeIndex != -1) {
			new LabBenchRecipeDisplayer(plugin, player, recipes, chosenRecipeIndex);
		} else {
			player.sendMessage(ChatColor.RED + "No lab bench recipe for '" + recipeName + "' found.");
		}
	}
	
	public void handleCustomBlockDrop(Block block, Player blockBreaker, BlockBreakEvent breakEvent) {
		World world = block.getWorld();
		BlockData blockData = block.getBlockData();
		

		boolean hasSilkTouch = false;
		boolean hasShears = false;
		if (blockBreaker != null) {
			ItemStack heldItem = blockBreaker.getInventory().getItemInMainHand();
			
			if (heldItem != null) {
				if (heldItem.getItemMeta() != null && heldItem.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
					hasSilkTouch = true;
				}
				if (heldItem.getType() == Material.SHEARS) {
					hasShears = true;
				}
			}
			
		}
		
		if (blockData.getMaterial() == Material.JUNGLE_LEAVES) {
			if (!hasSilkTouch && !hasShears) {
				if (rand.nextFloat() > 0.99) {
					ItemStack lemon = new ItemStack(Material.APPLE);
					ItemMeta lemonMeta = lemon.getItemMeta();
					lemonMeta.setCustomModelData(666);
					lemonMeta.setDisplayName(ChatColor.RESET + "Lemon");
					lemon.setItemMeta(lemonMeta);
					
					world.dropItemNaturally(block.getLocation(), lemon);
				}
			}
			
		} else if (blockData.getMaterial() == Material.IRON_ORE) {
			if (blockBreaker != null && !hasSilkTouch && breakEvent != null) {
				if (rand.nextFloat() > 0.9) {
					ItemStack pyrite = new ItemStack(Material.IRON_NUGGET);
					ItemMeta pyriteMeta = pyrite.getItemMeta();
					pyriteMeta.setCustomModelData(666);
					pyriteMeta.setDisplayName(ChatColor.RESET + "Pyrite");
					pyrite.setItemMeta(pyriteMeta);
					breakEvent.setDropItems(false);					
					world.dropItemNaturally(block.getLocation(), pyrite);
				}
			}
		} else if (blockData.getMaterial() == Material.WHEAT) {
			Ageable wheatData = (Ageable)blockData;
			if (wheatData.getAge() == wheatData.getMaximumAge()) {
				if (rand.nextFloat() > 0.999) {
					ItemStack ergot = new ItemStack(Material.POISONOUS_POTATO);
					ItemMeta ergotMeta = ergot.getItemMeta();
					ergotMeta.setCustomModelData(666);
					ergotMeta.setDisplayName(ChatColor.RESET + "Ergot Sclerotium");
					ergot.setItemMeta(ergotMeta);			
					world.dropItemNaturally(block.getLocation(), ergot);
				}
			}
		}
	}	
	
	public ArrayList<Block> getMatchingAdjacents(Block block, Material match) {
		ArrayList<Block> result = new ArrayList<Block>();

		Block[] adjacents = { block.getRelative(1, 0, 0),
		block.getRelative(-1, 0, 0),
		 block.getRelative(0, 0, 1),
		 block.getRelative(0, 0, -1) };

		for (int i = 0; 4 > i; i++) {
			if (adjacents[i].getType() == match) {
				result.add(adjacents[i]);
			}
		}
		
		return result;
	}
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		handleCustomBlockDrop(event.getBlock(), event.getPlayer(), event);
	}
	
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		handleCustomBlockDrop(event.getBlock(), null, null);
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		ItemStack heldItem = event.getItem();
		World world = event.getPlayer().getWorld();
		
		if (heldItem != null && heldItem.getType() == Material.BOOK && heldItem.hasItemMeta() && heldItem.getItemMeta().hasCustomModelData() && heldItem.getItemMeta().getCustomModelData() == 1337) {
			displayRecipe(event.getPlayer(), "lsd");
		} else if (event.hasBlock()) {
			Block block = event.getClickedBlock();
			BlockData data = block.getBlockData();
			plugin.getLogger().info("got here");
			if (block.getType() == Material.OAK_SIGN || block.getType() == Material.OAK_WALL_SIGN) {
				Sign sign = (Sign)block.getState();
				
				if (sign.getLine(0).equalsIgnoreCase("Lab Bench")) {
					ArrayList<Block> quartzAdjacents = getMatchingAdjacents(block, Material.QUARTZ_BRICKS);
					if (quartzAdjacents.size() == 1) {
						Block labBenchPiece = quartzAdjacents.get(0);
						if (getMatchingAdjacents(labBenchPiece, Material.QUARTZ_BRICKS).size() == 2) {
							openLab(event.getPlayer());
						}
					}
				}
			}
		}
	}
}
