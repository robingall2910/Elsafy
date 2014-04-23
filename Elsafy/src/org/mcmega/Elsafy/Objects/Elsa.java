package org.mcmega.Elsafy.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.mcmega.Elsafy.Elsafy;
import org.mcmega.Elsafy.Util;
import org.mcmega.Elsafy.Castle.CastleBuilder;

public class Elsa {
	
	private String pName;
	
	//Ice Interact
	private HashMap<Location, HashSet<BlockState>> changedBlocks = new HashMap<Location, HashSet<BlockState>>();
	private BukkitTask iceRemovalTask = null;
	
	//Walk on Ice
	private HashSet<Location> locationsToIce = new HashSet<Location>();
	private BukkitTask iceSpreadTask = null;
	
	//Ice Shooting
	private long lastLeftClick = 0;
	
	//Snow Swirling
	private SnowSwirlingTask snowTask;
	
	//Castle Building
	private long lastDoubleClickSpace = 0;
	private BukkitTask buildTask;
	
	//Snowman Building
	private long lastRightClick = 0;
	
	
	//Message Cooldown
	private long lastWaterFreezeMessage = 0;
	private long lastInteractFreezeMessage = 0;
	private long lastIceBlastMessage = 0;
	private long lastSnowmanMessage = 0;
	
	//Powers Cooldown
	private long lastIceBlast = 0;
	private long lastCastleCreate = 0;
	private long lastSnowmanBuild = 0;
	
	//Check Flying/Offline Task
	private BukkitTask checkTask;
	
	public Elsa(final String pName){
		this.pName = pName;
		
		if (Elsafy.getInstance().getConfigManager().snowSwirlingEnabled){
			snowTask = new SnowSwirlingTask(this);
		}
		
		checkTask = Bukkit.getScheduler().runTaskTimer(Elsafy.getInstance(), new Runnable(){

			@Override
			public void run() {
				Player player = Bukkit.getPlayer(pName);
				if (player != null){
					player.setAllowFlight(true);
				}else{
					Elsafy.getInstance().removeElsa(pName);
					checkTask.cancel();
				}
				
			}
		}, 300, 300);
	}
	
	public String getElsaName(){
		return pName;
	}
	
	public void setCastleTask(BukkitTask task){
		this.buildTask = task;
	}
	
	public void cancelCastleTask(){
		if (buildTask != null){
			buildTask.cancel();
			buildTask = null;
		}
	}
	
	public void callRightClick(){
		if (!Elsafy.getInstance().getConfigManager().buildSnowmanEnabled){
			return;
		}
		
		if (lastRightClick >= System.currentTimeMillis() - 175){
			
			if (lastSnowmanBuild >= System.currentTimeMillis() - Elsafy.getInstance().getConfigManager().buildSnowmanCooldown){
				return;
			}
			
			Player player = Bukkit.getPlayer(pName);
			if (player != null){
				buildSnowman(player);
				lastSnowmanBuild = System.currentTimeMillis();
			}
		}
		lastRightClick = System.currentTimeMillis();
	}
	
	public void callDoubleClickSpace(){
		if (!Elsafy.getInstance().getConfigManager().createCastleEnabled){
			return;
		}
		
		lastDoubleClickSpace = System.currentTimeMillis();
	}
	
	public void callClickShift(){
		if (!Elsafy.getInstance().getConfigManager().createCastleEnabled){
			return;
		}
		
		if (lastDoubleClickSpace >= System.currentTimeMillis() - 200){
			
			if (lastCastleCreate >= System.currentTimeMillis() - (Elsafy.getInstance().getConfigManager().createCastleCooldown * 1000)){
				return;
			}
			
			Player player = Bukkit.getPlayer(pName);
			if (buildTask != null){
				if (player != null){
					player.sendMessage(ChatColor.BLUE + "You power limits you to a single castle creation at once!");
				}
				return;
			}
			
			if (player != null){
				player.setVelocity(new Vector(0, -10, 0));
			}
			
			CastleBuilder.LoadandPaste(this);
			lastCastleCreate = System.currentTimeMillis();
		}
	}
	
	public void callLeftClick(){
		if (!Elsafy.getInstance().getConfigManager().iceBlastEnabled){
			return;
		}
		
		if (lastLeftClick >= System.currentTimeMillis() - 175){
			if (lastIceBlast >= System.currentTimeMillis() - Elsafy.getInstance().getConfigManager().iceBlastCooldown){
				return;
			}
			launchIceBlast();
			lastIceBlast = System.currentTimeMillis();
			return;
		}
		lastLeftClick = System.currentTimeMillis();
	}
	
