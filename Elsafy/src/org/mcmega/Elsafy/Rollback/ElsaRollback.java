package org.mcmega.Elsafy.Rollback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitTask;
import org.mcmega.Elsafy.Elsafy;
import org.mcmega.Elsafy.Objects.Elsa;

public class ElsaRollback {
	
	private HashMap<Location, HashSet<BlockState>> changedBlocks = new HashMap<Location, HashSet<BlockState>>();
	private HashSet<Location> waterBlocks = new HashSet<Location>();
	private HashSet<BlockState> endBlocks = new HashSet<BlockState>();
	
	private BukkitTask rollbackTask = null;
	
	public ElsaRollback(Elsa elsa) {
		if (!Elsafy.getInstance().getConfigManager().rollbackEnabled){
			return;
		}
		
		startRollbackTask();
	}
	
	public void addWaterBlock(Location location){
		waterBlocks.add(location);
	}
	
	public void addEndNotWaterBlock(BlockState state){
		endBlocks.add(state);
	}
	
	public void addBlock(Location core, BlockState state){
		if (changedBlocks.containsKey(core)){
			changedBlocks.get(core).add(state);
		}else{
			HashSet<BlockState> changes = new HashSet<BlockState>();
			changes.add(state);
			changedBlocks.put(core, changes);
		}
	}
	
	private void startRollbackTask(){
		if (rollbackTask != null){
			return;
		}
		rollbackTask = Bukkit.getScheduler().runTaskTimer(Elsafy.getInstance(), new Runnable(){

			@Override
			public void run() {
				Set<Location> iceLocationsCloned = new HashSet<Location>();
				iceLocationsCloned.addAll(changedBlocks.keySet());
				
				for (Location iceLocation : iceLocationsCloned){
					HashSet<BlockState> blocks = changedBlocks.get(iceLocation);
					if (blocks.size() == 0){
						changedBlocks.remove(iceLocation);
						endRollbackTask();
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
			
		}, 200, Elsafy.getInstance().getConfigManager().rollbackTicksPerRun);
	}
	
	public void endRollbackTask(){
		if (rollbackTask != null){
			rollbackTask.cancel();
			rollbackTask = null;
		}
	}
	
	public void end(){
		endRollbackTask();
		for (Location iceLocation : changedBlocks.keySet()){
			for (BlockState state : changedBlocks.get(iceLocation)){
				state.update(true);
			}
		}
		for (Location location : waterBlocks){
			location.getBlock().setType(Material.WATER);
		}
		for (BlockState state : endBlocks){
			state.update(true);
		}
	}

}
