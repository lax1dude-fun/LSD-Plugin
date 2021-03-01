package net.lax1dude.lsd_plugin.bootstrap;

import java.io.File;
import java.lang.reflect.Method;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BootstrapMain extends JavaPlugin {
	
	public void onLoad() {
		try {
			File output = new File("./plugins/LSD-Plugin/transformed.jar");
			
			byte[] dataFile = IOUtils.toByteArray(BootstrapMain.class.getResource("/transformed.jar"));
			
			String nativeName = "/v1_16_R3/";
			String replaceName = "/"+getCurrentPrefix()+"/";
			
			byte[] nativeBytes = nativeName.getBytes();
			byte[] replaceBytes = replaceName.getBytes();
			replace(dataFile, nativeBytes, replaceBytes);
			
			FileUtils.writeByteArrayToFile(output, dataFile);
			
			Method m = PluginManager.class.getDeclaredMethod("loadPlugin", new Class<?>[] {File.class});
			m.setAccessible(true);
			m.invoke(getServer().getPluginManager(), output);
			
		}catch(Exception e) {
			e.printStackTrace();
			getLogger().warning("could not bootstrap LSD-Plugin!");
		}
	}

	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	private String getCurrentPrefix() {
		for(int i = 13; i <= 32; ++i) {
			for(int j = 1; j <= 5; ++j) {
				try {
					if(Class.forName("net.minecraft.server.v1_"+i+"_R"+j+".MinecraftServer") != null) {
						getLogger().info("[LSD-Plugin] Current server package path: net.minecraft.server.v1_"+i+"_R"+j+".MinecraftServer");
						return "v1_"+i+"_R"+j;
					}
				}catch(Throwable t) {
					continue;
				}
			}
		}
		return null;
	}
	
	private static void replace(byte[] data, byte[] find, byte[] replace) {
		search: for(int i = 0; i < data.length - find.length; ++i) {
			for(int j = 0; j < find.length; ++j) {
				if(data[i + j] != find[j]) continue search;
			}
			for(int j = 0; j < replace.length; ++j) {
				data[i + j] = replace[j];
			}
		}
	}
}
