package org.mcmega.Elsafy.Objects;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.mcmega.Elsafy.Elsafy;
import org.mcmega.Elsafy.Util;
import org.mcmega.Elsafy.Bridge.BridgeBuilder;
import org.mcmega.Elsafy.Castle.CastleBuilder;
import org.mcmega.Elsafy.Rollback.ElsaRollback;

public class Elsa {
	
	private String pName;
	
	//Elsa Rollback
	private ElsaRollback elsaRollback;
	
	//Ice Interact
	//private HashMap<Location, HashSet<BlockState>> changedBlocks = new HashMap<Location, HashSet<BlockState>>();
	//private BukkitTask iceRemovalTask = null;
	
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
	
	//Snow Pillars
	private HashMap<Integer, BukkitTask> pillarTasks = new HashMap<Integer, BukkitTask>();
	
	//Ice Bridge
	private BridgeBuilder bridgeBuilder;
	public boolean isBridgeActive = false;
	
	//Ice Spikes
	private long lastRightClickSneaking = 0;
	
	//Message Cooldown
	private long lastWaterFreezeMessage = 0;
	private long lastInteractFreezeMessage = 0;
	private long lastIceBlastMessage = 0;
	private long lastSnowmanMessage = 0;
	private long lastSnowPillarMessage = 0;
	private long lastSnowflakeMessage = 0;
	private long lastBridgeMessage = 0;
	private long lastIceSpikesMessage = 0;
	
	//Powers Cooldown
	private long lastIceBlast = 0;
	private long lastCastleCreate = 0;
	private long lastSnowmanBuild = 0;
	private long lastSnowPillar = 0;
	private long lastSnowflake = 0;
	private long lastIceSpikes = 0;
	
