package net.lax1dude.lsd_plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;

public class PackageConstructorsTransformer {

	public static void extractPatchAndLoad(ClassLoader cs) {
		try {
			byte[] classFile1 = IOUtils.toByteArray(PackageConstructorsTransformer.class.getResource("/net/lax1dude/lsd_plugin/tripping/PacketConstructorz.class"));
			byte[] classFile2 = IOUtils.toByteArray(PackageConstructorsTransformer.class.getResource("/net/lax1dude/lsd_plugin/tripping/PacketConstructorz$1.class"));

			String nativeName = "/v1_16_R3/";
			String replaceName = "/"+getCurrentPrefix()+"/";

			byte[] nativeBytes = nativeName.getBytes();
			byte[] replaceBytes = replaceName.getBytes();
			replace(classFile1, nativeBytes, replaceBytes);
			replace(classFile2, nativeBytes, replaceBytes);
			
			nativeName = "PacketConstructorz";
			replaceName = "PacketConstructors";

			nativeBytes = nativeName.getBytes();
			replaceBytes = replaceName.getBytes();
			replace(classFile1, nativeBytes, replaceBytes);
			replace(classFile2, nativeBytes, replaceBytes);
			
			File transformed = new File("./plugins/LSD-Plugin/transformed.jar");
			transformed.deleteOnExit();
			
			FileOutputStream transformedClasses = new FileOutputStream(transformed);
			ZipOutputStream zipOut = new ZipOutputStream(transformedClasses);

			ZipEntry classFile1Out = new ZipEntry("net/lax1dude/lsd_plugin/tripping/PacketConstructors.class");
			ZipEntry classFile2Out = new ZipEntry("net/lax1dude/lsd_plugin/tripping/PacketConstructors$1.class");
			zipOut.putNextEntry(classFile1Out);
			zipOut.write(classFile1);
			zipOut.putNextEntry(classFile2Out);
			zipOut.write(classFile2);
			zipOut.close();
			
			transformedClasses.close();
/*
			URL[] urls = { new URL("jar:" + transformed.toURI().toURL().toString() + "!/") };
			URLClassLoader cl = URLClassLoader.newInstance(urls);
			
			Class c1 = cl.loadClass("net.lax1dude.lsd_plugin.tripping.PacketConstructors");
			Constructor cs1 = c1.getDeclaredConstructors()[0];
			cs1.setAccessible(true);
			cs1.newInstance();
			
			Class c2 = cl.loadClass("net.lax1dude.lsd_plugin.tripping.PacketConstructors$1");
			Constructor cs2 = c1.getDeclaredConstructors()[0];
			cs2.setAccessible(true);
			cs2.newInstance();
*/
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	private static String getCurrentPrefix() {
		for(int i = 13; i <= 32; ++i) {
			for(int j = 1; j <= 5; ++j) {
				try {
					if(Class.forName("net.minecraft.server.v1_"+i+"_R"+j+".MinecraftServer") != null) {
						System.out.println("[LSD-Plugin] Current server package path: net.minecraft.server.v1_"+i+"_R"+j+".MinecraftServer");
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
