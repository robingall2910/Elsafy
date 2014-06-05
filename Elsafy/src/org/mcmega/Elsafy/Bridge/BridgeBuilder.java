package org.mcmega.Elsafy.Bridge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.mcmega.Elsafy.Elsafy;
import org.mcmega.Elsafy.Util;
import org.mcmega.Elsafy.Objects.Elsa;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;

public class BridgeBuilder extends BukkitRunnable {
	
	private Elsa elsa;
	
	//Next Bridge to be pasted will have this elevation. This value is adjusted by jumping or crouching
	private BlockFace nextBridgeElevation;
	
	//Direction bridge is moving. Always the same for a BridgeBuilder object
	private BlockFace currentBridgeDirection;
	
	//Locations that need to be turned to ice when in range
	private HashSet<Location> incompleteLocations = new HashSet<Location>();
	
	//This can either be an x or z value. When the player reaches this value, a new bridge piece will be added
	private int nextRequiredPlaceLocation;
	
	//Opposite type (x,z) of Required Place Location. This value always stays the same
	private int staticPlaceLocation;
	
	//This value represents the last Y value a bridge was placed at
	private int lastYValue;
	
	public BridgeBuilder(Elsa elsa){
		this.elsa = elsa;
		Player player = Bukkit.getPlayer(elsa.getElsaName());
		if (player != null){
			Location pLoc = player.getLocation();
			lastYValue = pLoc.getBlockY();
			currentBridgeDirection = Util.getExactDirection(player);
			nextBridgeElevation = BlockFace.SELF;
			
			if (currentBridgeDirection == BlockFace.NORTH){
				//Z Decreasing
				staticPlaceLocation = pLoc.getBlockX();
				nextRequiredPlaceLocation = pLoc.getBlockZ();
			}else if (currentBridgeDirection == BlockFace.EAST){
				//X Increasing
				staticPlaceLocation = pLoc.getBlockZ();
				nextRequiredPlaceLocation = pLoc.getBlockX();
			}else if (currentBridgeDirection == BlockFace.SOUTH){
				//Z Increasing
				staticPlaceLocation = pLoc.getBlockX();
				nextRequiredPlaceLocation = pLoc.getBlockZ();
			}else if (currentBridgeDirection == BlockFace.WEST){
				//X Decreasing
				staticPlaceLocation = pLoc.getBlockZ();
				nextRequiredPlaceLocation = pLoc.getBlockX();
			}
			
			setBridge(player.getLocation(), false);
			
			List<ItemStack> itemsToMove = new ArrayList<ItemStack>();
			itemsToMove.add(player.getInventory().getItem(0));
			itemsToMove.add(player.getInventory().getItem(1));
			itemsToMove.add(player.getInventory().getItem(2));
			
			//Up Item
			ItemStack upItem = new ItemStack(Material.PORTAL);
			ItemMeta upMeta = upItem.getItemMeta();
			upMeta.setDisplayName(ChatColor.DARK_GREEN + "UP");
			List<String> upLore = new ArrayList<String>();
			upLore.add(ChatColor.GREEN + "Make the Bridge Move Upward");
			upMeta.setLore(upLore);
			upItem.setItemMeta(upMeta);
			
			//Down Item
			ItemStack downItem = new ItemStack(Material.PORTAL);
			ItemMeta downMeta = upItem.getItemMeta();
			downMeta.setDisplayName(ChatColor.DARK_GREEN + "DOWN");
			List<String> downLore = new ArrayList<String>();
			downLore.add(ChatColor.GREEN + "Make the Bridge Move Downward");
			downMeta.setLore(downLore);
			downItem.setItemMeta(downMeta);
			
			player.getInventory().setItem(0, upItem);
			player.getInventory().setItem(2, downItem);
			player.getInventory().setHeldItemSlot(1);
			
			for (ItemStack stack : itemsToMove){
				if (stack == null){
					continue;
				}
				player.getInventory().addItem(stack);
			}
			
			this.runTaskTimer(Elsafy.getInstance(), 1, 1);
		}
	}
	
	@Override
	public void run() {
		Player player = Bukkit.getPlayer(elsa.getElsaName());
		if (player == null){
			this.cancel();
			return;
		}
		int slot = player.getInventory().getHeldItemSlot();
		
		if (slot == 0){
			elsa.getBridgeBuilder().setNextBridgeElevation(BlockFace.UP);
			player.getInventory().setHeldItemSlot(1);
			return;
		}
		if (slot == 2){
			elsa.getBridgeBuilder().setNextBridgeElevation(BlockFace.DOWN);
			player.getInventory().setHeldItemSlot(1);
			return;
		}
		
		if (slot != 0 && slot != 1 && slot != 2){
			player.getInventory().setHeldItemSlot(1);
		}
	}
	
	public void setNextBridgeElevation(BlockFace elevation){
		nextBridgeElevation = elevation;
	}
	
