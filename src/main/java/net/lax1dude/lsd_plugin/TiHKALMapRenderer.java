package net.lax1dude.lsd_plugin;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class TiHKALMapRenderer extends MapRenderer {

	private int pageNumber = 0;
	private int prevPageNumber = -1;
	
	private static final int maxPages = 5;
	
	private static final BufferedImage[] pageCache = new BufferedImage[maxPages];
	
	public void nextPage() {
		++pageNumber;
		if(pageNumber >= maxPages) {
			pageNumber = maxPages - 1;
		}
	}
	
	public void previousPage() {
		--pageNumber;
		if(pageNumber < 0) {
			pageNumber = 0;
		}
	}
	
	private static void loadPage(int p) {
		if(pageCache[p] == null) {
			try {
				InputStream i = PluginMain.instance.getResource("/assets/lsdplugin/tihkal/page"+p+".png");
				if(i == null) {
					i = TiHKALMapRenderer.class.getResourceAsStream("/assets/lsdplugin/tihkal/page"+p+".png");
				}
				pageCache[p] = ImageIO.read(i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void render(MapView arg0, MapCanvas arg1, Player arg2) {
		if(prevPageNumber != pageNumber) {
			loadPage(pageNumber);
			if(pageCache[pageNumber] != null) {
				arg1.drawImage(0, 0, pageCache[pageNumber]);
			}
			prevPageNumber = pageNumber;
		}
	}

}
