package org.mcmega.Elsafy.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mcmega.Elsafy.Elsafy;
import org.mcmega.Elsafy.Objects.Elsa;

public class BridgeListener implements Listener {
	
	Elsafy plugin = Elsafy.getInstance();
	
	 @EventHandler
	 public void onPlayerMove(PlayerMoveEvent event) {
		 if (!plugin.isElsa(event.getPlayer().getName())){
			 return;
		 }
		 
		 Player player = event.getPlayer();
		 Elsa elsa = plugin.getElsaObject(player.getName());
		 
		 if (!elsa.isBridgeActive){
			 return;
		 }
		 
		 Location from = event.getFrom();
		 Location to = event.getTo();
		 
		 /*//Player Jumped
		 if (from.getY() < to.getY()){
			 if ((to.getY() - from.getY()) > 0.25){
				 if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.STEP){
					 elsa.getBridgeBuilder().setNextBridgeElevation(BlockFace.UP); 
				 }
			 }
			 return;
		 }*/
		 
		 double distance = from.distance(to);
		 
		 if (distance < 0.01){
			 return;
		 }
		 
		 elsa.getBridgeBuilder().callIceBridgeCheck(player.getLocation());
	 }
	 
	 @EventHandler
	 public void onItemDrop(PlayerDropItemEvent event) {
		 if (!plugin.isElsa(event.getPlayer().getName())){
			 return;
		 }
		 
		 Player player = event.getPlayer();
		 Elsa elsa = plugin.getElsaObject(player.getName());
		 
		 if (!elsa.isBridgeActive){
			 return;
		 }
		 
		 event.setCancelled(true);
	 }
	 
	 /*@EventHandler
	 public void onShift(PlayerToggleSneakEvent event) {
		 if (!plugin.isElsa(event.getPlayer().getName())){
			 return;
		 }
		 
		 Player player = event.getPlayer();
		 Elsa elsa = plugin.getElsaObject(player.getName());
		 
		 if (!elsa.isBridgeActive){
			 return;
		 }
		 
		 elsa.getBridgeBuilder().setNextBridgeElevation(BlockFace.DOWN);
	 }*/

}