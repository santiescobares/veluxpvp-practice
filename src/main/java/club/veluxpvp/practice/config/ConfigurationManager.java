package club.veluxpvp.practice.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import club.veluxpvp.practice.Practice;

public class ConfigurationManager {

	private Practice plugin;
	
	private File configFile = null;
	private File arenasFile = null;
	private FileConfiguration arenas = null;
	
	public ConfigurationManager() {
		this.plugin = Practice.getInstance();
		
		loadConfig();
		loadArenas();
	}

	// Config File
	public void loadConfig() {
		configFile = new File(plugin.getDataFolder(), "config.yml");
		
		if(!configFile.exists()) {
			plugin.getConfig().options().copyDefaults(true);
			plugin.saveConfig();
		}
	}
	
	// Arenas File
	public void loadArenas() {
		arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
		
		if(!arenasFile.exists()){
			this.getArenas().options().copyDefaults(true);
			
			saveArenas();
		}
	}
	
	public void saveArenas() {
		try {
			arenas.save(arenasFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
   
    public FileConfiguration getArenas() {
    	if (arenas == null) {
    		reloadArenas();
    	}
    	
    	return arenas;
    }
   
    public void reloadArenas() {
    	if (arenas == null) {
    		arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
    	}
    	
    	arenas = YamlConfiguration.loadConfiguration(arenasFile);
    	
    	Reader defConfigStream;
    	
    	try {
    		defConfigStream = new InputStreamReader(plugin.getResource("arenas.yml"), "UTF8");
    		
    		if (defConfigStream != null) {
    			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
    			
    			arenas.setDefaults(defConfig);
    		}
    	} catch (UnsupportedEncodingException e) {
    		e.printStackTrace();
    	}      
    }
}
