package net.lax1dude.lsd_plugin.tripping;

import java.util.UUID;

public class TripEntityLiving extends TripEntity {
	
	public float speed;
	public String entity;
	
	public TripEntityLiving(TripPlayer trip, double x, double y, double z, String entity, float speed) {
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
		//PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityDataSlot(-id, 7, itemId));
	}
	
	public static final float radToDeg = (float)(360.0D / 2.0D / Math.PI);

	@Override
	public void tick() {
		if(posX > 5f) motionX = (-trip.random.nextFloat()) * speed * 0.5f;
		if(posX < -5f) motionX = (trip.random.nextFloat()) * speed * 0.5f;
		//if(posY > 5f) motionY = (-trip.random.nextFloat()) * speed * 0.5f;
		//if(posY < -1f) motionY = (trip.random.nextFloat()) * speed * 0.5f;
		if(posZ > 5f) motionZ = (-trip.random.nextFloat()) * speed * 0.5f;
		if(posZ < -5f) motionZ = (trip.random.nextFloat()) * speed * 0.5f;
		motionX += (trip.random.nextFloat() - 0.5f) * 0.05f;
		motionY += (trip.random.nextFloat() - 0.5f) * 0.05f;
		motionZ += (trip.random.nextFloat() - 0.5f) * 0.05f;
		
		posY = 1.0d + trip.player.getWorld().getHighestBlockYAt((int)Math.floor(posX + this.trip.player.getLocation().getX()), (int)Math.floor(posZ + this.trip.player.getLocation().getZ())) - this.trip.player.getLocation().getY();
		
		float yaw = 180.0f - (float)Math.atan2(posX, posZ) * radToDeg;
		
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createMoveEntity(-id, posX + this.trip.player.getLocation().getX(), posY + this.trip.player.getLocation().getY(), posZ + this.trip.player.getLocation().getZ(), yaw, 0.0f));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityVelocity(-id, motionX, motionY, motionZ));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityLook(-id, yaw));
		if(this.trip.random.nextInt(200) == 0 || this.trip.currentDose < 150.0f) this.alive = false;
	}

	@Override
	public void destroy() {
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createDestoryEntity(-id));
	}

}
