package net.lax1dude.lsd_plugin;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin {

	public void onLoad() {
		getLogger().info("LSD-Plugin is loaded");
	}

	public void onDisable() {
		getLogger().info("LSD-Plugin is disabled");
	}

	public void onEnable() {
		getLogger().info("LSD-Plugin is enabled");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return false;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}
}
