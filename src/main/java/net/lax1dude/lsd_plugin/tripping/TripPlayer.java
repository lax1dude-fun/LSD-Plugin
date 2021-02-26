package net.lax1dude.lsd_plugin.tripping;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import net.lax1dude.lsd_plugin.PluginMain;

public class TripPlayer {
	
	public float currentDose = 0.0f;
	public final Player player;
	public final Random random;
	public int timer = 0;

	public TripPlayer(Player player) {
		this.player = player;
		this.random = new Random();
	}

	public void dose(int mcg) {
		if(mcg == 0) {
			currentDose = 0.0f;
		}else {
			currentDose += mcg;
		}
	}

	public void tick() {
		currentDose *= 0.99986138f;
		timer += 1;
		if(currentDose > 25f) {
			if(timer % 20 == 0) {
				if(timer % (random.nextInt((int)(30000 / currentDose)) + 1) == 0) {
					try {
						PacketContainer fakeDamage = new PacketContainer(PacketType.Play.Server.UPDATE_HEALTH);
						
						fakeDamage.getFloat()
						.write(0, (float) (player.getHealth() - 0.1f))
						.write(1, player.getSaturation());
						
						fakeDamage.getIntegers()
						.write(0, player.getFoodLevel());
						
						PluginMain.protocolManager.sendServerPacket(player, fakeDamage);
						
						PacketContainer fakeDamageB = new PacketContainer(PacketType.Play.Server.UPDATE_HEALTH);
						
						fakeDamageB.getFloat()
						.write(0, (float) (player.getHealth()))
						.write(1, player.getSaturation());
						
						fakeDamageB.getIntegers()
						.write(0, player.getFoodLevel());
						
						PluginMain.protocolManager.sendServerPacket(player, fakeDamageB);
						
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
				if(timer % (random.nextInt((int)(20000 / currentDose)) + 1) == 0) {
					givePotionEffect(9, 0, random.nextInt(Math.max(400 - (int)(currentDose / 3f), 150)));
				}
				if(timer % (random.nextInt((int)(40000 / currentDose)) + 1) == 0) {
					givePotionEffect(16, 0, random.nextInt(5));
				}
			}
			if(timer % (random.nextInt((int)(3000 / currentDose)) + 1) == 0) {
				try {
					
					PacketContainer playerAbilities = new PacketContainer(PacketType.Play.Server.ABILITIES);
					playerAbilities.getBooleans()
					.write(0, player.isInvulnerable())
					.write(1, player.isFlying())
					.write(2, player.getAllowFlight())
					.write(3, player.getGameMode() == GameMode.CREATIVE);
					playerAbilities.getFloat()
					.write(0, 0.05f)
					.write(1, (random.nextFloat() - 0.5f) * (currentDose / 100000f) * (1.0f + getPlayerVelocity(player) * 50.0f) + 0.1f);
					
					PluginMain.protocolManager.sendServerPacket(player, playerAbilities);
				
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			if(timer % (random.nextInt((int)(120000 / currentDose)) + 1) == 0) {
				try {
					
					PacketContainer playerAbilities = new PacketContainer(PacketType.Play.Server.ABILITIES);
					playerAbilities.getBooleans()
					.write(0, Boolean.valueOf(player.isInvulnerable()))
					.write(1, Boolean.valueOf(player.isFlying()))
					.write(2, Boolean.valueOf(player.getAllowFlight()))
					.write(3, Boolean.valueOf(player.getGameMode() == GameMode.CREATIVE));
					playerAbilities.getFloat()
					.write(0, 0.05f)
					.write(1, (random.nextFloat() - 0.5f) * (currentDose / 3000f) * (1.0f + getPlayerVelocity(player) * 20.0f) + 0.1f);
					
					PluginMain.protocolManager.sendServerPacket(player, playerAbilities);
				
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void givePotionEffect(int id, int amp, int ticks) {
		try {
			PacketContainer nausea = new PacketContainer(PacketType.Play.Server.ENTITY_EFFECT);
			nausea.getIntegers().write(0, player.getEntityId()).write(1, ticks);
			nausea.getBytes().write(0, (byte) id).write(1, (byte) amp).write(2, (byte) 0x02);
			PluginMain.protocolManager.sendServerPacket(player, nausea);
			(new BukkitRunnable() {

				@Override
				public void run() {
					try {
						PacketContainer remove = new PacketContainer(PacketType.Play.Server.REMOVE_ENTITY_EFFECT);
						remove.getIntegers().write(0, player.getEntityId());
						remove.getEffectTypes().write(0, PotionEffectType.getById(id));
						PluginMain.protocolManager.sendServerPacket(player, remove);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
				
			}).runTaskLater(PluginMain.instance, ticks);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private static float getPlayerVelocity(Player p) {
		return (float) p.getVelocity().length();
	}

}