	public void callIceBridgeCheck(Location pLoc){
		if (currentBridgeDirection == BlockFace.NORTH){
			//Z Decreasing
			if (pLoc.getBlockZ() < nextRequiredPlaceLocation){
				setBridge(new Location(pLoc.getWorld(), staticPlaceLocation, lastYValue, nextRequiredPlaceLocation - 2), false);
			}
		}else if (currentBridgeDirection == BlockFace.EAST){
			//X Increasing
			if (pLoc.getBlockX() > nextRequiredPlaceLocation){
				setBridge(new Location(pLoc.getWorld(), nextRequiredPlaceLocation + 2, lastYValue, staticPlaceLocation), false);
			}
		}else if (currentBridgeDirection == BlockFace.SOUTH){
			//Z Increasing
			if (pLoc.getBlockZ() > nextRequiredPlaceLocation){
				setBridge(new Location(pLoc.getWorld(), staticPlaceLocation, lastYValue, nextRequiredPlaceLocation + 2), false);
			}
		}else if (currentBridgeDirection == BlockFace.WEST){
			//X Decreasing
			if (pLoc.getBlockX() < nextRequiredPlaceLocation){
				setBridge(new Location(pLoc.getWorld(), nextRequiredPlaceLocation - 2, lastYValue, staticPlaceLocation), false);
			}
		}
		checkForCompletion(pLoc);
	}
	
	@SuppressWarnings("deprecation")
	private void setBridge(Location location, boolean ignoreElevation){
		if (!ignoreElevation){
			//Adjust Y Position and Place flat bridge piece
			if (nextBridgeElevation == BlockFace.UP){
				lastYValue = lastYValue + 2;
				Location newLoc = location.clone();
				newLoc.add(new Vector(currentBridgeDirection.getModX() * 3, 2, currentBridgeDirection.getModZ() * 3));
				setBridge(newLoc, true);
			}else if (nextBridgeElevation == BlockFace.DOWN){
				lastYValue = lastYValue - 2;
				Location newLoc = location.clone();
				newLoc.add(new Vector(currentBridgeDirection.getModX() * 2, -2, currentBridgeDirection.getModZ() * 2));
				setBridge(newLoc, true);
			}
		}
		
		CuboidClipboard cc;
		if (ignoreElevation){
			//System.out.println("Elevation Run");
			cc = Elsafy.getInstance().getBridgeManager().getUncompletePiece(null);
		}else{
			//System.out.println("Normal Run");
			cc = Elsafy.getInstance().getBridgeManager().getUncompletePiece(nextBridgeElevation);
		}
		
		if (currentBridgeDirection == BlockFace.EAST){
			cc.rotate2D(90);
		}else if (currentBridgeDirection == BlockFace.SOUTH){
			cc.rotate2D(180);
		}else if (currentBridgeDirection == BlockFace.WEST){
			cc.rotate2D(270);
		}
		
		com.sk89q.worldedit.Vector vector = new com.sk89q.worldedit.Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		
		//Fix Down
		if (nextBridgeElevation == BlockFace.DOWN && !ignoreElevation){
			//System.out.println("Running Down Fix");
			//System.out.println("Before Down Fix:  x:" + vector.getBlockX() + " y:" + vector.getBlockY() + " z:" + vector.getBlockZ());
			com.sk89q.worldedit.Vector newVector = vector.add(currentBridgeDirection.getModX() * 2, -2, currentBridgeDirection.getModZ() * 2);
			vector = newVector;
			//System.out.println("After Down Fix:  x:" + vector.getBlockX() + " y:" + vector.getBlockY() + " z:" + vector.getBlockZ());
		}
		vector.add(cc.getOffset());
		try {
			cc.paste(new EditSession(new BukkitWorld(location.getWorld()), -1), vector, false);
		} catch (MaxChangedBlocksException e) {
			//Will never happen
		}
		
		//Adjust next required position by 3 blocks
		if (currentBridgeDirection == BlockFace.NORTH || currentBridgeDirection == BlockFace.WEST){
			//Z Decreasing
			nextRequiredPlaceLocation = nextRequiredPlaceLocation - 3;
		}else if (currentBridgeDirection == BlockFace.EAST || currentBridgeDirection == BlockFace.SOUTH){
			//X Increasing
			nextRequiredPlaceLocation = nextRequiredPlaceLocation + 3;
		}
		
		
		for (int x=0; x < cc.getWidth(); x++){
			for (int y=0; y < cc.getHeight(); y++){
				for (int z=0; z < cc.getLength(); z++){
					com.sk89q.worldedit.Vector snowVector = new com.sk89q.worldedit.Vector(x, y, z);
					if (cc.getBlock(snowVector).getType() != Material.SNOW_BLOCK.getId()){
						continue;
					}
					Location snowLoc = BukkitUtil.toLocation(location.getWorld(), snowVector.add(vector).add(cc.getOffset()));
					incompleteLocations.add(snowLoc);
				}
			}
		}
		
		if (!ignoreElevation){
			nextBridgeElevation = BlockFace.SELF;
		}
	}
	
	private void checkForCompletion(Location pLoc){
		HashSet<Location> toRemove = new HashSet<Location>();
		for (Location location : incompleteLocations){
			if (pLoc.distance(location) < 4){
				location.getBlock().setType(Material.ICE);
				if (Elsafy.getInstance().getConfigManager().bridgeParticles){
					if (Elsafy.getInstance().isSpigot()){
						location.getWorld().spigot().playEffect(location, Effect.CLOUD, 0, 0, 0, 0, 0, 1, 10, 150);
					}else{
						location.getWorld().playEffect(location, Effect.SMOKE, 0);
					}
				}
				toRemove.add(location);
			}
		}
		incompleteLocations.removeAll(toRemove);
	}
	
	public void endBridge(){
		this.cancel();
		Player player = Bukkit.getPlayer(elsa.getElsaName());
		if (player != null){
			player.getInventory().remove(Material.PORTAL);
		}
	}

}
