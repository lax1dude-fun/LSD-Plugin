package net.lax1dude.lsd_plugin.tripping;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.lax1dude.lsd_plugin.PluginMain;

public class TripManager implements Listener {
	
	public final PluginMain plugin;
	
	private final HashMap<String,TripPlayer> playersTripping = new HashMap();

	public TripManager(PluginMain pluginMain) {
		plugin = pluginMain;
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			for(TripPlayer t : playersTripping.values()) {
				t.tick();
			}
		}, 0, 1);
		
	}
	
	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent evt) {
		if(!playersTripping.containsKey(evt.getPlayer().getName())) {
			playersTripping.put(evt.getPlayer().getName(), new TripPlayer(evt.getPlayer()));
		}
	}
	
	@EventHandler
	public void playerQuitEvent(PlayerQuitEvent evt) {
		playersTripping.remove(evt.getPlayer().getName());
	}

	public void dose(Player sender, int mcg) {
		if(playersTripping.containsKey(sender.getName())) {
			playersTripping.get(sender.getName()).dose(mcg);
		}
	}
	
	public int getDose(Player sender) {
		if(playersTripping.containsKey(sender.getName())) {
			return (int) playersTripping.get(sender.getName()).currentDose;
		}
		return 0;
	}

}