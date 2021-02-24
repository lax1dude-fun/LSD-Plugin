package net.lax1dude.lsd_plugin;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.radian628.lsd_crafting_system.CraftingSystem;

public class PluginMain extends JavaPlugin {

	CraftingSystem craftingSystem;
	
	public void onLoad() {
		getLogger().info("LSD-Plugin is loaded");
	}

	public void onDisable() {
		getLogger().info("LSD-Plugin is disabled");
	}

	public void onEnable() {
		craftingSystem = new CraftingSystem(this);
		getLogger().info("LSD-Plugin is enabled");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmd = command.getName();
		
		switch (cmd) {
			case "openlab":
				if (sender instanceof Player) {
					craftingSystem.openLab((Player)sender);
				}
				break;
		}
		
		return false;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}
}
