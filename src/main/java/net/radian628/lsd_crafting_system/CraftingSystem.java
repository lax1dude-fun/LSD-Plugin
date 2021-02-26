package net.radian628.lsd_crafting_system;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

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
	
	public void handleCustomBlockDrop(Block block, Player blockBreaker) {
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
			
		}
	}	
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		handleCustomBlockDrop(event.getBlock(), event.getPlayer());
	}
}
