package com.jacklinkproductions.iTags;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class Main extends JavaPlugin {
	
    private static PluginDescriptionFile pdfFile;
	private static Server bukkitServer;
	public static String enableJacksStuff = "false";
    
    @Override
    public void onDisable() {
        // Output info to console on disable
        getLogger().info("Thanks for using iTags!");
    }

    @Override
    public void onEnable() {
    	
        // Create default config if not exist yet.
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        // Load configuration.
        reloadConfiguration();
        
        // Register our events
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        // Register command executor.
        Commands commandExecutor = new Commands(this);
        getCommand("itags").setExecutor(commandExecutor);
        getCommand("ding").setExecutor(commandExecutor);
        getCommand("trending").setExecutor(commandExecutor);
        if (enableJacksStuff == "true")
        {
        	getCommand("tp").setExecutor(commandExecutor);
        	getCommand("print").setExecutor(commandExecutor);
        }
        
        // Output info to console on load
        pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    
    public void reloadConfiguration() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        reloadConfig();
        ChatListener.enableChatTags = getConfig().getString("enableChatTags");
        ChatListener.enableSignTags = getConfig().getString("enableSignTags");
        ChatListener.enableHashTags = getConfig().getString("enableHashTags");
        ChatListener.enableDotSlashCleaner = getConfig().getString("enableDotSlashCleaner");
        ChatListener.enableLinkUnderliner = getConfig().getString("enableLinkUnderliner");
        enableJacksStuff = getConfig().getString("enableJacksStuff");
        ChatListener.signNotifyType = getConfig().getString("signNotifyType");
        ChatListener.playerTag = getConfig().getString("playerTag");
        ChatListener.playerTagColor = getConfig().getString("playerTagColor");
        ChatListener.hashTagColor = getConfig().getString("hashTagColor");
        Commands.hashTagColor = getConfig().getString("hashTagColor");
        ChatListener.linkColor = getConfig().getString("linkColor");
        ChatListener.useDisplayNameColors = getConfig().getString("useDisplayNameColors");
        ChatListener.chatSound = getConfig().getString("chatSound");
        ChatListener.signSound = getConfig().getString("signSound");
    }
    
	public static PluginDescriptionFile getPDF()
	{
		return pdfFile;
	}

	public static Server getBukkitServer()
	{
		return bukkitServer;
	}

	public static String parseColor(String line)
	{
		return ChatColor.translateAlternateColorCodes('&', line);
	}
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
    @SuppressWarnings("rawtypes")
	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
      
        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

            @SuppressWarnings("unchecked")
			@Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
      
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
      
        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        
        return sortedMap;
    }

}
