package net.lax1dude.lsd_plugin.tripping;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

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
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_16_R3.PacketPlayOutRemoveEntityEffect;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_16_R3.PacketPlayOutUpdateHealth;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_16_R3.Particle;
import net.minecraft.server.v1_16_R3.ParticleParam;
import net.minecraft.server.v1_16_R3.RegistryBlocks;
import net.minecraft.server.v1_16_R3.Vec3D;

public class PacketConstructors {
	
	public static void sendPacket(Player p, Packet pkt) {
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(pkt);
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
	
	public static PacketPlayOutUpdateHealth createHealthUpdate(float health, int food, float saturation) {
		return new PacketPlayOutUpdateHealth(health, food, saturation);
	}
	
	public static PacketPlayOutAbilities createPlayerAbilities(boolean invul, boolean isFlying, boolean canFly, boolean canInstantlyBuild, float flyspeed, float fov) {
		PacketPlayOutAbilities abilities = new PacketPlayOutAbilities();
		setFieldBool(abilities, "a", invul);
		setFieldBool(abilities, "b", isFlying);
		setFieldBool(abilities, "c", canFly);
		setFieldBool(abilities, "d", canInstantlyBuild);
		setFieldF(abilities, "e", flyspeed);
		setFieldF(abilities, "f", fov);
		return abilities;
	}
	
	public static PacketPlayOutEntityEffect createEntityEffect(int entity, byte effect, byte amp, int duration, byte flags) {
		PacketPlayOutEntityEffect ret = new PacketPlayOutEntityEffect();
		setFieldI(ret, "a", entity);
		setFieldB(ret, "b", effect);
		setFieldB(ret, "c", amp);
		setFieldI(ret, "d", duration);
		setFieldB(ret, "e", flags);
		return ret;
	}
	
	public static PacketPlayOutRemoveEntityEffect createRemoveEntityEffect(int entity, byte effect) {
		PacketPlayOutRemoveEntityEffect ret = new PacketPlayOutRemoveEntityEffect();
		setFieldI(ret, "a", entity);
		setField(ret, "b", MobEffectList.fromId(effect));
		return ret;
	}
	
	public static PacketPlayOutWorldParticles createParticles(int particleType, boolean distance, double x, double y, double z, float sx, float sy, float sz, float data, int count) {
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
	
	public static PacketPlayOutSpawnEntity createSpawnEntity(int id, UUID uuid, String type, double x, double y, double z, float yaw, float pitch, int data, float motionx, float motiony, float motionz) {
		return new PacketPlayOutSpawnEntity(id, uuid, x, y, z, yaw, pitch, EntityTypes.a(type).get(), data, new Vec3D(motionx, motiony, motionz));
	}
	
	public static PacketPlayOutEntityTeleport createMoveEntity(int id, double x, double y, double z, float yaw, float pitch) {
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
	
	public static PacketPlayOutEntityMetadata createEntityData(int id, int slot, int type, Object o) {
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
	
	public static PacketPlayOutEntityMetadata createEntityDataSlot(int id, int slot, String item) {
		return createEntityData(id, slot, 6, new ItemStack(RegistryBlocks.ITEM.get(MinecraftKey.a(item))));
	}
	
	public static PacketPlayOutEntityDestroy createDestoryEntity(int id) {
		return new PacketPlayOutEntityDestroy(id);
	}
	
	public static PacketPlayOutEntityVelocity createEntityVelocity(int id, float x, float y, float z) {
		return new PacketPlayOutEntityVelocity(id, new Vec3D(x, y, z));
	}

}
