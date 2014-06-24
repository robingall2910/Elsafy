package org.mcmega.Elsafy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcmega.Elsafy.Bridge.BridgeManager;
import org.mcmega.Elsafy.Listeners.BridgeListener;
import org.mcmega.Elsafy.Listeners.ElsaListener;
import org.mcmega.Elsafy.Objects.Elsa;

public class Elsafy extends JavaPlugin {
	
	public static Elsafy instance;
	
	public ConfigManager configManager;
	public BridgeManager bridgeManager;
	
	public HashMap<String, Elsa> elsaList = new HashMap<String, Elsa>();
	
	private boolean isSpigot;
	
	@Override
	public void onEnable(){
		instance = this;
		
		Logger log = getLogger();
		
		//Check if Server is Running Spigot
		try {
			Class.forName( "org.spigotmc.SpigotConfig" );
			isSpigot = true;
		} catch( ClassNotFoundException e ) {
			isSpigot = false;
		}
		
		//Display Watermark
		log.log(Level.INFO, "==============================");
		log.log(Level.INFO, " _____  _            ___      ");
		log.log(Level.INFO, "|   __|| | ___  ___ |  _| _ _ ");
		log.log(Level.INFO, "|   __|| ||_ -|| .'||  _|| | |");
		log.log(Level.INFO, "|_____||_||___||__,||_|  |_  |");
		log.log(Level.INFO, "                         |___|");
		log.log(Level.INFO, "Plugin Created by BlueFusion12! V: 2.0");
		log.log(Level.INFO, "Plugin Video: http://youtu.be/IH6P757lDZc");
		log.log(Level.INFO, "==============================");
		
		if (!isSpigot){
			log.log(Level.WARNING, "Your Server is not running Spigot! Spigot is an optimized version of Bukkit that adds more API functionality as well as better performace. "
					+ "Elsafy uses Spigot for advanced particle effects. For now, advanced particles have been disabled. If you wish to use advanced particles, "
					+ "download Spigot here: http://www.spigotmc.org");
		}
		
		//Save Default Schematic
		saveResource("schematics/IceCastle.schematic");
		//Save Bridge Schematics
		saveResource("schematics/BridgeFlatUncomplete.schematic");
		saveResource("schematics/BridgeElevateUncomplete.schematic");
		
		//Load Config
		configManager = new ConfigManager();
		configManager.load(this);
		
		//Check for WorldEdit
		if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null){
			log.log(Level.WARNING, "WorldEdit is not installed... disabling Castle Building!");
			configManager.createCastleEnabled = false;
			//Disable Bridge
		}else{
			bridgeManager = new BridgeManager(this);
		}
		
		registerListeners();
		registerCommands();
	}
	
	@Override
	public void onDisable(){
		for (Elsa elsa : elsaList.values()){
			elsa.endElsa(true);
		}
	}
	
	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new ElsaListener(), this);
		pm.registerEvents(new BridgeListener(), this);
	}
	
	private void registerCommands() {
		getCommand("elsafy").setExecutor(new ElsaCommand());
	}
	
	public static Elsafy getInstance(){
		return instance;
	}
	
	public boolean isSpigot(){
		return isSpigot;
	}
	
	public ConfigManager getConfigManager(){
		return configManager;
	}
	
	public BridgeManager getBridgeManager(){
		return bridgeManager;
	}
	
	public boolean isElsa(String pName){
		if (elsaList.containsKey(pName)){
			return true;
		}else{
			return false;
		}
	}
	
	public Elsa getElsaObject(String pName){
		return elsaList.get(pName);
	}
	
	public void addElsa(String pName){
		elsaList.put(pName, new Elsa(pName));
	}
	
	public void removeElsa(String pName){
		elsaList.get(pName).endElsa(false);
		elsaList.remove(pName);
	}
	
	public Collection<Elsa> getAllElsas(){
		return elsaList.values();
	}
	
	
	//Thanks IslandWorld!
	public void saveResource(String resourcePath){
		if (resourcePath == null || resourcePath.equals("")){
			throw new IllegalArgumentException("ResourcePath cannot be null or empty");
		}

		resourcePath = resourcePath.replace('\\', '/');
		InputStream in = getResource(resourcePath);
		if (in == null){
			throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile());
		}

		File outFile = new File(getDataFolder(), resourcePath);
		int lastIndex = resourcePath.lastIndexOf('/');
		File outDir = new File(getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

		if (!outDir.exists()){
			outDir.mkdirs();
		}

		try{
			if (!outFile.exists()){
				OutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0){
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			}
		}
		catch (IOException ex){
			getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
		}
	}

}
