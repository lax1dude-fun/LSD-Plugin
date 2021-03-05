package net.lax1dude.lsd_plugin;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import net.md_5.bungee.api.ChatColor;

public class TiHKALMapHooks implements Listener {
	
	public static ItemStack getMapItem() {
		ItemStack tihkal = new ItemStack(Material.FILLED_MAP);
		
		MapMeta tihkalMeta = (MapMeta)tihkal.getItemMeta();
		tihkalMeta.setCustomModelData(666);
		tihkalMeta.setDisplayName(ChatColor.RESET + "Tryptamines I Have Known and Loved");
		tihkal.setItemMeta(tihkalMeta);
		
		return tihkal;
	}
	
	private static void setMapView(ItemStack item) {
    	if(item != null && item.hasItemMeta() && item.getItemMeta() instanceof MapMeta) {
    		MapMeta m = (MapMeta) item.getItemMeta();
    		if(m.getCustomModelData() == 666 && !(m.getMapView() != null && m.getMapView().getRenderers().size() > 0 && (m.getMapView().getRenderers().get(0) instanceof TiHKALMapRenderer))) {
    			MapView mm = m.getMapView();
    			m.getMapView().removeRenderer(m.getMapView().getRenderers().get(0));
    			m.getMapView().addRenderer(new TiHKALMapRenderer());
    			m.setMapView(mm);
    			item.setItemMeta(m);
    		}
    	}
	}
    
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof ItemFrame) {
                setMapView(((ItemFrame) entity).getItem());
            }
        }
    }

	@EventHandler
    public void onPlayerInv(PlayerItemHeldEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
        setMapView(item);
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof HumanEntity)) {
            return;
        }
        setMapView(event.getItem().getItemStack());
    }

    @EventHandler
    public void onPlayerInventoryPlace(InventoryClickEvent event) {
        switch (event.getAction()) {
            case PLACE_ALL:
            case PLACE_ONE:
            case PLACE_SOME:
            case SWAP_WITH_CURSOR:
            	setMapView(event.getCursor());
                break;
            default:

        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	ItemStack i = event.getItem();
    	if(i != null && i.hasItemMeta() && (i.getItemMeta() instanceof MapMeta)) {
    		MapMeta mapMeta = (MapMeta) i.getItemMeta();
    		if(mapMeta.getMapView() != null && mapMeta.getMapView().getRenderers().size() > 0 && (mapMeta.getMapView().getRenderers().get(0) instanceof TiHKALMapRenderer)) {
    			Action a = event.getAction();
    			if(event.getPlayer().isSneaking() && (a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK || a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)) {
    				((TiHKALMapRenderer)mapMeta.getMapView().getRenderers().get(0)).indexPage();
    				event.setCancelled(true);
    			}else if(a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK) {
    				((TiHKALMapRenderer)mapMeta.getMapView().getRenderers().get(0)).previousPage();
    				event.setCancelled(true);
    			}else if(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
    				((TiHKALMapRenderer)mapMeta.getMapView().getRenderers().get(0)).nextPage();
    				event.setCancelled(true);
    			}
    		}
    	}
    	i = event.getPlayer().getInventory().getItemInOffHand();
    	if(i != null && i.hasItemMeta() && (i.getItemMeta() instanceof MapMeta)) {
    		MapMeta mapMeta = (MapMeta) i.getItemMeta();
    		if(mapMeta.getMapView() != null && mapMeta.getMapView().getRenderers().size() > 0 && (mapMeta.getMapView().getRenderers().get(0) instanceof TiHKALMapRenderer)) {
    			Action a = event.getAction();
    			if(event.getPlayer().isSneaking() && (a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK)) {
    				((TiHKALMapRenderer)mapMeta.getMapView().getRenderers().get(0)).indexPage();
    				event.setCancelled(true);
    			}else if(a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK) {
    				((TiHKALMapRenderer)mapMeta.getMapView().getRenderers().get(0)).previousPage();
    				event.setCancelled(true);
    			}
    		}
    	}
    }
    
}
