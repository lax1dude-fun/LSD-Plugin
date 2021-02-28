package net.lax1dude.lsd_plugin.tripping;

import java.util.UUID;

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
				this.trip.player.getLocation().getYaw(), this.trip.player.getLocation().getPitch(), 0.0f, 0,
				(float)this.trip.player.getVelocity().getX(), (float)this.trip.player.getVelocity().getY(), (float)this.trip.player.getVelocity().getZ()));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityEffect(-id, (byte) 14, (byte) 0, 65536, (byte) 0x02));
		
	}

	@Override
	public void tick() {
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createMoveEntity(-id,
				this.trip.player.getLocation().getX(), this.trip.player.getLocation().getY() + getEyeHeight(), this.trip.player.getLocation().getZ(),
				this.trip.player.getLocation().getYaw(), this.trip.player.getLocation().getPitch()));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityVelocity(-id,
				(float)this.trip.player.getVelocity().getX(), (float)this.trip.player.getVelocity().getY(), (float)this.trip.player.getVelocity().getZ()));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityLook(-id, -this.trip.player.getLocation().getPitch()));
	}

	@Override
	public void destroy() {
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createDestoryEntity(-id));
	}

}
