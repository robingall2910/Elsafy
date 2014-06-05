package org.mcmega.Elsafy.Castle;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;

public class BlueBlock {
	
	private Vector vector;
	private BaseBlock block;
	
	public BlueBlock(Vector vector, BaseBlock block){
		this.vector = vector;
		this.block = block;
	}

	public Vector getVector() {
		return vector;
	}

	public BaseBlock getBlock() {
		return block;
	}
	
	public BlockState getBukkitBlockState(World world){
		return new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).getBlock().getState();
	}

}
