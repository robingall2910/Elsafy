package org.mcmega.Elsafy.Objects;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcmega.Elsafy.Elsafy;

public class IceArrow extends BukkitRunnable {
	
	private Arrow arrow;
	private Elsa elsa;
	
	@SuppressWarnings("deprecation")
	public IceArrow(Elsa elsa){
		this.elsa = elsa;
		Player player = Bukkit.getPlayer(elsa.getElsaName());
		if (player == null){
			return;
		}
		Block block = player.getTargetBlock(null, 200);
		if (block == null){
			return;
		}
		
		arrow = player.launchProjectile(Arrow.class);
		
		this.runTaskTimer(Elsafy.getInstance(), 1, 1);
	}

	@Override
	public void run() {
		Location loc = arrow.getLocation();
		
		boolean particles = Elsafy.getInstance().getConfigManager().iceBlastParticles;
		int particleCount = Elsafy.getInstance().getConfigManager().iceBlastParticleCount;
		
		if (particles){
			//arrow.getWorld().playEffect(arrow.getLocation(), Effect.SNOWBALL_BREAK, 0);
			//arrow.getWorld().playEffect(arrow.getLocation(), Effect.CLOUD, 0);
			if (Elsafy.getInstance().isSpigot()){
				loc.getWorld().spigot().playEffect(loc, Effect.CLOUD, 0, 0, 0, 0, 0, 1, particleCount, 150);
			}else{
				loc.getWorld().playEffect(loc, Effect.SMOKE, 0);
			}
		}
		
		if (arrow.isOnGround() || arrow.isDead()){
			for (BlockFace face : BlockFace.values()){
				Location effectLoc = loc.getBlock().getRelative(face).getLocation();
				
				if (particles){
					//arrow.getWorld().playEffect(arrow.getLocation(), Effect.SNOWBALL_BREAK, 0);
					//arrow.getWorld().playEffect(arrow.getLocation(), Effect.CLOUD, 0);
					if (Elsafy.getInstance().isSpigot()){
						effectLoc.getWorld().spigot().playEffect(effectLoc, Effect.CLOUD, 0, 0, 0, 0, 0, 1, (particleCount * 2) + 3, 150);
					}else{
						effectLoc.getWorld().playEffect(effectLoc, Effect.SMOKE, 0);
					}
				}
			}
			elsa.randomIceOnInteract(loc, true);
			
			if (Elsafy.getInstance().getConfigManager().iceBlastFrozenHeart){
				List<Entity> entities = arrow.getNearbyEntities(3, 3, 3);
				for (Entity entity : entities){
					if (entity instanceof LivingEntity){
						LivingEntity le = (LivingEntity) entity;
						
						//Elsas cannot have frozen hearts!
						if (le instanceof Player){
							Player player = (Player) le;
							if (Elsafy.getInstance().isElsa(player.getName())){
								continue;
							}
						}
						
						le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 10000, 1));
					}
				}
				
			}
			
			arrow.remove();
			this.cancel();
		}
		
	}

}