	public Elsa(final String pName){
		this.pName = pName;
		
		if (Elsafy.getInstance().getConfigManager().snowSwirlingEnabled){
			snowTask = new SnowSwirlingTask(this);
		}
		
		elsaRollback = new ElsaRollback(this);
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
	
	public ElsaRollback getElsaRollback(){
		return elsaRollback;
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

	public void callRightClickSneaking(){
		if (!Elsafy.getInstance().getConfigManager().iceSpikesEnabled){
			return;
		}
		
		if (lastRightClickSneaking >= System.currentTimeMillis() - 175){
			
			if (lastIceSpikes >= System.currentTimeMillis() - Elsafy.getInstance().getConfigManager().buildSnowmanCooldown){
				return;
			}
			
			Player player = Bukkit.getPlayer(pName);
			if (player != null){
				callIceSpikes(player);
				lastIceSpikes = System.currentTimeMillis();
			}
		}
		lastRightClickSneaking = System.currentTimeMillis();
	}

	public void callLeftClickSneaking(){
		if (!Elsafy.getInstance().getConfigManager().snowPillarsEnabled){
			return;
		}
		
		if (lastLeftClick >= System.currentTimeMillis() - 175){
			if (lastSnowPillar >= System.currentTimeMillis() - Elsafy.getInstance().getConfigManager().snowPillarsCooldown){
				return;
			}
			launchSnowPillar();
			lastSnowPillar = System.currentTimeMillis();
			return;
		}
		lastLeftClick = System.currentTimeMillis();
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
	
	public void callLeftClickLookingUp(){
		if (!Elsafy.getInstance().getConfigManager().snowflakeEnabled){
			return;
		}
		
		if (lastLeftClick >= System.currentTimeMillis() - 175){
			if (lastSnowflake >= System.currentTimeMillis() - Elsafy.getInstance().getConfigManager().snowflakeCooldown){
				return;
			}
			launchSnowflake();
			lastSnowflake = System.currentTimeMillis();
			return;
		}
		lastLeftClick = System.currentTimeMillis();
	}
	
	private void launchSnowPillar(){
		int misfire = Elsafy.getInstance().getConfigManager().snowPillarsMisfireRate;
		if (misfire > 0){
			Random randomGenerator = new Random();
			int chance = randomGenerator.nextInt(100);
			if (misfire > chance){
				Player player = Bukkit.getPlayer(pName);
				if (Elsafy.getInstance().getConfigManager().snowPillarParticles && player != null){
					if (Elsafy.getInstance().isSpigot()){
						player.getWorld().spigot().playEffect(player.getEyeLocation(), Effect.VILLAGER_THUNDERCLOUD, 0, 0, 0, 0, 0, 1, 7, 150);
						player.getWorld().playEffect(player.getEyeLocation(), Effect.CLICK1, 0);
					}else{
						player.getWorld().playEffect(player.getEyeLocation(), Effect.SMOKE, 0);
						player.getWorld().playEffect(player.getEyeLocation(), Effect.CLICK1, 0);
					}
				}
				return;
			}
		}
		
		new IceBall(this, LaunchType.SNOW_PILLAR);
		if (lastSnowPillarMessage <= System.currentTimeMillis() - 60000){
			Player player = Bukkit.getPlayer(pName);
			if (player != null){
				player.sendMessage(ChatColor.DARK_AQUA + "Catch me Elsa! Catch me!");
			}
			lastSnowPillarMessage = System.currentTimeMillis();
		}
	}
	
	private void launchIceBlast(){
		new IceBall(this, LaunchType.ICE_BLAST);
		if (lastIceBlastMessage <= System.currentTimeMillis() - 60000){
			Player player = Bukkit.getPlayer(pName);
			if (player != null){
				player.sendMessage(ChatColor.DARK_AQUA + "YOU MONSTER!!! Beware your ice magic!");
			}
			lastIceBlastMessage = System.currentTimeMillis();
		}
	}
	
	private void launchSnowflake(){
		new IceBall(this, LaunchType.SNOWFLAKE);
		if (lastSnowflakeMessage <= System.currentTimeMillis() - 60000){
			Player player = Bukkit.getPlayer(pName);
			if (player != null){
				player.sendMessage(ChatColor.DARK_AQUA + "There is beauty in it... but also great danger!");
			}
			lastSnowflakeMessage = System.currentTimeMillis();
		}
	}
	
	private void callIceSpikes(Player player) {
		new IceBall(this, LaunchType.ICE_SPIKES);
		if (lastIceSpikesMessage <= System.currentTimeMillis() - 60000){
			if (player != null){
				player.sendMessage(ChatColor.DARK_AQUA + "YOU MONSTER!!! Beware your ice magic!");
			}
			lastIceSpikesMessage = System.currentTimeMillis();
		}
	}
	
	public void shootIceSpikes(Location loc, float yaw){
		int spikes = 15; //TODO: Spike config
		for (int yawDelta = -60; yawDelta <= 60; yawDelta+=spikes){
			Location targetLoc = loc.clone();
			targetLoc.setYaw(yaw + yawDelta);
			targetLoc.add(targetLoc.getDirection().normalize().multiply(10)); //TODO: Allow different sizes
			targetLoc.setY(targetLoc.getY() + 3);
			
			List<Point2D> points = Util.drawLine(loc.getBlockX(), loc.getBlockZ(), targetLoc.getBlockX(), targetLoc.getBlockZ());
			double yIncrease = (targetLoc.getBlockY() - loc.getBlockY()) / (double)points.size();
			int pointsIced = 0;
			
			final List<Location> locsToFreeze = new ArrayList<Location>();
			for (Point2D point : points){
				int y = (int) (yIncrease * pointsIced);
				locsToFreeze.add(new Location(loc.getWorld(), point.getX(), loc.getBlockY() + y, point.getY()));
				pointsIced++;
			}
			
			new BukkitRunnable(){
				
				@Override
				public void run() {
					if (locsToFreeze.isEmpty()){
						this.cancel();
						return;
					}
					locsToFreeze.get(0).getBlock().setType(Material.ICE);
					locsToFreeze.remove(0);
				}
				
			}.runTaskTimer(Elsafy.getInstance(), 0, 1);
		}
	}
	
	private void buildSnowman(Player player){
		Block targetBlock = player.getTargetBlock(new HashSet<Material>(), 200);
		if (targetBlock == null){
			return;
		}
		Location targetLoc = targetBlock.getLocation();
		Location newTargetLoc = targetLoc.clone().add(new Vector(0,1,0));
		for (BlockFace face : BlockFace.values()){
			Location effectLoc = newTargetLoc.getBlock().getRelative(face).getLocation();
			if (Elsafy.getInstance().isSpigot()){
				effectLoc.getWorld().spigot().playEffect(effectLoc, Effect.CLOUD, 0, 0, 0, 0, 0, 1, 15, 150);
			}else{
				effectLoc.getWorld().playEffect(effectLoc, Effect.SMOKE, 0);
			}
			
		}
		Creature c = (Creature) player.getWorld().spawnEntity(newTargetLoc, EntityType.SNOWMAN);
		c.setCustomName(ChatColor.DARK_AQUA + "Olaf");
		
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
					//changedBlocks.put(iceLocation, new HashSet<BlockState>());
					//Fix for leftover ice
					if (iceLocation.getBlock().getType() != Material.ICE){
						if (Elsafy.getInstance().getConfigManager().rollbackOTFreezeOnInteract){
							elsaRollback.addBlock(iceLocation, iceLocation.getBlock().getState());
						}
						//changedBlocks.get(iceLocation).add(iceLocation.getBlock().getState());
					}
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
							//Fix for leftover ice
							if (testLocation.getBlock().getType() != Material.ICE){
								//changedBlocks.get(iceLocation).add(testLocation.getBlock().getState());
								if (Elsafy.getInstance().getConfigManager().rollbackOTFreezeOnInteract){
									elsaRollback.addBlock(iceLocation, testLocation.getBlock().getState());
								}
							}
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
			if (face == BlockFace.DOWN){
				continue;
			}
			
			Block testBlock = loc.getBlock().getRelative(face);
			
			if (testBlock.getType() != Material.WATER && testBlock.getType() != Material.STATIONARY_WATER && testBlock.getType() != Material.PACKED_ICE){
				continue;
			}else{
				if (Elsafy.getInstance().getConfigManager().rollbackEFreezeWater){
					elsaRollback.addWaterBlock(testBlock.getLocation());
				}
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
	
	public void buildSnowPillar(final Location location){
		Random randomGenerator = new Random();
		int height = randomGenerator.nextInt(10) + 1;
		double radius = randomGenerator.nextInt(2) + 2;
		
		final Queue<Location> toSnow = new ConcurrentLinkedQueue<Location>();
		
		//Use WorldEdit Algorithm to generate a cylinder
		toSnow.addAll(Util.makeCylinder(location, 1, radius + 1));
		toSnow.addAll(Util.makeCylinder(location.clone().add(0, 1, 0), height, radius));
		//Use WorldEdit Algorithm to generate a sphere top of the cylinder
		toSnow.addAll(Util.makeSphere(location.clone().add(0, height, 0), radius));
		
		int taskID = 0;
		for (int id : pillarTasks.keySet()){
			if (taskID == id){
				taskID++;
				continue;
			}
		}
		final int finalTaskID = taskID;
		BukkitTask pillarTask = Bukkit.getScheduler().runTaskTimer(Elsafy.getInstance(), new Runnable(){

			@Override
			public void run() {
				int bps = Elsafy.getInstance().getConfigManager().snowPillarsBuildRate;
				for (int i=0; i < bps; i++){
					Location freezeLoc = toSnow.poll();
					Block block = freezeLoc.getBlock();
					if (block.getType() != Material.SNOW_BLOCK){
						/*if (changedBlocks.containsKey(location)){
							changedBlocks.get(location).add(block.getState());
						}else{
							changedBlocks.put(location, new HashSet<BlockState>());
							changedBlocks.get(location).add(block.getState());
						}*/
						if (Elsafy.getInstance().getConfigManager().rollbackOTSnowPillars){
							elsaRollback.addBlock(location, block.getState());
						}
					}
					block.setType(Material.SNOW_BLOCK);
					if (Elsafy.getInstance().getConfigManager().snowPillarParticles){
						if (Elsafy.getInstance().isSpigot()){
							block.getWorld().spigot().playEffect(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()), Effect.CLOUD, 0, 0, 0, 0, 0, 1, 2, 150);
						}else{
							block.getWorld().playEffect(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()), Effect.SMOKE, 0);
						}
					}
					
					if (toSnow.isEmpty()){
						cancelSnowPillarTask(finalTaskID, false);
						break;
					}
				}
			}
			
		}, 1, 1);
		pillarTasks.put(taskID, pillarTask);
		
	}
	
	public void enableIceBridge(){
		bridgeBuilder = new BridgeBuilder(this);
		isBridgeActive = true;
		if (lastBridgeMessage <= System.currentTimeMillis() - 60000){
			Player player = Bukkit.getPlayer(pName);
			if (player != null){
				player.sendMessage(ChatColor.DARK_AQUA + "You are one with the wind and sky!!");
			}
			lastBridgeMessage = System.currentTimeMillis();
		}
	}
	
	public void disableIceBridge(){
		bridgeBuilder.endBridge();
		bridgeBuilder = null;
		isBridgeActive = false;
	}
	
	public BridgeBuilder getBridgeBuilder(){
		return bridgeBuilder;
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
					if (Elsafy.getInstance().getConfigManager().rollbackEFreezeWater){
						elsaRollback.addWaterBlock(loc);
					}
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
	
	public void cancelIceSpreadTask(){
		if (iceSpreadTask != null){
			iceSpreadTask.cancel();
			iceSpreadTask = null;
		}
		if (locationsToIce != null){
			locationsToIce.clear();
		}
	}
	
	private void cancelSnowSwirlingTask(){
		if (snowTask != null){
			snowTask.cancel();
			snowTask = null;
		}
	}
	
	private void cancelSnowPillarTask(int taskID, boolean cancelAll){
		if (cancelAll){
			for (BukkitTask task : pillarTasks.values()){
				task.cancel();
			}
			return;
		}else{
			if (pillarTasks.containsKey(taskID)){
				pillarTasks.get(taskID).cancel();
				pillarTasks.remove(taskID);
			}
		}
	}
	
	public void endElsa(boolean force){
		cancelIceSpreadTask();
		cancelSnowSwirlingTask();
		cancelSnowPillarTask(0, true);
		if (isBridgeActive){
			disableIceBridge();
		}
		if (force){
			elsaRollback.forceEnd();
		}else{
			elsaRollback.end();
		}
		
	}

}
