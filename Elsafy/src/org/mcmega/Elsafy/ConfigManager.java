package org.mcmega.Elsafy;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigManager {
	
	Configuration config;
	
	//FreezeOnInteract
	public boolean freezeOnInteractEnabled;
	public int freezeOnInteractProbability;
	public int freezeOnInteractRadius;
	
	//IceBlast
	public boolean iceBlastEnabled;
	public boolean iceBlastParticles;
	public int iceBlastParticleCount;
	public boolean iceBlastFrozenHeart;
	public int iceBlastCooldown;
	
	//FreezeWater
	public boolean freezeWaterEnabled;
	public int freezeWaterRate;
	
	//CreateCastle
	public boolean createCastleEnabled;
	public int createCastleRate;
	public boolean createCastleParticles;
	public int createCastleParticleAmount;
	public int createCastleCooldown;
	
	//BuildSnowman
	public boolean buildSnowmanEnabled;
	public int buildSnowmanCooldown;
	
	//Snow Swirling
	public boolean snowSwirlingEnabled;
	
	//Snow Pillars
	public boolean snowPillarsEnabled;
	public boolean snowPillarParticles;
	public int snowPillarParticleCount;
	public int snowPillarsMisfireRate;
	public int snowPillarsBuildRate;
	public int snowPillarsCooldown;
	
	//Snowflake
	public boolean snowflakeEnabled;
	public int snowflakeCooldown;
	
	//Build Bridge
	public boolean bridgeEnabled;
	public boolean bridgeParticles;
	public int bridgeParticleCount;
	
    public boolean load(Elsafy plugin) {
        if (plugin == null) {
            return false;
        }

        plugin.saveDefaultConfig();

        config = plugin.getConfig();
        ConfigurationSection mainSection = config.getConfigurationSection("Powers");
        if (mainSection == null) {
            return false;
        }
        
        //Config Update
        if(!config.isSet("Powers.SnowPillars")){
        	File file = new File(plugin.getDataFolder(), "config.yml");
        	file.delete();
        	plugin.saveDefaultConfig();
        	plugin.getLogger().log(Level.WARNING, "Due to an update, your configuration has been reset! Please reconfigure your config as you see fit!");
        }
        
        //Parse Powers
        freezeOnInteract(mainSection);
        iceBlast(mainSection);
        freezeWater(mainSection);
        createCastle(mainSection);
        buildSnowman(mainSection);
        snowSwirling(mainSection);
        snowPillar(mainSection);
        snowflake(mainSection);
        bridge(mainSection);
        
        return true;
    }

	private void freezeOnInteract(ConfigurationSection powerSection){
    	ConfigurationSection foiSection = powerSection.getConfigurationSection("FreezeOnInteract");
    	
    	this.freezeOnInteractEnabled = foiSection.getBoolean("enabled", false);
    	this.freezeOnInteractProbability = foiSection.getInt("probability", 10);
    	this.freezeOnInteractRadius = foiSection.getInt("maxradius", 6);
    }
    
    private void iceBlast(ConfigurationSection powerSection){
    	ConfigurationSection ibSection = powerSection.getConfigurationSection("IceBlast");
    	
    	this.iceBlastEnabled = ibSection.getBoolean("enabled", false);
    	this.iceBlastParticles = ibSection.getBoolean("particles", true);
    	this.iceBlastParticleCount = ibSection.getInt("particleamount", 6);
    	this.iceBlastFrozenHeart = ibSection.getBoolean("applyfrozenheart", false);
    	this.iceBlastCooldown = ibSection.getInt("cooldown", 1000);
    }
    
    private void freezeWater(ConfigurationSection powerSection){
    	ConfigurationSection fwSection = powerSection.getConfigurationSection("FreezeWater");
    	
    	this.freezeWaterEnabled = fwSection.getBoolean("enabled", false);
    	this.freezeWaterRate = fwSection.getInt("freezerate", 10);
    }
    
    private void createCastle(ConfigurationSection powerSection){
    	ConfigurationSection ccSection = powerSection.getConfigurationSection("CreateCastle");
    	
    	this.createCastleEnabled = ccSection.getBoolean("enabled", false);
    	this.createCastleParticles = ccSection.getBoolean("particles", true);
    	this.createCastleParticleAmount = ccSection.getInt("particleamount", 6);
    	this.createCastleRate = ccSection.getInt("buildrate", 15);
    	this.createCastleCooldown = ccSection.getInt("cooldown", 120);
    }
    
    private void buildSnowman(ConfigurationSection powerSection){
    	ConfigurationSection bsSection = powerSection.getConfigurationSection("BuildSnowman");
    	
    	this.buildSnowmanEnabled = bsSection.getBoolean("enabled", false);
    	this.buildSnowmanCooldown = bsSection.getInt("cooldown", 1000);
    }
    
    private void snowSwirling(ConfigurationSection powerSection){
    	ConfigurationSection ssSection = powerSection.getConfigurationSection("SnowSwirling");
    	
    	this.snowSwirlingEnabled = ssSection.getBoolean("enabled", false);
    }
    
    private void snowPillar(ConfigurationSection powerSection) {
    	ConfigurationSection spSection = powerSection.getConfigurationSection("SnowPillars");
    	
		this.snowPillarsEnabled = spSection.getBoolean("enabled", true);
		this.snowPillarParticles = spSection.getBoolean("particles", true);
		this.snowPillarParticleCount = spSection.getInt("particleamount", 6);
		this.snowPillarsMisfireRate = spSection.getInt("misfirerate", 10);
		this.snowPillarsBuildRate = spSection.getInt("pillarbuildrate", 4);
		this.snowPillarsCooldown = spSection.getInt("cooldown", 1000);
	}
    
	private void snowflake(ConfigurationSection powerSection) {
		ConfigurationSection sfSection = powerSection.getConfigurationSection("Snowflake");
		
		this.snowflakeEnabled = sfSection.getBoolean("enabled", true);
		this.snowflakeCooldown = sfSection.getInt("cooldown", 5000);
	}
	
	private void bridge(ConfigurationSection powerSection) {
		ConfigurationSection bSection = powerSection.getConfigurationSection("Bridge");
		
		this.bridgeEnabled = bSection.getBoolean("enabled", true);
		this.bridgeParticles = bSection.getBoolean("particles", true);
		this.bridgeParticleCount = bSection.getInt("particleamount", 4);
	}
	
}
