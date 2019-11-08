package org.mcmega.Elsafy.Objects;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcmega.Elsafy.Elsafy;

public class SnowSwirlingTask extends BukkitRunnable {
	
	private Elsa elsa;
	
	public SnowSwirlingTask(Elsa elsa){
		this.elsa = elsa;
		
		Random random = new Random();
		int ticks = random.nextInt(3000) + 1000;
 		this.runTaskTimer(Elsafy.getInstance(), ticks, ticks);
	}

	@Override
	public void run() {
		Player player = Bukkit.getPlayer(elsa.getElsaName());
		if (player == null){
			this.cancel();
			return;
		}
		
		final Location loc = player.getEyeLocation();
		int ticks = 0;
		for (final BlockFace face : BlockFace.values()){
			ticks = ticks + 3;
			Bukkit.getScheduler().runTaskLater(Elsafy.getInstance(), new Runnable(){

				@Override
				public void run() {
					Location effectLoc = loc.getBlock().getRelative(face).getLocation();
					
					if (Elsafy.getInstance().isSpigot())
					{
						effectLoc.getWorld().playEffect(effectLoc, Effect.SMOKE, 0);
					}
				}
				
			}, ticks);
		}
		
		player.sendMessage(ChatColor.BLUE + "Your soul is swirling throughout the air!");
		
	}

}
