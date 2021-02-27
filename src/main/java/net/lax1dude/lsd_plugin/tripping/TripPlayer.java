package net.lax1dude.lsd_plugin.tripping;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
					PacketConstructors.sendPacket(player, PacketConstructors.createHealthUpdate((float) (player.getHealth() - 0.1f), player.getFoodLevel(), player.getSaturation()));
					PacketConstructors.sendPacket(player, PacketConstructors.createHealthUpdate((float) (player.getHealth()), player.getFoodLevel(), player.getSaturation()));
				}
				if(timer % (random.nextInt((int)(20000 / currentDose)) + 1) == 0) {
					givePotionEffect(9, 0, random.nextInt(Math.max(400 - (int)(currentDose / 3f), 150)));
				}
				if(timer % (random.nextInt((int)(2000 / currentDose)) + 1) == 0) {
					givePotionEffect(9, 0, random.nextInt(100) + 100);
				}
				if(timer % (random.nextInt((int)(40000 / currentDose)) + 1) == 0) {
					givePotionEffect(16, 0, random.nextInt(5));
				}
			}
			if(timer % (random.nextInt((int)(2000 / currentDose)) + 1) == 0) {
				int particle;
				do {
					particle = random.nextInt(62);
				}while(particle == 3 || particle == 14 || particle == 23 || particle == 32 || particle == 34 || particle == 21);
				PacketConstructors.sendPacket(player, PacketConstructors.createParticles(particle, true, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 2.0f, 2.0f, 2.0f, 1.0f, 2));
			}
			if(timer % (random.nextInt((int)(3000 / currentDose)) + 1) == 0) {
				PacketConstructors.sendPacket(player, PacketConstructors.createPlayerAbilities(player.isInvulnerable(), player.isFlying(), player.getAllowFlight(), player.getGameMode() == GameMode.CREATIVE,
						0.05f, (random.nextFloat() - 0.5f) * (currentDose / 2000000f) * (1.0f + getPlayerVelocity(player) * 400.0f) + 0.1f));
			}
			if(timer % (random.nextInt((int)(120000 / currentDose)) + 1) == 0) {
				PacketConstructors.sendPacket(player, PacketConstructors.createPlayerAbilities(player.isInvulnerable(), player.isFlying(), player.getAllowFlight(), player.getGameMode() == GameMode.CREATIVE,
						0.05f, (random.nextFloat() - 0.5f) * (currentDose / 6000f) * (1.0f + getPlayerVelocity(player) * 20.0f) + 0.1f));
			}
		}
	}
	
	private void givePotionEffect(int id, int amp, int ticks) {
		PacketConstructors.sendPacket(player, PacketConstructors.createEntityEffect(player.getEntityId(), (byte) id, (byte) amp, ticks, (byte) 0x02));
		(new BukkitRunnable() {
			@Override
			public void run() {
				PacketConstructors.sendPacket(player, PacketConstructors.createRemoveEntityEffect(player.getEntityId(), (byte) id));
			}	
		}).runTaskLater(PluginMain.instance, ticks);
	}
	
	private static float getPlayerVelocity(Player p) {
		return (float) p.getVelocity().length();
	}

}
