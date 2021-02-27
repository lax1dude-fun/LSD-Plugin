package net.lax1dude.lsd_plugin.tripping;

import java.util.ArrayList;
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
	
	public final ArrayList<TripEntity> entities = new ArrayList();

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
	
	public void addEntity(TripEntity e) {
		e.alive = true;
		entities.add(e);
		e.initialize();
	}
	
	public void removeEntity(TripEntity e) {
		e.alive = false;
		e.destroy();
		entities.remove(e);
	}

	public void tick() {
		currentDose *= 0.99986138f;
		timer += 1;
		
		ArrayList<TripEntity> entitiesToTick = new ArrayList();
		entitiesToTick.addAll(entities);
		
		for(TripEntity t : entitiesToTick) {
			t.update();
			if(!t.alive) {
				removeEntity(t);
			}
		}
		
		if(currentDose > 25f) {
			if(timer % 20 == 0) {
				if(timer % (random.nextInt((int)(30000 / currentDose) + 1) + 1) == 0) {
					PacketConstructors.sendPacket(player, PacketConstructors.createHealthUpdate((float) (player.getHealth() - 0.1f), player.getFoodLevel(), player.getSaturation()));
					PacketConstructors.sendPacket(player, PacketConstructors.createHealthUpdate((float) (player.getHealth()), player.getFoodLevel(), player.getSaturation()));
				}
				if(timer % (random.nextInt((int)(20000 / currentDose) + 1) + 1) == 0) {
					givePotionEffect(9, 0, random.nextInt(Math.max(400 - (int)(currentDose / 3f), 150)));
				}
				if(timer % (random.nextInt((int)(2000 / currentDose) + 1) + 1) == 0) {
					givePotionEffect(9, 0, random.nextInt(100) + 100);
				}
				if(timer % (random.nextInt((int)(40000 / currentDose) + 1) + 1) == 0) {
					givePotionEffect(16, 0, random.nextInt(5));
				}
				if(currentDose > 140f && timer % (random.nextInt((int)(4000 / currentDose) + 1) + 1) == 0) {
					int particle;
					do {
						particle = random.nextInt(62);
					}while(particle == 3 || particle == 14 || particle == 23 || particle == 32 || particle == 34 || particle == 21 || particle == 16 || particle == 22 || particle == 35 || particle == 42 || particle == 44 || particle == 46 || particle == 36 || particle == 56 || particle == 57 || particle == 2);
					createParticlePlane(particle);
				}
				if(currentDose > 180f && timer % (random.nextInt((int)(1000 / currentDose) + 1) + 1) == 0) {
					int particle;
					do {
						particle = random.nextInt(62);
					}while(particle == 3 || particle == 14 || particle == 23 || particle == 32 || particle == 34 || particle == 21 || particle == 16 || particle == 22 || particle == 35 || particle == 42 || particle == 44 || particle == 46 || particle == 36 || particle == 2);
					this.addEntity(new TripEntityGhost(this, 0.0D, 0.0D, 0.0D, particle, random.nextFloat() * 0.5f));
				}
				if(currentDose > 180f && timer % (random.nextInt((int)(300 / currentDose) + 1) + 1) == 0) {
					this.addEntity(new TripEntityItem(this, 0.0D, 0.0D, 0.0D, PacketConstructors.getRandomItem(random), random.nextFloat() * 1.0f));
				}
				if(currentDose > 180f && timer % (random.nextInt((int)(30000 / currentDose) + 1) + 1) == 0) {
					this.addEntity(new TripEntityLiving(this, (random.nextFloat() - 0.5f) * 6.0f, (random.nextFloat() - 0.5f) * 6.0f, (random.nextFloat() - 0.5f) * 6.0f, "creeper", random.nextFloat() * 1.0f));
				}
			}
			if(timer % (random.nextInt((int)(2000 / currentDose)) + 1) == 0) {
				int particle;
				do {
					particle = random.nextInt(62);
				}while(particle == 3 || particle == 14 || particle == 23 || particle == 32 || particle == 34 || particle == 21 || particle == 16);
				PacketConstructors.sendPacket(player, PacketConstructors.createParticles(particle, true, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 4.0f, 2.0f, 4.0f, 1.0f, random.nextInt(2 + (int)(currentDose / 70)) + 1));
			}
			if(currentDose > 100f && timer % (random.nextInt((int)(3000 / currentDose) + 1) + 1) == 0) {
				PacketConstructors.sendPacket(player, PacketConstructors.createPlayerAbilities(player.isInvulnerable(), player.isFlying(), player.getAllowFlight(), player.getGameMode() == GameMode.CREATIVE,
						0.05f, (random.nextFloat() - 0.5f) * (currentDose / 2000000f) * (1.0f + getPlayerVelocity(player) * 400.0f) + 0.1f));
			}
			if(currentDose > 200f && timer % (random.nextInt((int)(120000 / currentDose) + 1) + 1) == 0) {
				PacketConstructors.sendPacket(player, PacketConstructors.createPlayerAbilities(player.isInvulnerable(), player.isFlying(), player.getAllowFlight(), player.getGameMode() == GameMode.CREATIVE,
						0.05f, (random.nextFloat() - 0.5f) * (currentDose / 6000f) * (1.0f + getPlayerVelocity(player) * 20.0f) + 0.1f));
			}
		}
	}
	
	private void createParticlePlane(int p) {
		int x1 = player.getLocation().getBlockX() - 3 + random.nextInt(5);
		final int y = player.getLocation().getBlockY();
		int z1 = player.getLocation().getBlockZ() - 3 + random.nextInt(5);
		int x2 = x1 + 1 + random.nextInt(2);
		int z2 = z1 + 1 + random.nextInt(2);
		float timer = 0;
		for(int x = x1; x < x2; ++x) {
			for(int z = z1; z < z2; ++z) {
				for(int x3 = 0; x3 < 8; ++x3) {
					for(int z3 = 0; z3 < 8; ++z3) {
						final float x4 = x + ((float)x3 / 8.0f);
						final float z4 = z + ((float)z3 / 8.0f);
						(new BukkitRunnable() {
							@Override
							public void run() {
								PacketConstructors.sendPacket(player, PacketConstructors.createParticles(p, true, x4, y, z4, 0.0f, 0.0f, 0.0f, 0.0f, 1));
							}	
						}).runTaskLater(PluginMain.instance, (int)timer);
					}
					timer += 0.3f;
				}
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
