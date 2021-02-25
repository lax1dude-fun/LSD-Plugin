package net.radian628.lsd_crafting_system;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftingSystem implements Listener {
	
	JavaPlugin plugin;
	Random rand;
	
	public CraftingSystem(JavaPlugin javaPlugin) {
		rand = new Random();
		plugin = javaPlugin;
		plugin.getLogger().info("test");
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void openLab(Player player) {
		new LabBench(plugin, player);
	}
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		BlockData blockData = event.getBlock().getBlockData();
		
		ItemStack heldItem = event.getPlayer().getInventory().getItemInMainHand();
		
		boolean hasSilkTouch = false;
		if (heldItem != null) {
			if (heldItem.getItemMeta() != null && heldItem.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
				hasSilkTouch = true;
			}
		}
		if (blockData.getMaterial() == Material.JUNGLE_LEAVES) {
			if (event.isDropItems() && !hasSilkTouch && heldItem.getType() != Material.SHEARS) {
				if (rand.nextFloat() > 0.99) {
					event.setDropItems(false);
					
					ItemStack lemon = new ItemStack(Material.APPLE);
					ItemMeta lemonMeta = lemon.getItemMeta();
					lemonMeta.setCustomModelData(666);
					lemonMeta.setDisplayName("\"name\":\"Lemon\",\"italic\":\"false\"");
					lemon.setItemMeta(lemonMeta);
					
					event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), lemon);
				}
			}
		}
	}
}
