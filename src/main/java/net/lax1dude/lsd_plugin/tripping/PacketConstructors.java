package net.lax1dude.lsd_plugin.tripping;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R3.Block;
import net.minecraft.server.v1_16_R3.DataWatcher.Item;
import net.minecraft.server.v1_16_R3.DataWatcherObject;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.IRegistry;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import net.minecraft.server.v1_16_R3.MobEffectList;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketDataSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutAbilities;
import net.minecraft.server.v1_16_R3.PacketPlayOutCamera;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_16_R3.PacketPlayOutRemoveEntityEffect;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R3.PacketPlayOutUpdateHealth;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_16_R3.Particle;
import net.minecraft.server.v1_16_R3.ParticleParam;
import net.minecraft.server.v1_16_R3.RegistryBlocks;
import net.minecraft.server.v1_16_R3.Vec3D;

public class PacketConstructors {
	
	public static void sendPacket(Player p, Object pkt) {
		((CraftPlayer)p).getHandle().playerConnection.sendPacket((Packet)pkt);
	}
	
	private static void setField(Object o, String f, Object v) {
		try {
			Field ff = o.getClass().getDeclaredField(f);
			ff.setAccessible(true);
			ff.set(o, v);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private static void setFieldI(Object o, String f, int v) {
		try {
			Field ff = o.getClass().getDeclaredField(f);
			ff.setAccessible(true);
			ff.setInt(o, v);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private static void setFieldF(Object o, String f, float v) {
		try {
			Field ff = o.getClass().getDeclaredField(f);
			ff.setAccessible(true);
			ff.setFloat(o, v);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private static void setFieldD(Object o, String f, double v) {
		try {
			Field ff = o.getClass().getDeclaredField(f);
			ff.setAccessible(true);
			ff.setDouble(o, v);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private static void setFieldB(Object o, String f, byte b) {
		try {
			Field ff = o.getClass().getDeclaredField(f);
			ff.setAccessible(true);
			ff.setByte(o, b);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private static void setFieldBool(Object o, String f, boolean b) {
		try {
			Field ff = o.getClass().getDeclaredField(f);
			ff.setAccessible(true);
			ff.setBoolean(o, b);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static Object createHealthUpdate(float health, int food, float saturation) {
		return new PacketPlayOutUpdateHealth(health, food, saturation);
	}
	
	public static Object createPlayerAbilities(boolean invul, boolean isFlying, boolean canFly, boolean canInstantlyBuild, float flyspeed, float fov) {
		PacketPlayOutAbilities abilities = new PacketPlayOutAbilities();
		setFieldBool(abilities, "a", invul);
		setFieldBool(abilities, "b", isFlying);
		setFieldBool(abilities, "c", canFly);
		setFieldBool(abilities, "d", canInstantlyBuild);
		setFieldF(abilities, "e", flyspeed);
		setFieldF(abilities, "f", fov);
		return abilities;
	}
	
	public static Object createEntityEffect(int entity, byte effect, byte amp, int duration, byte flags) {
		PacketPlayOutEntityEffect ret = new PacketPlayOutEntityEffect();
		setFieldI(ret, "a", entity);
		setFieldB(ret, "b", effect);
		setFieldB(ret, "c", amp);
		setFieldI(ret, "d", duration);
		setFieldB(ret, "e", flags);
		return ret;
	}
	
	public static Object createRemoveEntityEffect(int entity, byte effect) {
		PacketPlayOutRemoveEntityEffect ret = new PacketPlayOutRemoveEntityEffect();
		setFieldI(ret, "a", entity);
		setField(ret, "b", MobEffectList.fromId(effect));
		return ret;
	}
	
	public static Object createParticles(int particleType, boolean distance, double x, double y, double z, float sx, float sy, float sz, float data, int count) {
		return new PacketPlayOutWorldParticles(new ParticleParam() {
			
			@Override
			public Particle<?> getParticle() {
				return IRegistry.PARTICLE_TYPE.fromId(particleType);
			}
			
			@Override
			public void a(PacketDataSerializer paramPacketDataSerializer) {
			}
			
			@Override
			public String a() {
				return IRegistry.PARTICLE_TYPE.fromId(particleType).toString();
			}
			
		}, distance, x, y, z, sx, sy, sz, data, count);
	}
	
	public static Object createSpawnEntity(int id, UUID uuid, String type, double x, double y, double z, float yaw, float pitch, int data, float motionx, float motiony, float motionz) {
		return new PacketPlayOutSpawnEntity(id, uuid, x, y, z, yaw, pitch, EntityTypes.a(type).get(), data, new Vec3D(motionx, motiony, motionz));
	}
	
	public static Object createSpawnLivingEntity(int id, UUID uuid, String type, double x, double y, double z, float yaw, float pitch, float headpitch, int data, float motionx, float motiony, float motionz) {
		PacketPlayOutSpawnEntityLiving ret = new PacketPlayOutSpawnEntityLiving();
		setFieldI(ret, "a", id);
		setField(ret, "b", uuid);
		setFieldI(ret, "c", IRegistry.ENTITY_TYPE.a(EntityTypes.a(type).get()));
		setFieldD(ret, "d", x);
		setFieldD(ret, "e", y);
		setFieldD(ret, "f", z);
		setFieldI(ret, "g", (int) (motionx * 8000.0F));
		setFieldI(ret, "h", (int) (motiony * 8000.0F));
		setFieldI(ret, "i", (int) (motionz * 8000.0F));
		setFieldB(ret, "j", (byte) ((int) (yaw * 256.0F / 360.0F)));
		setFieldB(ret, "k", (byte) ((int) (pitch * 256.0F / 360.0F)));
		setFieldB(ret, "l", (byte) ((int) (headpitch * 256.0F / 360.0F)));
		return ret;
	}
	
	public static Object createMoveEntity(int id, double x, double y, double z, float yaw, float pitch) {
		PacketPlayOutEntityTeleport ret = new PacketPlayOutEntityTeleport();
		setFieldI(ret, "a", id);
		setFieldD(ret, "b", x);
		setFieldD(ret, "c", y);
		setFieldD(ret, "d", z);
		setFieldB(ret, "e", (byte) ((int) (yaw * 256.0F / 360.0F)));
		setFieldB(ret, "f", (byte) ((int) (pitch * 256.0F / 360.0F)));
		setFieldBool(ret, "g", true);
		return ret;
	}
	
	public static Object createEntityData(int id, int slot, int type, Object o) {
		PacketPlayOutEntityMetadata ret = new PacketPlayOutEntityMetadata();
		setFieldI(ret, "a", id);
		ArrayList<Item> itemsList = new ArrayList();
		itemsList.add(new Item(new DataWatcherObject(slot, DataWatcherRegistry.a(type)), o));
		setField(ret, "b", itemsList);
		return ret;
	}
	
	public static String getRandomItem(Random r) {
		return RegistryBlocks.ITEM.a(r).toString();
	}
	
	public static int getRandomBlock(Random r) {
		int i;
		do {
			i = r.nextInt(300);
		}while(Block.REGISTRY_ID.fromId(i) == null);
		return i;
	}
	
	public static Object createEntityDataSlot(int id, int slot, String item) {
		return createEntityData(id, slot, 6, new ItemStack(RegistryBlocks.ITEM.get(MinecraftKey.a(item))));
	}
	
	public static Object createDestoryEntity(int id) {
		return new PacketPlayOutEntityDestroy(id);
	}
	
	public static Object createEntityVelocity(int id, float x, float y, float z) {
		return new PacketPlayOutEntityVelocity(id, new Vec3D(x, y, z));
	}
	
	public static Object createEntityLook(int id, float head) {
		PacketPlayOutEntityHeadRotation ret = new PacketPlayOutEntityHeadRotation();
		setFieldI(ret, "a", id);
		setFieldB(ret, "b", (byte) ((int) (head * 256.0F / 360.0F)));
		return ret;
	}
	
	public static Object createWorldBorder(double diameter) {
		PacketPlayOutWorldBorder ret = new PacketPlayOutWorldBorder();
		setField(ret, "a", EnumWorldBorderAction.SET_SIZE);
		setFieldD(ret, "e", diameter);
		return ret;
	}
	
	public static Object createWorldBorderWarn(int diameter) {
		PacketPlayOutWorldBorder ret = new PacketPlayOutWorldBorder();
		setField(ret, "a", EnumWorldBorderAction.SET_WARNING_BLOCKS);
		setFieldI(ret, "i", diameter);
		return ret;
	}
	
	public static Object createCamera(int id) {
		PacketPlayOutCamera ret = new PacketPlayOutCamera();
		setFieldI(ret, "a", id);
		return ret;
	}

}
