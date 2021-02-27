package net.lax1dude.lsd_plugin.tripping;

public class TripEntityGhost extends TripEntity {
	
	public int particle;
	public float speed;
	
	public TripEntityGhost(TripPlayer trip, double x, double y, double z, int particle, float speed) {
		super(trip, x, y, z);
		this.particle = particle;
		this.speed = speed;
	}

	@Override
	public void initialize() {
		motionX = (trip.random.nextFloat() - 0.5f) * speed;
		motionY = (trip.random.nextFloat() - 0.5f) * speed;
		motionZ = (trip.random.nextFloat() - 0.5f) * speed;
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
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createParticles(particle, true, posX + this.trip.player.getLocation().getX(), posY + this.trip.player.getLocation().getY(), posZ + this.trip.player.getLocation().getZ(), 0.0f, 0.0f, 0.0f, 0.0f, 1));
		if(this.trip.random.nextInt(200) == 0) this.alive = false;
	}

	@Override
	public void destroy() {
		
	}

}
