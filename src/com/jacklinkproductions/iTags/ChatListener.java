package com.jacklinkproductions.iTags;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener
{	
    private final Main plugin;

    ChatListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e)
	throws EventException, IOException
	{
		String message = e.getMessage();
		String tagmessage;
		String linkmessage;
		Player taggedplayer;
		
		if (message.contains("./") && Main.enableDotSlashCleaner == "true")
		{
			message = message.replaceAll("./", "/");
			e.setMessage(message);
		}
		
		if  (Main.enableLinkUnderliner == "true" && (message.contains("http://") || message.contains("https://")))
		{
			if ((e.getPlayer().hasPermission("itags.links")) || (e.getPlayer().hasPermission("itags.*")) || (e.getPlayer().isOp()))
			{
				String[] words = message.split(" ");
				for (int x = 0; x < words.length; x++)
				{
					if (words[x].contains(".") && (words[x].startsWith("http://") || words[x].startsWith("https://"))) //Ensure that the link is a link
					{
						linkmessage = words[x];
						message = message.replace(linkmessage, Main.parseColor(Main.linkColor) + Main.parseColor("&n") + linkmessage + " " + Main.parseColor("&r") + Main.parseColor(Main.chatColor));
					}
				}

				e.setMessage(message);
			}
		}
		
		if (message.contains(Main.playerTag) && Main.enableChatTags == "true")
		{
			if ((e.getPlayer().hasPermission("itags.playertag")) || (e.getPlayer().hasPermission("itags.*")) || (e.getPlayer().isOp()) || (!e.getPlayer().isOnline()))
			{
				String[] words = message.split(" ");
				for (int x = 0; x < words.length; x++)
				{
					if (words[x].startsWith(Main.playerTag) && words[x].length() > 2)
					{
						tagmessage = words[x];
						tagmessage = tagmessage.replaceAll(Main.playerTag, "");

						// Prepare for ending punctuation check
						String last = tagmessage.substring(tagmessage.length() - 1);
						String[] apunctuation = {"?", "!", ".", ",", "\"", "'", ")", "}", "]", "/"};
						List <String> punctuation = Arrays.asList(apunctuation);
						String endmark = "";
						
						if (punctuation.contains(last))
						{
							tagmessage = tagmessage.replace(last, "");
						}
						
						taggedplayer = Bukkit.getPlayer(tagmessage);
						
						if (taggedplayer != null)
						{
							if (Main.useDisplayNameColors == "true")
							{
								message = message.replaceAll(Main.playerTag + tagmessage + endmark, taggedplayer.getDisplayName() + Main.parseColor("&r") + Main.parseColor(Main.chatColor) + endmark);
							}
							else
							{
								message = message.replaceAll(Main.playerTag + tagmessage + endmark, Main.parseColor(Main.playerTagColor) + Main.playerTag + taggedplayer.getName() + Main.parseColor("&r") + Main.parseColor(Main.chatColor) + endmark);
							}
							taggedplayer.getWorld().playSound(taggedplayer.getLocation(), Sound.valueOf(Main.chatSound.toUpperCase()), 0.5F, 0.0F);
						}
					}
				}
				
				e.setMessage(message);
			}
		}
		
		if (message.contains("#") && Main.enableHashTags == "true")
		{
			if ((e.getPlayer().hasPermission("itags.hashtag")) || (e.getPlayer().hasPermission("itags.*")) || (e.getPlayer().isOp()) || (!e.getPlayer().isOnline()))
			{
				String[] yesbadwords = {"fucked", "ass", "assface", "asshole", "assholes", "asswipe", "bastard", "bastards", "bastardz", "basterds", "basterdz", "blowjob", "c0ck", "c0cks", "c0k", "cawk", "cawks", "clit", "cnts", "cntz", "cock", "cockhead", "cocks", "cocksucker", "crap", "cum", "cunt", "cunts", "cuntz", "dick", "dild0", "dild0s", "dildo", "dildos", "dilld0", "dilld0s", "dyke", "enema", "fag", "fag1t", "faget", "fagg1t", "faggit", "faggot", "fagit", "fags", "fagz", "faig", "faigs", "fart", "fuck", "fucker", "fuckin", "fucking", "fucks", "fuk", "fukah", "fuken", "fuker", "fukin", "fukk", "fukkah", "fukken", "fukker", "fukkin", "g00k", "gay", "gayboy", "gaygirl", "gays", "gayz", "h00r", "h0ar", "h0re", "hells", "hoar", "hoor", "hoore", "jackoff", "jap", "japs", "jerkoff", "jisim", "jiss", "jizm", "jizz", "knob", "knobs", "knobz", "kunt", "kunts", "kuntz", "lesbian", "lezzian", "lipshits", "lipshitz", "masochist", "masokist", "massterbait", "masstrbait", "masstrbate", "masterbaiter", "masterbate", "masterbates", "mothafucker", "mothafuker", "mothafukkah", "mothafukker", "motherfucker", "motherfukah", "motherfuker", "motherfukkah", "motherfukker", "muthafucker", "muthafukah", "muthafuker", "muthafukkah", "muthafukker", "n1gr", "nastt", "nigger", "nigur", "niiger", "niigr", "orafis", "orgasim", "orgasm", "orgasum", "oriface", "orifice", "orifiss", "packi", "packie", "packy", "paki", "pakie", "paky", "pecker", "peeenus", "peeenusss", "peenus", "peinus", "pen1s", "penas", "penis", "penus", "penuus", "phuc", "phuck", "phuk", "phuker", "phukker", "polac", "polack", "polak", "poonani", "pr1c", "pr1ck", "pr1k", "pusse", "pussee", "pussy", "puuke", "puuker", "queer", "queers", "queerz", "qweers", "qweerz", "qweir", "recktum", "rectum", "retard", "sadist", "scank", "schlong", "screwing", "semen", "sex", "skanck", "skank", "skankee", "skankey", "skanks", "skanky", "slut", "sluts", "slutty", "slutz", "tit", "turd", "va1jina", "vag1na", "vagiina", "vagina", "vaj1na", "vajina", "vullva", "vulva", "w0p", "wh00r", "wh0re", "whore", "bitch", "blowjob", "clit", "arschloch", "fuck", "shit", "asshole", "b17ch", "b1tch", "bastard", "bi+ch", "boiolas", "buceta", "c0ck", "cawk", "chink", "cipa", "clits", "cock", "cum", "cunt", "dildo", "dirsa", "ejakulate", "fatass", "fcuk", "fuk", "fux0r", "hoer", "hore", "jism", "kawk", "l3itch", "lesbian", "masturbate", "masterbat3", "motherfucker", "mofo", "nazi", "nigga", "nigger", "nutsack", "phuck", "pimpis", "pusse", "pussy", "scrotum", "shemale", "slut", "smut", "teets", "tits", "boobs", "b00bs", "teez", "testical", "testicle", "titt", "w00se", "jackoff", "wank", "whoar", "whore", "amcik", "andskota", "assrammer", "ayir", "bi7ch", "breasts", "cabron", "cazzo", "chraa", "chuj", "d4mn", "daygo", "dego", "dupa", "dziwka", "ekto", "enculer", "faen", "fag*", "fanculo", "fanny", "feces", "feg", "felcher", "ficken", "flikker", "foreskin", "fotze", "futkretzn", "gay", "gook", "guiena", "h0r", "h4x0r", "hell", "helvete", "honkey", "huevon", "hui", "injun", "jizz", "kike", "klootzak", "kraut", "knulle", "kuk", "kuksuger", "kurac", "kurwa", "lesbo", "mamhoon", "mibun", "monkleigh", "mouliewop", "muie", "mulkku", "muschi", "nazis", "nepesaurio", "orospu", "perse", "picka", "pimmel", "pizda", "poontsee", "poop", "porn", "p0rn", "pr0n", "preteen", "pula", "pule", "puta", "puto", "qahbeh", "rautenberg", "schaffer", "schlampe", "schmuck", "screw", "sharmuta", "sharmute", "shipal", "shiz", "skribz", "skurwysyn", "sphencter", "spic", "spierdalaj", "splooge", "suka", "twat", "vittu", "wichser", "yed", "zabourah"};
				String[] nobadwords = {"herobrine"};
				List <String> badwords = Arrays.asList(nobadwords);
				
				if (Main.censorHashTags == "true")
				{
					badwords = Arrays.asList(yesbadwords);
				}
				
				String lasttagmessage = null;
				String[] words = message.split(" ");
				for (int x = 0; x < words.length; x++)
				{
					String word = words[x].replaceAll("#", "");
					if (words[x].startsWith("#") && word.matches("^[a-zA-Z0-9!]*$") && !badwords.contains(word.toLowerCase())) //Ensure that the hashtag is alphanumeric or !, ?
					{
						tagmessage = words[x];
						message = message.replaceAll(tagmessage, Main.parseColor(Main.hashTagColor) + tagmessage + Main.parseColor("&r") + Main.parseColor(Main.chatColor));

				    	File file = new File(plugin.getDataFolder(), "hashtags.log");
				        if (!file.exists()) {
				        	file.createNewFile();
				        	BufferedWriter out = new BufferedWriter(new FileWriter(file));
				        	out.write(tagmessage);
				        	out.newLine();
				        	out.close();
				        }
				        else if (!tagmessage.equals(lasttagmessage)) //Don't add duplicate hashtags within line to log 
				        {
				        	BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
				        	out.write(tagmessage);
				        	out.newLine();
				        	out.close();
				        }
				        
						lasttagmessage = tagmessage;
					}
				}

				e.setMessage(message);
			}
		}
	}
	
	@EventHandler
	public void onSign(SignChangeEvent e)
	{
		String[] lines = e.getLines();
		int lc = 0;
		for (String l : lines)
		{
			if (l.contains(Main.playerTag) && Main.enableSignTags == "true")
			{
				if ((e.getPlayer().hasPermission("itags.signtag")) || (e.getPlayer().hasPermission("itags.*")) || (e.getPlayer().isOp()))
				{
					String[] words = l.split(" ");
					for (int x = 0; x < words.length; x++)
					{
						if (words[x].startsWith(Main.playerTag))
						{
							String tagmessage = words[x];
							tagmessage = tagmessage.replaceAll(Main.playerTag, "");
							Player taggedplayer = Bukkit.getPlayer(tagmessage);
							if (taggedplayer != null)
							{
								String taggedname = taggedplayer.getName();
								String playername = e.getPlayer().getName();
								
								if (Main.useDisplayNameColors == "true")
								{
									taggedname = taggedplayer.getDisplayName();
									playername = e.getPlayer().getDisplayName();
								}
									
								String newMessage = l.replaceAll(Main.playerTag + tagmessage, taggedname);
								
								e.setLine(lc, newMessage);
								
								if (Main.signNotifyType.equals("full"))
								{
					                  taggedplayer.sendMessage(ChatColor.GOLD + "[iTags] " + playername + ChatColor.GOLD + " just tagged you in a sign!");
					                  taggedplayer.sendMessage(ChatColor.GOLD + "----------------------");
					                  taggedplayer.sendMessage(ChatColor.GOLD + "-- " + ChatColor.RESET + e.getLine(0));
					                  taggedplayer.sendMessage(ChatColor.GOLD + "-- " + ChatColor.RESET + e.getLine(1));
					                  taggedplayer.sendMessage(ChatColor.GOLD + "-- " + ChatColor.RESET + e.getLine(2));
					                  taggedplayer.sendMessage(ChatColor.GOLD + "-- " + ChatColor.RESET + e.getLine(3));
					                  taggedplayer.sendMessage(ChatColor.GOLD + "----------------------");
								}
								else if (Main.signNotifyType.equals("mini"))
								{
					                  taggedplayer.sendMessage(ChatColor.GOLD + "[iTags] " + playername + ChatColor.GOLD + " just tagged you in a sign!");
								}
								else
								{
					                  return;
								}
								
								taggedplayer.getWorld().playSound(taggedplayer.getLocation(), Sound.valueOf(Main.signSound.toUpperCase()), 0.5F, 1.0F);
							}
						}
					}
				}
			}
			lc++;
		}
	}
}