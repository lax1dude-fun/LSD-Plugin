package net.lax1dude.lsd_plugin.tripping;

import java.util.UUID;

public class TripEntityCreeper extends TripEntity {

	private float speed;
	private String entity;

	public TripEntityCreeper(TripPlayer trip, double x, double y, double z, float speed, String entity) {
		super(trip, x, y, z);
		this.speed = speed;
		this.entity = entity;
	}
	
	private void recalculateMotion() {
		int dx = (int) (posX - this.trip.player.getLocation().getX());
		int dz = (int) (posZ - this.trip.player.getLocation().getZ());
		double len = Math.sqrt(dx * dx + dz * dz);
		motionX = (float) (-dx / len) * speed;
		motionZ = (float) (-dz / len) * speed;
	}

	@Override
	public void initialize() {
		posX += this.trip.player.getLocation().getX();
		posZ += this.trip.player.getLocation().getZ();
		posY = 1.0d + trip.player.getWorld().getHighestBlockYAt((int)Math.floor(posX), (int)Math.floor(posZ));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createSpawnLivingEntity(-id, UUID.randomUUID(), entity, posX, posY, posZ, 0.0F, 0.0F, 0.0F, 0, 0.0F, 0.0F, 0.0F));
		
		byte flags = 0x00;
		if(trip.random.nextInt(15) == 0) flags |= 0x01;
		if(trip.random.nextInt(15) == 0) flags |= 0x02;
		if(trip.random.nextInt(15) == 0) flags |= 0x40;
		if(trip.random.nextInt(15) == 0) flags |= (0x40 | 0x20);
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityData(-id, 0, 0, flags));
		
		recalculateMotion();
	}

	@Override
	public void tick() {
		if(this.trip.random.nextInt(30) == 0) recalculateMotion();
		if(this.trip.random.nextInt(400) == 0) this.alive = false;
		motionX += (trip.random.nextFloat() - 0.5f) * 0.2f * speed;
		motionY += (trip.random.nextFloat() - 0.5f) * 0.2f * speed;
		motionZ += (trip.random.nextFloat() - 0.5f) * 0.2f * speed;
		posY = 1.0d + trip.player.getWorld().getHighestBlockYAt((int)Math.floor(posX), (int)Math.floor(posZ));
		int dx = (int) (posX - this.trip.player.getLocation().getX());
		int dz = (int) (posZ - this.trip.player.getLocation().getZ());
		float yaw = 180.0f - (float)Math.atan2(dx, dz) * TripEntityLiving.radToDeg;
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createMoveEntity(-id, posX, posY, posZ, yaw, 0.0f));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityVelocity(-id, motionX, motionY, motionZ));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityLook(-id, yaw));
		if(dx < 1.0f && dx > -1.0f && dz < 1.0f && dz > -1.0f) {
			this.alive = false;
			PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createParticles(15, true, posX, posY + 1.0D, posZ, 0.2f, 0.2f, 0.2f, 0.0f, 5));
		}
	}

	@Override
	public void destroy() {
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createDestoryEntity(-id));
	}

}
