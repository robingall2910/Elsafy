package org.mcmega.Elsafy;

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
	
	//CreateCastke
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
        
        //Parse Powers
        freezeOnInteract(mainSection);
        iceBlast(mainSection);
        freezeWater(mainSection);
        createCastle(mainSection);
        buildSnowman(mainSection);
        snowSwirling(mainSection);
        
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

}