	private void launchIceBlast(){
		new IceArrow(this);
		if (lastIceBlastMessage <= System.currentTimeMillis() - 60000){
			Player player = Bukkit.getPlayer(pName);
			if (player != null){
				player.sendMessage(ChatColor.DARK_AQUA + "YOU MONSTER!!! Beware your ice magic!");
			}
			lastIceBlastMessage = System.currentTimeMillis();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void buildSnowman(Player player){
		Block targetBlock = player.getTargetBlock(null, 200);
		if (targetBlock == null){
			return;
		}
		Location targetLoc = targetBlock.getLocation();
		targetLoc.add(new Vector(0,1,0));
		for (BlockFace face : BlockFace.values()){
			Location effectLoc = targetLoc.getBlock().getRelative(face).getLocation();
			if (Elsafy.getInstance().isSpigot()){
				effectLoc.getWorld().spigot().playEffect(effectLoc, Effect.CLOUD, 0, 0, 0, 0, 0, 1, 15, 150);
			}else{
				effectLoc.getWorld().playEffect(effectLoc, Effect.SMOKE, 0);
			}
			
		}
		player.getWorld().spawnEntity(targetLoc, EntityType.SNOWMAN);
		
		if (lastSnowmanMessage <= System.currentTimeMillis() - 60000){
			if (player != null){
				player.sendMessage(ChatColor.BLUE + "Really? Your sister waits 13 years to build a snowman and you build one by yourself?!");
			}
			lastSnowmanMessage = System.currentTimeMillis();
		}
	}
	
	public void randomIceOnInteract(Location iceLocation, boolean overrideChance){
		Random randomGenerator = new Random();
		int iceChance = randomGenerator.nextInt(100);
		
		if (iceChance < Elsafy.getInstance().getConfigManager().freezeOnInteractProbability || overrideChance){
			HashSet<Location> checkLocations = new HashSet<Location>();
			HashSet<Location> icedLocations = new HashSet<Location>();
			int iceSpread = randomGenerator.nextInt(Elsafy.getInstance().getConfigManager().freezeOnInteractRadius + 1);
			for (int i=0; i <= iceSpread; i++){
				if (i == 0){
					changedBlocks.put(iceLocation, new HashSet<BlockState>());
					changedBlocks.get(iceLocation).add(iceLocation.getBlock().getState());
					iceLocation.getBlock().setType(Material.ICE);
					for (BlockFace face : BlockFace.values()){
						if (face == BlockFace.SELF){
							continue;
						}
						
						Location newLoc = iceLocation.getBlock().getRelative(face).getLocation();
						if (checkLocations.contains(newLoc)){
							continue;
						}
						
						checkLocations.add(newLoc);
						icedLocations.add(iceLocation);
					}
				}else{
					@SuppressWarnings("unchecked")
					HashSet<Location> checkLocationCopy = (HashSet<Location>) checkLocations.clone();
					for (Location testLocation : checkLocationCopy){
						if (testLocation.getBlock().getType() == Material.AIR){
							checkLocations.remove(testLocation);
							continue;
						}else{
							changedBlocks.get(iceLocation).add(testLocation.getBlock().getState());
							testLocation.getBlock().setType(Material.ICE);
							checkLocations.remove(testLocation);
							icedLocations.add(testLocation);
							for (BlockFace face : BlockFace.values()){
								Location newLoc = (testLocation.getBlock().getRelative(face).getLocation());
								if (icedLocations.contains(newLoc)){
									continue;
								}else{
									int newIceChance = randomGenerator.nextInt(10);
									if (newIceChance < 8){
										checkLocations.add(newLoc);
									}
									checkLocations.add(newLoc);
								}
							}
						}
					}
				}
			}
			if (lastInteractFreezeMessage <= System.currentTimeMillis() - 60000 && !overrideChance){
				Player player = Bukkit.getPlayer(pName);
				if (player != null){
					player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "CONTROL IT! " + ChatColor.DARK_AQUA + "Conceal, don't feel!");
				}
				lastInteractFreezeMessage = System.currentTimeMillis();
			}
			startIceRemoval();
		}
	}
	
	public void walkOnIce(Player player){
		Location loc = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
		if (loc.getBlock().getType() == Material.WATER || loc.getBlock().getType() == Material.STATIONARY_WATER){
			loc.getBlock().setType(Material.PACKED_ICE);
			//player.getWorld().spigot().playEffect(loc, Effect.SNOWBALL_BREAK, 0, 0, 0, 0, 0, 1, 6, 60);
			loc.getBlock().getRelative(Util.getCardinalDirection(player)).setType(Material.PACKED_ICE);
		}
		for (BlockFace face : BlockFace.values()){
			Block testBlock = loc.getBlock().getRelative(face);
			
			if (testBlock.getType() != Material.WATER && testBlock.getType() != Material.STATIONARY_WATER && testBlock.getType() != Material.PACKED_ICE){
				continue;
			}else{
				testBlock.setType(Material.PACKED_ICE);
				locationsToIce.add(testBlock.getLocation());
			}
		}
		if (lastWaterFreezeMessage <= System.currentTimeMillis() - 30000){
			player.sendMessage(ChatColor.BLUE + "The... the fjord... it's freezing!");
			lastWaterFreezeMessage = System.currentTimeMillis();
		}
		startIceSpread();
	}
	
	private void startIceSpread(){
		if (iceSpreadTask != null){
			return;
		}
		
		iceSpreadTask = Bukkit.getScheduler().runTaskTimer(Elsafy.getInstance(), new Runnable(){

			@Override
			public void run() {
				if (locationsToIce.size() == 0){
					cancelIceSpreadTask();
					return;
				}
				
				//Get amount of ice blocks to freeze
				int amount = Elsafy.getInstance().getConfigManager().freezeWaterRate;
				
				//Get random ice block to freeze
				Random generator = new Random();
				Object[] values = locationsToIce.toArray();
				
				List<Location> freezeSession = new ArrayList<Location>();
				for (int i=0; i < amount; i++){
					freezeSession.add((Location) values[generator.nextInt(values.length)]);
				}
				
				for (Location loc : freezeSession){
					loc.getBlock().setType(Material.PACKED_ICE);
					locationsToIce.remove(loc);
					for (BlockFace face : BlockFace.values()){
						if (face == BlockFace.DOWN){
							continue;
						}
						
						Block testBlock = loc.getBlock().getRelative(face);
						
						if (testBlock.getType() != Material.WATER || testBlock.getType() == Material.PACKED_ICE){
							continue;
						}else{
							locationsToIce.add(testBlock.getLocation());
						}
					}
				}
				
			}
			
		}, 5, 1);
	}
	
	private void startIceRemoval(){
		if (iceRemovalTask != null){
			return;
		}
		iceRemovalTask = Bukkit.getScheduler().runTaskTimer(Elsafy.getInstance(), new Runnable(){

			@Override
			public void run() {
				Set<Location> iceLocationsCloned = new HashSet<Location>();
				iceLocationsCloned.addAll(changedBlocks.keySet());
				
				for (Location iceLocation : iceLocationsCloned){
					HashSet<BlockState> blocks = changedBlocks.get(iceLocation);
					if (blocks.size() == 0){
						changedBlocks.remove(iceLocation);
						cancelIceRemovalTask();
						continue;
					}else{
						BlockState maxDistanceBlock = null;
						double maxDistance = -1;
						for (BlockState state : blocks){
							double distance = state.getLocation().distance(iceLocation);
							if (distance > maxDistance){
								maxDistanceBlock = state;
								maxDistance = distance;
							}
						}
						if (maxDistanceBlock != null){
							maxDistanceBlock.update(true);
							blocks.remove(maxDistanceBlock);
						}
						
					}
				}
				
			}
			
		}, 200, 10);
	}
	
	private void cancelIceRemovalTask(){
		if (iceRemovalTask != null){
			iceRemovalTask.cancel();
			iceRemovalTask = null;
		}
	}
	
	private void cancelIceSpreadTask(){
		if (iceSpreadTask != null){
			iceSpreadTask.cancel();
			iceSpreadTask = null;
		}
	}
	
	private void cancelSnowSwirlingTask(){
		if (snowTask != null){
			snowTask.cancel();
			snowTask = null;
		}
	}
	
	public void endElsa(){
		cancelIceRemovalTask();
		cancelIceSpreadTask();
		cancelSnowSwirlingTask();
		for (Location loc : changedBlocks.keySet()){
			for (BlockState state : changedBlocks.get(loc)){
				state.update(true);
			}
		}
	}

}
