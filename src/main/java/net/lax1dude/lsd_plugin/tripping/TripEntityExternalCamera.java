package net.lax1dude.lsd_plugin.tripping;

public class TripEntityExternalCamera extends TripEntityCreeper {

	public TripEntityExternalCamera(TripPlayer trip, double x, double y, double z, float speed, String entity) {
		super(trip, x, y, z, speed, entity);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createCamera(-id));
	}

	@Override
	public void destroy() {
		PacketConstructors.sendPacket(this.trip.player, PacketConstructors.createCamera(this.trip.player.getEntityId()));
		super.destroy();
	}
	
}
