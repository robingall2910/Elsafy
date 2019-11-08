package org.mcmega.Elsafy.Objects;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcmega.Elsafy.Elsafy;

public class IceBall extends BukkitRunnable {
	
	//private Arrow arrow;
	private Snowball ball;
	private Elsa elsa;
	private LaunchType type;
	
	private float yaw;
	
	public IceBall(Elsa elsa, LaunchType type){
		this.elsa = elsa;
		this.type = type;
		Player player = Bukkit.getPlayer(elsa.getElsaName());
		if (player == null){
			return;
		}
		Block block = player.getTargetBlock(new HashSet<Material>(), 200);
		if (block == null){
			return;
		}
		
		ball = player.getWorld().spawn(player.getEyeLocation(), Snowball.class);
		ball.setShooter(player);
		
		double velocity = 1.5;
		if (type == LaunchType.SNOWFLAKE){
			velocity = 1;
		}
		ball.setVelocity(player.getLocation().getDirection().multiply(velocity));
		yaw = player.getLocation().getYaw();
		
		//arrow = player.launchProjectile(Arrow.class);
		
		this.runTaskTimer(Elsafy.getInstance(), 1, 1);
	}

	@Override
	public void run() {
		Location loc = ball.getLocation();
		
		boolean particles = false;
		int particleCount = 0;
		if (type == LaunchType.SNOW_PILLAR){
			particles = Elsafy.getInstance().getConfigManager().snowPillarParticles;
			particleCount = Elsafy.getInstance().getConfigManager().snowPillarParticleCount;
			
			if (particles){
				//arrow.getWorld().playEffect(arrow.getLocation(), Effect.SNOWBALL_BREAK, 0);
				//arrow.getWorld().playEffect(arrow.getLocation(), Effect.CLOUD, 0);
				if (Elsafy.getInstance().isSpigot())
				{
					loc.getWorld().playEffect(loc, Effect.SMOKE, 0);
				}
			}
		}else if (type == LaunchType.ICE_BLAST){
			particles = Elsafy.getInstance().getConfigManager().iceBlastParticles;
			particleCount = Elsafy.getInstance().getConfigManager().iceBlastParticleCount;
			
			if (particles){
				//arrow.getWorld().playEffect(arrow.getLocation(), Effect.SNOWBALL_BREAK, 0);
				//arrow.getWorld().playEffect(arrow.getLocation(), Effect.CLOUD, 0);
				if (Elsafy.getInstance().isSpigot())
				{
					loc.getWorld().playEffect(loc, Effect.SMOKE, 0);
				}
			}
		}else if (type == LaunchType.SNOWFLAKE){
			if (Elsafy.getInstance().isSpigot())
			{
				loc.getWorld().playEffect(loc, Effect.SMOKE, 0);
			}
		}
		
		//Snowflake when velocity is moving downward
		if (type == LaunchType.SNOWFLAKE){
			if (ball.getVelocity().getY() <= 0 || ball.isDead()){
				for (BlockFace face : BlockFace.values()){
					Location effectLoc = loc.getBlock().getRelative(face).getLocation();
					//arrow.getWorld().playEffect(arrow.getLocation(), Effect.SNOWBALL_BREAK, 0);
					//arrow.getWorld().playEffect(arrow.getLocation(), Effect.CLOUD, 0);
					if (Elsafy.getInstance().isSpigot())
					{
						effectLoc.getWorld().playEffect(effectLoc, Effect.SMOKE, 0);
					}
				}
				ball.remove();
				this.cancel();
			}
			return;
		}
		
		if (ball.isOnGround() || ball.isDead()){
			for (BlockFace face : BlockFace.values()){
				Location effectLoc = loc.getBlock().getRelative(face).getLocation();
				
				if (particles){
					//arrow.getWorld().playEffect(arrow.getLocation(), Effect.SNOWBALL_BREAK, 0);
					//arrow.getWorld().playEffect(arrow.getLocation(), Effect.CLOUD, 0);
					if (Elsafy.getInstance().isSpigot())
					{
						effectLoc.getWorld().playEffect(effectLoc, Effect.SMOKE, 0);
					}
				}
			}
			
			if (type == LaunchType.SNOW_PILLAR){
				elsa.buildSnowPillar(loc);
			}else if (type == LaunchType.ICE_BLAST){
				elsa.randomIceOnInteract(loc, true);
				
				if (Elsafy.getInstance().getConfigManager().iceBlastFrozenHeart){
					List<Entity> entities = ball.getNearbyEntities(3, 3, 3);
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
			}else if (type == LaunchType.ICE_SPIKES){
				System.out.println("Shooting spikes!");
				elsa.shootIceSpikes(loc, yaw);
			}
			
			ball.remove();
			this.cancel();
		}
		
	}

}
