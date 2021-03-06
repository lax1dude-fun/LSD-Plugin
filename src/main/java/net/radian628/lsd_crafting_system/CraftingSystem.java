package net.radian628.lsd_crafting_system;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.lax1dude.lsd_plugin.PluginMain;
import net.lax1dude.lsd_plugin.TiHKALMapHooks;
import net.md_5.bungee.api.ChatColor;

public class CraftingSystem implements Listener {
	
	PluginMain plugin;
	Random rand;
	ArrayList<LabBenchRecipe> recipes;
	HashMap<String, FileConfiguration> configs;
	
	HashSet<Material> signs;
	
	public CraftingSystem(PluginMain javaPlugin) {
		rand = new Random();
		plugin = javaPlugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		recipes = new ArrayList<LabBenchRecipe>();
		configs = new HashMap<String, FileConfiguration>();
		addConfig("recipes");
		getAllRecipes();
		
		Material[] signArray = {
				Material.OAK_WALL_SIGN,
				Material.JUNGLE_WALL_SIGN,
				Material.ACACIA_WALL_SIGN,
				Material.DARK_OAK_WALL_SIGN,
				Material.BIRCH_WALL_SIGN,
				Material.SPRUCE_WALL_SIGN,
				Material.CRIMSON_WALL_SIGN,
				Material.WARPED_WALL_SIGN
			};
		signs = new HashSet<Material>(Arrays.asList(signArray));
	}
	
	public void addConfig(String name) {
		String filename = name + ".yml";
		File configFile = new File(plugin.getDataFolder(), filename);
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			plugin.saveResource(filename, false);
		}
		
		YamlConfiguration config = new YamlConfiguration();
		
