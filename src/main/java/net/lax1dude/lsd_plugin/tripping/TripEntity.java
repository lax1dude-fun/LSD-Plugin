package net.lax1dude.lsd_plugin.tripping;

public abstract class TripEntity {
	
	private static int entityIdNext = 0;
	
	public final TripPlayer trip;
	
	public int id;
	public boolean alive = true;
	
	public int age;

	public double posX;
	public double posY;
	public double posZ;
	
	public float motionX = 0.0f;
	public float motionY = 0.0f;
	public float motionZ = 0.0f;
	
	public TripEntity(TripPlayer trip, double x, double y, double z) {
		this.trip = trip;
		this.id = entityIdNext++;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.age = 0;
	}
	
	public void update() {
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		++age;
		tick();
	}

	public abstract void initialize();
	public abstract void tick();
	public abstract void destroy();
	
	public int hashCode() {
		return id;
	}
	
	public boolean equals(Object o) {
		return (o instanceof TripEntity) && ((TripEntity)o).id == id;
	}
}
