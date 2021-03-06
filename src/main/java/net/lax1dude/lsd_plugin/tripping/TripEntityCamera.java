package net.lax1dude.lsd_plugin.tripping;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import net.lax1dude.lsd_plugin.tripping.TripPlayer.CameraMode;

public class TripEntityCamera extends TripEntity {
	
	public TripPlayer.CameraMode cameraMode;

	public TripEntityCamera(TripPlayer trip, TripPlayer.CameraMode mode) {
		super(trip, 0, 0, 0);
		cameraMode = mode;
	}
	
	private double getEyeHeight() {
		return this.trip.player.getEyeHeight() - (cameraMode == CameraMode.CREEPER ? 1.7f * 0.85f : (cameraMode == CameraMode.SPIDER ? 0.65F : 2.55f));
	}

	@Override
	public void initialize() {
		String name = (cameraMode == CameraMode.CREEPER ? "creeper" : (cameraMode == CameraMode.SPIDER ? "spider" : "enderman"));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createSpawnLivingEntity(-id, UUID.randomUUID(), name,
				this.trip.player.getLocation().getX(), this.trip.player.getLocation().getY() + getEyeHeight(), this.trip.player.getLocation().getZ(),
				this.trip.player.getLocation().getYaw(), this.trip.player.getLocation().getPitch(), this.trip.player.getLocation().getYaw(), 0,
				(float)this.trip.player.getVelocity().getX(), (float)this.trip.player.getVelocity().getY(), (float)this.trip.player.getVelocity().getZ()));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityData(-id, 0, 0, (byte)0x20));
		
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createCamera(-id));
	}

	@Override
	public void tick() {
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityData(this.trip.player.getEntityId(), 0, 0, (byte)0x20));
		
		Location l = this.trip.player.getLocation();
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createMoveEntity(-id,
				l.getX(), l.getY() + getEyeHeight(), l.getZ(),
				l.getYaw(), l.getPitch()));
		
		Vector velocity = this.trip.player.getVelocity();
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityVelocity(-id,
				(float)velocity.getX(), (float)velocity.getY(), (float)velocity.getZ()));
		
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityLook(-id, l.getYaw()));
		
		if(this.trip.random.nextInt(400) == 0) this.alive = false;
	}

	@Override
	public void destroy() {
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityData(this.trip.player.getEntityId(), 0, 0, (byte)0x00));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createDestoryEntity(-id));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createCamera(this.trip.player.getEntityId()));
	}

}