		try {
			config.load(configFile);
			configs.put(name, config);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration getConfig(String name) {
		return configs.get(name);
	}
	
	public void getAllRecipes() {
		recipes.clear();
		Map<String, Object> recipesInConfig = getConfig("recipes").getConfigurationSection("recipes").getValues(false);

		Iterator<Entry<String, Object>> recipeIterator = recipesInConfig.entrySet().iterator();
		while (recipeIterator.hasNext()) {
			Entry<String, Object> recipe = recipeIterator.next();
			String recipeName = recipe.getKey();
			

			String ingredientKey = "recipes." + recipeName + ".ingredients";
			ItemStack[] requiredIngredients = new ItemStack[9];
	
			Map<String, Object> requiredIngredientsFromConfig = getConfig("recipes").getConfigurationSection(ingredientKey).getValues(true);
			
			for (int i = 0; 9 > i; i++) {
				
				ItemStack requiredIngredient = (ItemStack)requiredIngredientsFromConfig.get(String.valueOf(i));
			
				requiredIngredients[i] = requiredIngredient;
			}
			
			ItemStack product = getConfig("recipes").getItemStack("recipes." + recipeName + ".product");
			
			String byproductsKey = "recipes." + recipeName + ".byproducts";
			
			List<ItemStack> byproductsList = null;
			ItemStack[] byproducts = null;
			
			
			if (getConfig("recipes").isList(byproductsKey)) {
				
				byproductsList = (List<ItemStack>)getConfig("recipes").get(byproductsKey);
				byproducts = new ItemStack[byproductsList.size()];
				byproductsList.toArray(byproducts);
			}
			
			String shapelessKey = "recipes." + recipeName + ".shapeless";
			
			if (getConfig("recipes").isBoolean(shapelessKey) && getConfig("recipes").getBoolean(shapelessKey)) {
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
	
	public void placeLabEquipmentModel(Location loc, int equipmentIndex) {
		ArmorStand entity = (ArmorStand)loc.getWorld().spawnEntity(loc.add(0.5, -0.1875, 0.5), EntityType.ARMOR_STAND);
		entity.setInvisible(true);
		entity.setInvulnerable(true);
		entity.setGravity(false);
		ItemStack labEquipment = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta labEquipmentMeta = labEquipment.getItemMeta();
		labEquipmentMeta.setCustomModelData(1337 + equipmentIndex);
		labEquipment.setItemMeta(labEquipmentMeta);
		entity.getEquipment().setHelmet(labEquipment);
		entity.addEquipmentLock(EquipmentSlot.HEAD, LockType.REMOVING_OR_CHANGING);
		entity.addEquipmentLock(EquipmentSlot.CHEST, LockType.ADDING_OR_CHANGING);
		entity.addEquipmentLock(EquipmentSlot.LEGS, LockType.ADDING_OR_CHANGING);
		entity.addEquipmentLock(EquipmentSlot.FEET, LockType.ADDING_OR_CHANGING);
		entity.addEquipmentLock(EquipmentSlot.HAND, LockType.ADDING_OR_CHANGING);
		entity.addEquipmentLock(EquipmentSlot.OFF_HAND, LockType.ADDING_OR_CHANGING);
		entity.addScoreboardTag("LabEquipmentModel");
	}
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		handleCustomBlockDrop(event.getBlock(), event.getPlayer(), event);
		
		if (event.getBlock().getType() == Material.QUARTZ_BRICKS) {
			List<Entity> entities = event.getPlayer().getNearbyEntities(6.0, 6.0, 6.0);
			for (Entity entity : entities) {
				if (entity.getType() == EntityType.ARMOR_STAND && entity.getScoreboardTags().contains("LabEquipmentModel")) {
					if (entity.getWorld().getBlockAt(entity.getLocation().add(0.0, 1.0, 0.0)).equals(event.getBlock())) {
						entity.remove();
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		handleCustomBlockDrop(event.getBlock(), null, null);
	}
	
	public boolean checkItemAndModelData(ItemStack item, Material itemId, int customModelData) {
		return item != null && item.getType() == itemId && item.hasItemMeta() && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == customModelData;
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		ItemStack heldItem = event.getItem();
		
		if (heldItem != null && heldItem.getType() == Material.BOOK && heldItem.hasItemMeta() && heldItem.getItemMeta().hasCustomModelData() && heldItem.getItemMeta().getCustomModelData() == 1337) {
			displayRecipe(event.getPlayer(), "lsd");
		} else if (checkItemAndModelData(heldItem, Material.PAPER, 1337)) {
			plugin.tripMgr.dose(event.getPlayer(), 100);
			event.getItem().setAmount(event.getItem().getAmount() - 1);
		} else if (event.hasBlock()) {
			Block block = event.getClickedBlock();
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
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onSignChange(SignChangeEvent event) {

		Block block = event.getBlock();
		if (signs.contains(block.getType())) {
			
			boolean hasLabBench = false;
			for (int i = 0; i < 4; i++) {
				if (event.getLine(i).equalsIgnoreCase("Lab Bench")) {
					hasLabBench = true;
					break;
				}
			}
			
			if (hasLabBench) {
				ArrayList<Block> quartzAdjacents = getMatchingAdjacents(block, Material.QUARTZ_BRICKS);
				if (quartzAdjacents.size() == 1) {
					Block labBenchPiece = quartzAdjacents.get(0);
					ArrayList<Block> otherBenchPieces = getMatchingAdjacents(labBenchPiece, Material.QUARTZ_BRICKS);
					if (otherBenchPieces.size() == 2) {
						placeLabEquipmentModel(labBenchPiece.getLocation(), 0);
						placeLabEquipmentModel(otherBenchPieces.get(0).getLocation(), 1);
						placeLabEquipmentModel(otherBenchPieces.get(1).getLocation(), 2);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onLootGenerate(LootGenerateEvent event) {
		if (event.getInventoryHolder() instanceof Chest && rand.nextFloat() > 0.9) {
			event.getInventoryHolder().getInventory().addItem(TiHKALMapHooks.getMapItem());
		}
	}
	
	@EventHandler
	public void onEntityInteractEntity(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		ItemStack heldItem = event.getPlayer().getInventory().getItemInMainHand();
		
		if (entity.getType() == EntityType.COW && heldItem.getType() == Material.GLASS_BOTTLE) {
			ItemMeta heldItemMeta = heldItem.getItemMeta();
			heldItemMeta.setDisplayName(ChatColor.RESET + "Methane");
			heldItemMeta.setCustomModelData(689);
			heldItem.setItemMeta(heldItemMeta);
			event.getPlayer().getInventory().setItemInMainHand(heldItem);
		}
	}
}
