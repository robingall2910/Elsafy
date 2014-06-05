package org.mcmega.Elsafy.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffectType;
import org.mcmega.Elsafy.Elsafy;
import org.mcmega.Elsafy.Objects.Elsa;

public class ElsaListener implements Listener {
	
	Elsafy plugin = Elsafy.getInstance();
	
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	if (!plugin.isElsa(event.getPlayer().getName())){
    		return;
    	}
    	
    	Player player = event.getPlayer();
    	Block block = event.getClickedBlock();
    	
    	Elsa elsa = plugin.getElsaObject(player.getName());
    	
    	if (block == null){
    		if (player.isSneaking()){
        		if (event.getAction() == Action.LEFT_CLICK_AIR){
        			elsa.callLeftClickSneaking();
        		}else if (event.getAction() == Action.RIGHT_CLICK_AIR){
        			elsa.callRightClickSneaking();
        		}
    		}else if (player.getLocation().getPitch() < -89){
    			if (event.getAction() == Action.LEFT_CLICK_AIR){
        			elsa.callLeftClickLookingUp();
        			return;
    			}
    		}else{
        		if (event.getAction() == Action.LEFT_CLICK_AIR){
        			elsa.callLeftClick();
        		}else if (event.getAction() == Action.RIGHT_CLICK_AIR){
        			elsa.callRightClick();
        		}
    		}
    		return;
    	}
    	
    	if (block.getLocation().distance(player.getLocation()) > 40){
    		return;
    	}
    	
    	elsa.randomIceOnInteract(block.getLocation(), false);
    	
    	if (event.getAction() == Action.LEFT_CLICK_BLOCK){
    		if (player.isSneaking()){
    			elsa.callLeftClickSneaking();
    		}else{
    			elsa.callLeftClick();
    		}
    	}
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
    	if (event.getPlayer().hasPotionEffect(PotionEffectType.WITHER)){
    		for (Entity e : event.getPlayer().getNearbyEntities(1, 1, 1)){
    			if (e instanceof Player){
    				Player nearbyPlayer = (Player) e;
    				event.getPlayer().removePotionEffect(PotionEffectType.WITHER);
    				event.getPlayer().sendMessage(ChatColor.GOLD + "Your frozen heart has been thawed by " + nearbyPlayer.getName() + "!");
    			}
    		}
    	}
    	
    	if (!plugin.isElsa(event.getPlayer().getName())){
    		return;
    	}
    	
		if (!Elsafy.getInstance().getConfigManager().freezeWaterEnabled){
			return;
		}
    	
    	Player player = event.getPlayer();
    	Elsa elsa = plugin.getElsaObject(player.getName());
    	
    	Material mat = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
    	if (mat == Material.WATER || mat == Material.STATIONARY_WATER){
    		elsa.walkOnIce(player);
    		return;
    	}
    	for (BlockFace face : BlockFace.values()){
    		if (face == BlockFace.DOWN){
    			continue;
    		}
    		
    		Material surrounding = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(face).getType();
    		if (surrounding == Material.WATER || surrounding == Material.STATIONARY_WATER){
        		elsa.walkOnIce(player);
        		return;
    		}
    	}
    }
    
    @EventHandler
    public void onFlightAttempt(final PlayerToggleFlightEvent event) {
    	if (!plugin.isElsa(event.getPlayer().getName())){
    		return;
    	}
    	
    	if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
    	
    	Elsa elsa = plugin.getElsaObject(event.getPlayer().getName());
    	if(event.isFlying()) {
    		event.setCancelled(true);
    		elsa.callDoubleClickSpace();
    		event.getPlayer().setAllowFlight(false);
    		Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){

				@Override
				public void run() {
					if (event.getPlayer() != null){
						event.getPlayer().setAllowFlight(true);
					}
				}
    			
    		}, plugin.getConfigManager().createCastleCooldown / 20);
    	}
    }
    
    @EventHandler
    public void onShift(PlayerToggleSneakEvent event) {
    	if (!plugin.isElsa(event.getPlayer().getName())){
    		return;
    	}
    	
    	Elsa elsa = plugin.getElsaObject(event.getPlayer().getName());
    	elsa.callClickShift();
    }

}
