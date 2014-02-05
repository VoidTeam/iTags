package com.jacklinkproductions.iTags;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginDescriptionFile;

import com.jacklinkproductions.iTags.Updater;

public class Main extends JavaPlugin
{
    static PluginDescriptionFile pdfFile;

	public static String enableChatTags = "true";
	public static String enableSignTags = "true";
	public static String enableHashTags = "false";
	public static String enableDotSlashCleaner = "true";
	public static String enableLinkUnderliner = "true";
	public static String signNotifyType = "mini";
	public static String playerTag = "@";
	public static String playerTagColor = "&e";
	public static String hashTagColor = "&b";
	public static String linkColor = "&7";
	public static String chatColor = "&f";
	public static String censorHashTags = "true";
	public static String useDisplayNameColors = "false";
	public static String chatSound = "NOTE_PLING";
	public static String signSound = "NOTE_BASS_GUITAR";
	public static String updateNotification = "true";
	
	public static Integer configVersion = 3;
	public static int updaterID = 60549;
	
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
        
        // Check for old config
        if ((getConfig().isSet("config-version") == false) || (getConfig().getInt("config-version") < 3))
        {
            // Delete old config
            File oldfile = new File(this.getDataFolder(), "config.yml");
            oldfile.delete();
            
            // Create new config from file
            saveDefaultConfig();
            
            // Update config values from previous config version
            File file = new File(this.getDataFolder(), "config.yml");
            try {
				updateConfig(file);
				reloadConfiguration();
	            getLogger().info("Updated config.yml file with new options!");
			} catch (Exception e) {
	            getLogger().warning("Config.yml could not be updated! Please delete it! E01");
				e.printStackTrace();
			}
        }

        // Setup Updater system
        if (getConfig().getString("update-notification") == "true")
        {
        	checkForUpdates();
        }
        
        // Register our events
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        // Register command executor.
        Commands commandExecutor = new Commands(this);
        getCommand("itags").setExecutor(commandExecutor);
        getCommand("ding").setExecutor(commandExecutor);
        getCommand("trending").setExecutor(commandExecutor);
        
        // Output info to console on load
        pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " v" + pdfFile.getVersion() + " is enabled!" );
    }

	public static String parseColor(String line)
	{
		return ChatColor.translateAlternateColorCodes('&', line);
	}
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	// For hashtags...
    @SuppressWarnings("rawtypes")
	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map) {
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
    
    public void reloadConfiguration() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        reloadConfig();
        enableChatTags = getConfig().getString("enableChatTags");
        enableSignTags = getConfig().getString("enableSignTags");
        enableHashTags = getConfig().getString("enableHashTags");
        playerTag = getConfig().getString("playerTag");
        playerTagColor = getConfig().getString("playerTagColor");
        hashTagColor = getConfig().getString("hashTagColor");
        linkColor = getConfig().getString("linkColor");
        chatColor = getConfig().getString("chatColor");
        useDisplayNameColors = getConfig().getString("useDisplayNameColors");
        chatSound = getConfig().getString("chatSound");
        signSound = getConfig().getString("signSound");
        signNotifyType = getConfig().getString("signNotifyType");
        censorHashTags = getConfig().getString("censorHashTags");
        enableDotSlashCleaner = getConfig().getString("enableDotSlashCleaner");
        enableLinkUnderliner = getConfig().getString("enableLinkUnderliner");
    	updateNotification = getConfig().getString("update-notification");
    }
    
    public void updateConfig(File config) throws Exception
    {
        BufferedReader reader = new BufferedReader(new FileReader(config));
        File tempFile = new File(getDataFolder(), "temp.ignore");
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        String line;
        try
        {
            while ((line = reader.readLine()) != null)
            {
            	
            	line = line.replaceAll(".*enableChatTags.*", "enableChatTags: " + enableChatTags);
            	line = line.replaceAll(".*enableSignTags.*", "enableSignTags: " + enableSignTags);
            	line = line.replaceAll(".*enableHashTags.*", "enableHashTags: " + enableHashTags);
            	line = line.replaceAll(".*playerTag:.*", "playerTag: '" + playerTag + "'");
            	line = line.replaceAll(".*playerTagColor.*", "playerTagColor: '" + playerTagColor + "'");
            	line = line.replaceAll(".*hashTagColor.*", "hashTagColor: '" + hashTagColor + "'");
            	line = line.replaceAll(".*linkColor.*", "linkColor: '" + linkColor + "'");
            	line = line.replaceAll(".*chatColor.*", "chatColor: '" + chatColor + "'");
            	line = line.replaceAll(".*useDisplayNameColors.*", "useDisplayNameColors: " + useDisplayNameColors);
            	line = line.replaceAll(".*chatSound.*", "chatSound: " + chatSound);
            	line = line.replaceAll(".*signSound.*", "signSound: " + signSound);
            	line = line.replaceAll(".*signNotifyType.*", "signNotifyType: " + signNotifyType);
            	line = line.replaceAll(".*censorHashTags.*", "censorHashTags: " + censorHashTags);
            	line = line.replaceAll(".*enableDotSlashCleaner.*", "enableDotSlashCleaner: " + enableDotSlashCleaner);
            	line = line.replaceAll(".*enableLinkUnderliner.*", "enableLinkUnderliner: " + enableLinkUnderliner);
            	line = line.replaceAll(".*update-notification.*", "update-notification: " + updateNotification);
            	line = line.replaceAll(".*config-version.*", "config-version: " + configVersion);
            	
	            writer.write(line);
	            writer.newLine();
            }
        }
        catch (Exception e)
        {
            getLogger().warning("Config.yml could not be updated! Please delete it! E02");
            e.printStackTrace();
        }
        finally
        {
            reader.close();
            writer.flush();
            writer.close();
            config.delete();
            tempFile.renameTo(config);
        }
    }

    public boolean updateAvailable = false;
    String latestVersion = null;
    long updateSize = 0;

    public void checkForUpdates()
    {
        final Updater updater = new Updater(this, updaterID, getFile(), Updater.UpdateType.NO_DOWNLOAD, true); // Start Updater but just do a version check
        updateAvailable = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE; // Determine if there is an update ready for us
        latestVersion = updater.getLatestName();
        getLogger().info(latestVersion + " is the latest version available, and the updatability of it is: " + updater.getResult().name());

        if(updateAvailable)
        {
            for (Player player : getServer().getOnlinePlayers())
            {
                if (player.hasPermission("lampcontrol.update"))
                {
                    player.sendMessage(ChatColor.YELLOW + "An update is available: " + latestVersion);
                    player.sendMessage(ChatColor.YELLOW + "Type /itags update if you would like to update.");
                }
            }

            getServer().getPluginManager().registerEvents(new Listener()
            {
                @EventHandler
                public void onPlayerJoin (PlayerJoinEvent event)
                {
                    Player player = event.getPlayer();
                    if (player.hasPermission("lampcontrol.update"))
                    {
                        player.sendMessage(ChatColor.YELLOW + "An update is available: " + latestVersion);
                        player.sendMessage(ChatColor.YELLOW + "Type /itags update if you would like to update.");
                    }
                }
            }, this);
        }
    }
    
    @Override
    public File getFile() {

        return super.getFile();
    }
}
