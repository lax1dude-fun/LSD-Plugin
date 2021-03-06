package net.lax1dude.lsd_plugin.tripping;

import java.util.UUID;

import org.bukkit.Location;

public class TripEntityFlyingMob extends TripEntity {

	private float speed;
	private String entity;

	public TripEntityFlyingMob(TripPlayer trip, double x, double y, double z, float speed, String entity) {
		super(trip, x, y, z);
		this.speed = speed;
		this.entity = entity;
	}

	@Override
	public void initialize() {
		motionX = (trip.random.nextFloat() - 0.5f) * speed;
		motionY = (trip.random.nextFloat() - 0.5f) * speed;
		motionZ = (trip.random.nextFloat() - 0.5f) * speed;
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createSpawnLivingEntity(-id, UUID.randomUUID(), entity, posX + this.trip.player.getLocation().getX(), posY + this.trip.player.getLocation().getY(), posZ + this.trip.player.getLocation().getZ(), 0.0F, 0.0F, 0.0F, 0, 0.0F, 0.0F, 0.0F));

		byte flags = 0x00;
		if(trip.random.nextInt(15) == 0) flags |= 0x01;
		if(trip.random.nextInt(15) == 0) flags |= 0x02;
		if(trip.random.nextInt(15) == 0) flags |= 0x40;
		if(trip.random.nextInt(15) == 0) flags |= (0x40 | 0x20);
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityData(-id, 0, 0, flags));
	}
	
	public static final float radToDeg = (float)(360.0D / 2.0D / Math.PI);

	@Override
	public void tick() {
		if(posX > 50f) motionX = (-trip.random.nextFloat()) * speed * 0.5f;
		if(posX < -50f) motionX = (trip.random.nextFloat()) * speed * 0.5f;
		if(posY > 50f) motionY = (-trip.random.nextFloat()) * speed * 0.5f;
		if(posY < -5f) motionY = (trip.random.nextFloat()) * speed * 0.5f;
		if(posZ > 50f) motionZ = (-trip.random.nextFloat()) * speed * 0.5f;
		if(posZ < -50f) motionZ = (trip.random.nextFloat()) * speed * 0.5f;
		motionX += (trip.random.nextFloat() - 0.5f) * 0.05f;
		motionY += (trip.random.nextFloat() - 0.5f) * 0.05f;
		motionZ += (trip.random.nextFloat() - 0.5f) * 0.05f;
		float yaw = (float)Math.atan2(motionX, motionZ) * radToDeg;
		
		Location l = this.trip.player.getLocation();
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createMoveEntity(-id, posX + l.getX(), posY + l.getY(), posZ + l.getZ(), yaw, 0.0f));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityVelocity(-id, motionX, motionY, motionZ));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityLook(-id, yaw));
		if(this.trip.random.nextInt(400) == 0) this.alive = false;
	}

	@Override
	public void destroy() {
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createDestoryEntity(-id));
	}

}
