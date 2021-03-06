package net.lax1dude.lsd_plugin.tripping;

import java.util.UUID;

import org.bukkit.Location;

public class TripEntityItem extends TripEntity {
	
	public float speed;
	public String itemId;
	
	public TripEntityItem(TripPlayer trip, double x, double y, double z, String itemId, float speed) {
		super(trip, x, y, z);
		this.speed = speed;
		this.itemId = itemId;
	}

	@Override
	public void initialize() {
		motionX = (trip.random.nextFloat() - 0.5f) * speed;
		motionY = (trip.random.nextFloat() - 0.5f) * speed;
		motionZ = (trip.random.nextFloat() - 0.5f) * speed;
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createSpawnEntity(-id, UUID.randomUUID(), "item", posX + this.trip.player.getLocation().getX(), posY + this.trip.player.getLocation().getY(), posZ + this.trip.player.getLocation().getZ(), 0.0F, 0.0F, 0, 0.0F, 0.0F, 0.0F));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityDataSlot(-id, 7, itemId));
	}

	@Override
	public void tick() {
		if(posX > 5f) motionX = (-trip.random.nextFloat()) * speed * 0.5f;
		if(posX < -5f) motionX = (trip.random.nextFloat()) * speed * 0.5f;
		if(posY > 5f) motionY = (-trip.random.nextFloat()) * speed * 0.5f;
		if(posY < -1f) motionY = (trip.random.nextFloat()) * speed * 0.5f;
		if(posZ > 5f) motionZ = (-trip.random.nextFloat()) * speed * 0.5f;
		if(posZ < -5f) motionZ = (trip.random.nextFloat()) * speed * 0.5f;
		motionX += (trip.random.nextFloat() - 0.5f) * 0.05f;
		motionY += (trip.random.nextFloat() - 0.5f) * 0.05f;
		motionZ += (trip.random.nextFloat() - 0.5f) * 0.05f;
		
		Location l = this.trip.player.getLocation();
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createMoveEntity(-id, posX + l.getX(), posY + l.getY(), posZ + l.getZ(), 0.0f, 0.0f));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityVelocity(-id, motionX, motionY, motionZ));
		if(this.trip.random.nextInt(200) == 0 || this.trip.currentDose < 150.0f) this.alive = false;
	}

	@Override
	public void destroy() {
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createDestoryEntity(-id));
	}

}
