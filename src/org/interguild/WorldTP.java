package org.interguild;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldTP extends JavaPlugin {
	
	private static WorldTP itMe = null;
	
	public static WorldTP getMe(){
		return itMe;
	}
	
	public static List<World> CREATIVE_WORLDS = new ArrayList<World>();
	public static List<World> SURVIVAL_WORLDS = new ArrayList<World>();

	static WorldType getWorldType(World world) {
		for (World w : CREATIVE_WORLDS)
			if (w.equals(world))
				return WorldType.CREATIVE;
		for (World w : SURVIVAL_WORLDS)
			if (w.equals(world))
				return WorldType.SURVIVAL;
	
		getMe().getLogger().severe("Error: The world '" + world.getName() + "' isn't listed in the config file for WorldTP.");
		return WorldType.INVALID;
	}
	
	static List<World> getWorldList(WorldType type) {
		if (type == WorldType.CREATIVE)
			return WorldTP.CREATIVE_WORLDS;
		else
			return WorldTP.SURVIVAL_WORLDS;
	}

	static String getFolderName(WorldType type) {
		return (type == WorldType.CREATIVE ? "Creative" : "Survival");
	}
	
	public WorldTP(){
		itMe = this;
	}
	
	 // Fired when plugin is first enabled
    @Override
    public void onEnable() {
    	loadConfigFile();
    	getCommand("wtp").setExecutor(new WorldTPCommand());
    	getServer().getPluginManager().registerEvents(new WorldTPListener(), this);
    	
    	File dataFolder = WorldTP.getMe().getDataFolder();
    	if(!dataFolder.exists()){
    		dataFolder.mkdir();
    	}
    	File f = new File(dataFolder.getAbsolutePath() + File.separator + "Creative");
		if(!f.exists()){
			f.mkdir();
		}
		f = new File(dataFolder.getAbsolutePath() + File.separator + "Survival");
		if(!f.exists()){
			f.mkdir();
		}
    }
    
    private void loadConfigFile(){
    	String[] cworlds = {"world_creative"};
    	getConfig().addDefault("worlds.creative", Arrays.asList(cworlds));
    	String[] sworlds = {"world", "world_nether", "world_the_end"};
    	getConfig().addDefault("worlds.survival", Arrays.asList(sworlds));
    	getConfig().options().copyDefaults(true);
    	saveConfig();
    	
    	for(Object w : getConfig().getList("worlds.creative"))
    		checkWorld((String) w, false);
    	for(Object w : getConfig().getList("worlds.survival"))
    		checkWorld((String) w, true);
    }
    
    private void checkWorld(String worldName, boolean isSurvival){
    	World world = Bukkit.getWorld(worldName);
    	
    	if(world==null){
    		getLogger().severe("The config file for WorldTP has an invalid world name: '"+worldName+"'");
    		return;
    	}
    	
    	if(isSurvival){
    		SURVIVAL_WORLDS.add(world);
    	}else{
    		CREATIVE_WORLDS.add(world);
    	}
    }
    
    // Fired when plugin is disabled
    @Override
    public void onDisable() {

    }
}
