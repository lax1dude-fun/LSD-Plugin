package net.lax1dude.lsd_plugin.tripping;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R3.MobEffectList;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutAbilities;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_16_R3.PacketPlayOutRemoveEntityEffect;
import net.minecraft.server.v1_16_R3.PacketPlayOutUpdateHealth;

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

}
