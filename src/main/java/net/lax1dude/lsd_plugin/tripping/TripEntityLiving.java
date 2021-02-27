package net.lax1dude.lsd_plugin.tripping;

import java.util.UUID;

public class TripEntityLiving extends TripEntity {
	
	public String entity;
	
	public TripEntityLiving(TripPlayer trip, double x, double y, double z, String entity) {
		super(trip, x, y, z);
		this.entity = entity;
	}

	@Override
	public void initialize() {
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createSpawnLivingEntity(-id, UUID.randomUUID(), entity, posX + this.trip.player.getLocation().getX(), posY + this.trip.player.getLocation().getY(), posZ + this.trip.player.getLocation().getZ(), 0.0F, 0.0F, 0.0F, 0, 0.0F, 0.0F, 0.0F));
	}
	
	public static final float radToDeg = (float)(360.0D / 2.0D / Math.PI);

	@Override
	public void tick() {
		posY = 1.0d + trip.player.getWorld().getHighestBlockYAt((int)Math.floor(posX + this.trip.player.getLocation().getX()), (int)Math.floor(posZ + this.trip.player.getLocation().getZ())) - this.trip.player.getLocation().getY();
		float yaw = 180.0f - (float)Math.atan2(posX, posZ) * radToDeg;
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createMoveEntity(-id, posX + this.trip.player.getLocation().getX(), posY + this.trip.player.getLocation().getY(), posZ + this.trip.player.getLocation().getZ(), yaw, 0.0f));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityVelocity(-id, motionX, motionY, motionZ));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityLook(-id, yaw));
	}

	@Override
	public void destroy() {
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createDestoryEntity(-id));
	}

}
