package org.mcmega.Elsafy.Bridge;

import java.io.File;
import java.io.IOException;

import org.bukkit.block.BlockFace;
import org.mcmega.Elsafy.Elsafy;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

@SuppressWarnings("deprecation")
public class BridgeManager {
	
	public File flatUncomplete;
	//public File flatComplete;
	public File elevateUncomplete;
	//public File elevateComplete;
	
	public BridgeManager(Elsafy plugin){
		flatUncomplete = new File(plugin.getDataFolder() + "/schematics/BridgeFlatUncomplete.schematic");
		//flatComplete = new File(plugin.getDataFolder() + "/schematics/BridgeFlatComplete.schematic");
		elevateUncomplete = new File(plugin.getDataFolder() + "/schematics/BridgeElevateUncomplete.schematic");
		//elevateComplete = new File(plugin.getDataFolder() + "/schematics/BridgeElevateComplete.schematic");
	}
	
	public CuboidClipboard getUncompletePiece(BlockFace nextBridge){
		try {
			if (nextBridge == null){
				return SchematicFormat.MCEDIT.load(flatUncomplete);
			}
			
			if (nextBridge == BlockFace.UP){
				return SchematicFormat.MCEDIT.load(elevateUncomplete);
			}else if (nextBridge == BlockFace.DOWN){
				CuboidClipboard cc = SchematicFormat.MCEDIT.load(elevateUncomplete);
				cc.rotate2D(180);
				return cc;
			}else{
				return SchematicFormat.MCEDIT.load(flatUncomplete);
			}
		} catch (IOException | DataException e) {
			e.printStackTrace();
			return null;
		}
	}

}
