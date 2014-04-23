package org.mcmega.Elsafy.Castle;

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

}
