
package com.jacklinkproductions.iTags;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
	
	public static String hashTagColor = "&b";
	
    private final Main plugin;

    Commands(Main plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("itags"))
        {
            if ((args.length == 0) || (args[0].equalsIgnoreCase("help")) || args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("info"))
            {
              sender.sendMessage(ChatColor.GREEN + "[iTags] Version 1.0");
              return true;
            }
            if (args[0].equalsIgnoreCase("reload"))
            {
              if ((sender.hasPermission("itags.reload")) || (sender.hasPermission("itags.*")) || (sender.isOp()))
              {
                  plugin.reloadConfiguration();
                  sender.sendMessage(ChatColor.GREEN + "[iTags] Configuration reloaded!");
                  return true;
              }
              else
              {
                  sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
                  return true;
              }
            }
        }
        
        if (cmd.getName().equalsIgnoreCase("ding"))
        {
            if ((sender.hasPermission("itags.ding")) || (sender.hasPermission("itags.*")) || (sender.isOp()) && (sender instanceof Player))
            {
                Player fromPlayer = (Player) sender;
                Player toPlayer = (Player) sender;
                int inote = 0;

            	if (args.length == 2 && !Main.isNumeric(args[0]) && Main.isNumeric(args[1])) // /ding name number
	            {
                	if (Bukkit.getPlayer(args[0]) == null) //Player doesn't exist
                	{
            			fromPlayer.sendMessage(ChatColor.RED + "Player not found!");
            			return true;
                	}
                	else
                	{
                		toPlayer = Bukkit.getPlayer(args[0]);
                	}
            		
	            	inote = Integer.valueOf(args[1]) + 14;
                	
					float dnote = Float.valueOf(inote);
	            	float note = dnote / 28;
	            	
	        		toPlayer.getWorld().playSound(toPlayer.getLocation(), Sound.NOTE_PLING, 0.6F, note);
	    			toPlayer.sendMessage(ChatColor.GOLD + "Hey you! " + fromPlayer.getDisplayName() + ChatColor.GOLD + " wants your attention!");
	    			fromPlayer.sendMessage(ChatColor.GOLD + "A ding has been sent to " + toPlayer.getDisplayName() + ChatColor.GOLD + "!");
	    			return true;
	            }
                else if (args.length == 1) // /ding X
                {
                	if (Main.isNumeric(args[0])) //If it's a number do it to self and set note to arg[0]
                	{
    	            	inote = Integer.valueOf(args[0]) + 14;
    	            	
    					float dnote = Float.valueOf(inote);
    	            	float note = dnote / 28;
    	            	
    	        		toPlayer.getWorld().playSound(toPlayer.getLocation(), Sound.NOTE_PLING, 0.6F, note);
    	    			toPlayer.sendMessage(ChatColor.GOLD + "Ding!");
    	    			return true;
                	}
	    			else //If it's not a number then arg[0] is a player name
	    			{
	                	if (Bukkit.getPlayer(args[0]) == null) //Player doesn't exist
	                	{
	            			fromPlayer.sendMessage(ChatColor.RED + "Player not found!");
	            			return true;
	                	}
	                	else
	                	{
	                		toPlayer = Bukkit.getPlayer(args[0]);
	                	}
                    	
    					float dnote = Float.valueOf(inote);
    	            	float note = dnote / 28;
    	            	
    	        		toPlayer.getWorld().playSound(toPlayer.getLocation(), Sound.NOTE_PLING, 0.6F, note);
    	    			toPlayer.sendMessage(ChatColor.GOLD + "Hey you! " + fromPlayer.getDisplayName() + ChatColor.GOLD + " wants your attention!");
    	    			fromPlayer.sendMessage(ChatColor.GOLD + "A ding has been sent to " + toPlayer.getDisplayName() + ChatColor.GOLD + "!");
    	    			return true;
	    			}
                }
            	else if (args.length == 0) // /ding
                {                	
					float dnote = Float.valueOf(inote);
	            	float note = dnote / 28;
	            	
	        		toPlayer.getWorld().playSound(toPlayer.getLocation(), Sound.NOTE_PLING, 0.6F, note);
	    			fromPlayer.sendMessage(ChatColor.GOLD + "Ding!");
	    			return true;
                }
            	else
            	{
        			fromPlayer.sendMessage(ChatColor.RED + "Incorrect Syntax: /ding [name] [note]");
        			return true;
            	}
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
                return true;
            }
        }
    
	    if (cmd.getName().equalsIgnoreCase("trending"))
	    {
	        if ((sender.hasPermission("itags.trending")) || (sender.hasPermission("itags.*")) || (sender.isOp()))
	        {
		    	File file = new File(plugin.getDataFolder(), "hashtags.log");
		        if (!file.exists()) {
		            sender.sendMessage(ChatColor.RED + "So far no one has used any #hashtags!");
		        }
		        else
		        {
		        	//Get each line from the file and add it to a List
		        	ArrayList<String> list = new ArrayList<String>();
		        	BufferedReader in = null;
					try {
						in = new BufferedReader(new FileReader(file));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
		        	String line = null;
		        	try {
						while ((line = in.readLine()) != null)
						{
				        	list.add(line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
		        	
		        	//Analyze the List for unique hashtags and count them
		        	Set<String> unique = new HashSet<String>(list);
		        	Map<String, Integer> hashtagOccurances = new HashMap<String, Integer>();
		        	for (String hashtag : unique) {
		        		int amount = Collections.frequency(list, hashtag);
		        		hashtagOccurances.put(hashtag, amount);
		        	}
		        	
		            sender.sendMessage("-- " + ChatColor.GOLD + "Trending #hashtags" + ChatColor.RESET + " --");

			        Map<String, Integer> sortedHashtags = Main.sortByValues(hashtagOccurances);
			        List<Entry<String, Integer>> reversedHashtags = new ArrayList<>(sortedHashtags.entrySet());
			        int count = 1;
			        for (int i = reversedHashtags.size() -1; i >= 0 ; i --)
			        {
			            Entry<String, Integer> entry = reversedHashtags.get(i);
			            String hashtag = entry.getKey();
			            Integer occurances = entry.getValue();
			            
			            if (count > 8)
			            {
			            	break;
			            }

			            sender.sendMessage(ChatColor.GRAY + "" + count + ". " + Main.parseColor(hashTagColor) + hashtag + ChatColor.WHITE + " (" + occurances + ")");
			            count++;
			        }
		        }
	            return true;
	        }
	        else
	        {
	            sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
	            return true;
	        }
	    }
	    
	    if (cmd.getName().equalsIgnoreCase("tp"))
	    {
	        if (((sender.hasPermission("itags.tpo")) || (sender.hasPermission("itags.*")) || (sender.isOp()) && (sender instanceof Player)))
	        {
	            Player player = (Player) sender;
                if (args.length == 1)
                {
		        	player.performCommand("tpo " + args[0]);
		            return true;
                }
                else if (args.length == 2)
                {
		        	player.performCommand("tpo " + args[0] + args[1]);
		            return true;
                }
                else
                {
		        	player.performCommand("tpo");
                	return true;
                }
	        }
	        else if (sender instanceof Player)
	        {
	            Player player = (Player) sender;
                if (args.length == 1)
                {
		        	player.performCommand("etp " + args[0]);
		            return true;
                }
                else if (args.length == 2)
                {
		        	player.performCommand("etp " + args[0] + args[1]);
		            return true;
                }
                else
                {
		        	player.performCommand("etp");
                	return true;
                }
	        }
	        else
	        {
                if (args.length == 1)
                {
                	Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "etp " + args[0]);
		            return true;
                }
                else if (args.length == 2)
                {
                	Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "etp " + args[0] + args[1]);
		            return true;
                }
                else
                {
                	Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "etp");
                	return false;
                }
	        }
	    }
	    
	    if (cmd.getName().equalsIgnoreCase("print"))
	    {
	        if ((sender.hasPermission("itags.print")) || (sender.hasPermission("itags.*")) || (sender.isOp()))
	        {
	            if (args.length > 0)
	            {
					StringBuilder strBuilder = new StringBuilder();
					for (int i=0; i<args.length; i++)
					{
					    strBuilder.append(args[i]);
					    strBuilder.append(" ");
					}
					String message = Main.parseColor(strBuilder.toString());
					
			    	Bukkit.getServer().broadcastMessage(message);
		            return true;
	            }
	            else
	            {
        			sender.sendMessage(ChatColor.RED + "Incorrect Syntax: /print <message>");
        			return true;
	            }
	        }
	        else
	        {
	            sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
	            return true;
	        }
	    }
    
    return false;
  }
}
