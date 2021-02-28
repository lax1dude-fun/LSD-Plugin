package net.lax1dude.lsd_plugin.tripping;

import java.util.UUID;

public class TripEntityGhostBlock extends TripEntity {
	
	public int block;
	
	public TripEntityGhostBlock(TripPlayer trip, double x, double y, double z, int block, int meta) {
		super(trip, x, y, z);
		this.block = block | (meta << 12);
	}

	@Override
	public void initialize() {
		posY = 1.0d + trip.player.getWorld().getHighestBlockYAt((int)Math.floor(posX), (int)Math.floor(posZ));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createSpawnEntity(-id, UUID.randomUUID(), "falling_block", posX, posY, posZ, 0.0F, 0.0F, block, 0.0F, 0.0F, 0.0F));
	}

	@Override
	public void tick() {
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createMoveEntity(-id, posX, posY, posZ, 0.0f, 0.0f));
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createEntityVelocity(-id, 0.0f, 0.0f, 0.0f));
		if(this.trip.random.nextInt(400) == 0 || this.trip.currentDose < 150.0f) this.alive = false;
	}

	@Override
	public void destroy() {
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createDestoryEntity(-id));
	}

}
