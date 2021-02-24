package net.lax1dude.lsd_plugin;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.lax1dude.lsd_plugin.tripping.TripManager;
import net.radian628.lsd_crafting_system.CraftingSystem;

public class PluginMain extends JavaPlugin {

	public TripManager tripMgr;
	public CraftingSystem craftingSystem;

	public void onLoad() {
		getLogger().info("LSD-Plugin is loaded");
	}

	public void onDisable() {
		getLogger().info("LSD-Plugin is disabled");
	}

	public void onEnable() {
		getLogger().info("LSD-Plugin is enabled");
		tripMgr = new TripManager(this);
		craftingSystem = new CraftingSystem(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("lsdplugin")) {
			if(args.length < 1) {
				sender.sendMessage("Usage: /lsdplugin help");
			}else {
				if(args[0].equalsIgnoreCase("reload")) {
					
				}else if(args[0].equalsIgnoreCase("help")) {
					
				}else if(args[0].equalsIgnoreCase("dose")) {
					try {
						int mcg = Integer.parseInt(args[1]);
						if(args.length == 3) {
							Player p = getServer().getPlayer(args[2]);
							if(p != null) {
								tripMgr.dose(p, mcg);
							}else {
								sender.sendMessage("That player does not exist");
							}
						}else if(args.length == 2) {
							if(sender instanceof Player) {
								tripMgr.dose((Player)sender, mcg);
							}
						}
					}catch(Throwable t) {
						sender.sendMessage("Usage: /lsdplugin dose <micrograms> [player]");
					}
				}else if(args[0].equalsIgnoreCase("openlab")) {
					if(sender instanceof Player) {
						craftingSystem.openLab((Player)sender);
					}
				}
			}
			return true;
		}
		return false;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}
}