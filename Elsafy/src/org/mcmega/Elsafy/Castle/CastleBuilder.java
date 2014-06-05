package org.mcmega.Elsafy.Castle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.mcmega.Elsafy.Elsafy;
import org.mcmega.Elsafy.Util;
import org.mcmega.Elsafy.Objects.Elsa;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

public class CastleBuilder {
	
	private static Elsafy plugin = Elsafy.getInstance();
	
	public static void LoadandPaste(Elsa elsa){	
		try {
        	File schemaFile = new File(plugin.getDataFolder() + "/schematics/IceCastle.schematic");
        	CuboidClipboard cc = SchematicFormat.MCEDIT.load(schemaFile);
        	build(cc, elsa);
        	
        } catch (IOException | DataException e) {
            e.printStackTrace();
            return;
        }
        
	}

	public static void build(final CuboidClipboard cc, final Elsa elsa){	
		final Player player = Bukkit.getPlayer(elsa.getElsaName());
		if (player == null){
			return;
		}
		
		BlockFace face = Util.getCardinalDirection(player);
		
		if (face == BlockFace.SOUTH){
			cc.rotate2D(90);
		}else if (face == BlockFace.WEST){
			cc.rotate2D(180);
		}else if (face == BlockFace.NORTH){
			cc.rotate2D(270);
		}
		
		Vector size = cc.getSize();
		final Location loc1 = player.getLocation().clone();
		Vector offset = cc.getOffset();
		loc1.add(new org.bukkit.util.Vector(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ()));
		
		final EditSession session = new EditSession(new BukkitWorld(loc1.getWorld()), -1);
		final Queue<BlueBlock> queue = new ConcurrentLinkedQueue<BlueBlock>();
		final List<BlueBlock> pasteLast = new ArrayList<BlueBlock>();
        for (int y = 0; y < size.getBlockY(); ++y) {
            for (int x = 0; x < size.getBlockX(); ++x) {
                for (int z = 0; z < size.getBlockZ(); ++z) {
                    BaseBlock block = cc.getBlock(new Vector(x, y, z));
                    Location testLoc = new Location(loc1.getWorld(), x + loc1.getBlockX(), y + loc1.getBlockY(), z + loc1.getBlockZ());
                    //Block testBlock = testLoc.getBlock();
                    
                    if (block == null) {
                        continue;
                    }
                    
                    /*if (block.getType() == 0 && (testBlock.getType() == Material.STONE ||
                    		testBlock.getType() == Material.DIRT || testBlock.getType() == Material.AIR ||
                    		testBlock.getType() == Material.STATIONARY_WATER || testBlock.getType() == Material.GRASS ||
                    		testBlock.getType() == Material.SNOW || testBlock.getType() == Material.SNOW_BLOCK) && (player.getLocation().getBlockY() > loc1.getBlockY())){
                    	continue;
                    }*/
                    
                    if (block.getType() == 0 && (player.getLocation().getBlockY() > testLoc.getBlockY())){
                    	continue;
                    }
                    
					if (BlockType.shouldPlaceLast(block.getType())){
						pasteLast.add(new BlueBlock(BukkitUtil.toVector(testLoc), block));
						continue;
					}
					
					if (BlockType.shouldPlaceFinal(block.getType())){
						pasteLast.add(new BlueBlock(BukkitUtil.toVector(testLoc), block));
						continue;
					}
                    
                    //Add to block queue
                    BlueBlock bb = new BlueBlock(new Vector(x, y, z).add(BukkitUtil.toVector(loc1)), block);
                    queue.add(bb);
                }
            }
        }
        player.sendMessage(ChatColor.BLUE + "Your power flurries through the air into the ground as your castle is raised by your power!");
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){

			@Override
			public void run() {
				if (player != null){
					player.sendMessage(ChatColor.BLUE + "LET IT GO!! LET IT GOOOOO!!!!");
				}
			}
        	
        }, 200);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable(){

			@Override
			public void run() {
				int bps = Elsafy.getInstance().getConfigManager().createCastleRate;
				for (int i=0; i < bps; i++){
					BlueBlock bb = queue.poll();
					
					if (bb.getBlock().getType() == 0){
						i--;
					}
					
					try {
						session.setBlock(bb.getVector(), bb.getBlock());
						
						if (bb.getBlock().getId() != 0){
							if (Elsafy.getInstance().isSpigot()){
								loc1.getWorld().spigot().playEffect(new Location(loc1.getWorld(), bb.getVector().getBlockX(), bb.getVector().getBlockY(), bb.getVector().getBlockZ()), Effect.CLOUD, 0, 0, 0, 0, 0, 1, 4, 150);
							}else{
								loc1.getWorld().playEffect(new Location(loc1.getWorld(), bb.getVector().getBlockX(), bb.getVector().getBlockY(), bb.getVector().getBlockZ()), Effect.SMOKE, 0);
							}
						}
						
					} catch (MaxChangedBlocksException e) {
						// Should never be called
					}
					
					if (queue.isEmpty()){
						completeBuild(session, pasteLast, elsa);
						return;
					}
				}
				
			}
        	
        }, 1, 1);
        elsa.setCastleTask(task);
	}

	public static void completeBuild(EditSession session, List<BlueBlock> pasteLast, Elsa elsa) {
		for (BlueBlock bb : pasteLast){
			try {
				session.setBlock(bb.getVector(), bb.getBlock());
			} catch (MaxChangedBlocksException e) {
				// Should never be called
			}
		}
		
		elsa.cancelCastleTask();
		
		Player player = Bukkit.getPlayer(elsa.getElsaName());
		
		if (player != null){
			player.sendMessage(ChatColor.BLUE + "By the might of your power, your ice castle has been created! ");
		}
		
	}

}
