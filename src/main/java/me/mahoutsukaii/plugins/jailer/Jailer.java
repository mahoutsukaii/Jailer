package me.mahoutsukaii.plugins.jailer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import util.JailBuilding;

import com.nijikokun.bukkit.Permissions.Permissions;

public class Jailer extends JavaPlugin {

	public static boolean dev = false;

	public static final Logger log = Logger.getLogger("Minecraft");

	public Configuration properties = new Configuration(new File("plugins/Jailer/config.yml"));
	public String maindir = "plugins/Jailer/";

	public static boolean enableJail;
	public static boolean enableBroadcast;
	public static boolean allowBuild;
	public static boolean mutedJail;
	
	public static String broadcastJail;
	public static String failedLeave;
	public static String beenJailed;
	public static String releasedMsg;
	public static String vandalMessage;

	
	public int jailX;
	public int jailY;
	public int jailZ;
	public String jailWorld;

	public Location jailLocation;


	public ArrayList<String> jailedPlayers = new ArrayList<String>();
	public ArrayList<Long> jailTimes = new ArrayList<Long>();

	private final JailerPlayerListener playerListener = new JailerPlayerListener(this);
	private final JailerBlockListener blockListener = new JailerBlockListener(this);

	Permissions CurrentPermissions = null;

	public void setupPermissions() {
		Plugin plugin = this.getServer().getPluginManager().getPlugin("Permissions");

		if (CurrentPermissions == null) {
			// Permission plugin already registered
			return;
		}

		if (plugin != null) {
			CurrentPermissions = (Permissions) plugin;
		} else {
			log.log(Level.CONFIG, "[Jailer] Needs permissions plugin. disabling...");
			this.getServer().getPluginManager().disablePlugin(this);
		}
	}

	public void getStrings()
	{

		enableJail = properties.getNode("settings").getBoolean("enableJail", true);
		enableBroadcast = properties.getNode("settings").getBoolean("enableBroadcast", true);
		allowBuild = properties.getNode("settings").getBoolean("allowBuild", false);
		mutedJail = properties.getNode("settings").getBoolean("mutedJail", false);

		broadcastJail = properties.getNode("messages").getString("broadcastJail", "%player% was jailed by %admin% for %time%!");
		failedLeave = properties.getNode("messages").getString("failedLeave", "&aYou have not been released yet! You have %time% left!");
		beenJailed = properties.getNode("messages").getString("beenJailed", "You were jailed by %admin% for %time%.");
		releasedMsg = properties.getNode("messages").getString("released", "You are free to leave the jail.");
		vandalMessage = properties.getNode("messages").getString("vandalMessage", "&4You may not vandalise the jail!");
		this.jailX = properties.getNode("location").getInt("jailX", 0);
		this.jailY = properties.getNode("location").getInt("jailY", 0);
		this.jailZ = properties.getNode("location").getInt("jailZ", 0);
		this.jailWorld = properties.getNode("location").getString("jailWorld", "world");

		jailLocation = new Location(Bukkit.getServer().getWorld(jailWorld),jailX,jailY,jailZ);

	}

	public void saveConfig()
	{
		properties.load();
		properties.getNode("location").setProperty("jailX", this.jailX);
		properties.getNode("location").setProperty("jailY", this.jailY);
		properties.getNode("location").setProperty("jailZ", this.jailZ);
		properties.getNode("location").setProperty("jailWorld", this.jailWorld);
		properties.save();
	}
	public void onDisable() {
		// TODO: Place any custom disable code here.
		jailedPlayers.clear();
		jailTimes.clear();
		System.out.println(this + " is now disabled!");
	}

	public void onEnable() {
		// TODO: Place any custom enable code here, such as registering events
		new File(maindir).mkdir();

		createDefaultConfiguration("config.yml");
		properties.load();
		getStrings();
		if(!enableJail)
		{
			log.log(Level.CONFIG, "[Jailer] Disabled in config. disabling...");
			this.getServer().getPluginManager().disablePlugin(this);
		}
		jailedPlayers.clear();
		jailTimes.clear();
		PluginManager pm = getServer().getPluginManager();
		if(!allowBuild)
		{
			pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
			pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
			pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, playerListener, Priority.Normal, this);
			pm.registerEvent(Event.Type.PLAYER_BUCKET_FILL, playerListener, Priority.Normal, this);
		}
		if(mutedJail)
		{
			pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
		}
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Highest, this);
	//	pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		System.out.println(this + " is now enabled!");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		String commandName = command.getName().toLowerCase();
		String[] trimmedArgs = args;

		if(commandName.equals("setjail"))
		{ 
			return setJail(sender, trimmedArgs);
		}
		
		if(commandName.equals("jail"))
		{ 
			return jail(sender, trimmedArgs);
		}
		if(commandName.equals("bail"))
		{ 
			return bail(sender, trimmedArgs);
		}
		if(commandName.equals("buildjail"))
		{ 
			return buildJail(sender, trimmedArgs);
		}


		return false;

	}
	
	public boolean buildJail(CommandSender sender, String[] args)
	{
		Player player = null;
		boolean auth = false;


		if(sender instanceof Player)
		{
			player = (Player)sender;
			if (Permissions.Security.permission(player, "jailer.buildjail")) auth=true;
		}

		else
		{
			sender.sendMessage("Can't build from console...");
			return true;
		}

		if(dev)
			auth = true;

		if(!auth)
		{
			return false;
		}
		player = sender.getServer().getPlayer(sender.getName());
		JailBuilding jailBuilding = new JailBuilding(player.getWorld(), player.getLocation().getBlock().getX(), player.getLocation().getBlock().getY()-1, player.getLocation().getBlock().getZ(),0);
		jailBuilding.build();
		player.sendMessage(ChatColor.GREEN + "Jail built!");
		setJail(sender, args);
		return true;
	}

	public boolean setJail(CommandSender sender, String[] args)
	{

		Player player = null;
		boolean auth = false;


		if(sender instanceof Player)
		{
			player = (Player)sender;
			if (Permissions.Security.permission(player, "jailer.setjail")) auth=true;
		}

		else
		{
			sender.sendMessage("Can't set jail from console...");
			return true;
		}

		if(dev)
			auth = true;

		if(!auth)
		{
			return false;
		}

		this.jailX = player.getLocation().getBlockX();
		this.jailY = player.getLocation().getBlockY();
		this.jailZ = player.getLocation().getBlockZ();
		this.jailWorld = player.getLocation().getWorld().getName();

		jailLocation = new Location(Bukkit.getServer().getWorld(jailWorld),jailX,jailY,jailZ);

		saveConfig();
		sender.sendMessage(ChatColor.GREEN + "Jail has been set!");
		return true;
	}
	
	public boolean bail(CommandSender sender, String[] args)
	{

		Player player = null;
		boolean auth = false;


		if(sender instanceof Player)
		{
			player = (Player)sender;
			if (Permissions.Security.permission(player, "jailer.bail")) auth=true;
		}

		else
		{
			auth = true;
		}

		if(dev)
			auth = true;

		if(!auth)
		{
			return false;
		}
		
		boolean offline = false;
		String victim = expandName(args[0]);
		if(!jailedPlayers.contains(victim))
		{
			sender.sendMessage(ChatColor.RED +victim + " is not in jail!");
			return true;
		}
		
		int index = jailedPlayers.indexOf(victim);
		
		//jailedPlayers.remove(index);
		jailTimes.set(index, (long) 0);
		
		
		sender.sendMessage(ChatColor.GREEN + "You have bailed out " + expandName(args[0]));
		return true;
	}
	
	public boolean jail(CommandSender sender, String[] args)
	{

		Player player = null;
		boolean auth = false;


		if(sender instanceof Player)
		{
			player = (Player)sender;
			if (Permissions.Security.permission(player, "jailer.jail")) auth=true;
		}

		else
		{
			auth = true;
		}

		if(dev)
			auth = true;

		if(!auth)
		{
			return false;
		}
		
		if(args.length < 2)
		{
			return false;
		}
		
		Player victim = getServer().getPlayer(expandName(args[0]));
		
		if(victim == null)
		{
			sender.sendMessage(ChatColor.RED + args[0] + " is not online.");
			return true;
		}
		
	   	 String unparsedTime = args[1];
    	 long time = Long.parseLong(unparsedTime.replaceAll("[\\D]", ""));
    	 char unit = unparsedTime.charAt(unparsedTime.length() -1);
  
     
     
		long temptime = new Date().getTime();

		if(unit == 's')
			temptime = temptime + time * 1000;		
		if(unit == 'm')
			temptime = temptime + time * 1000 * 60;
		if(unit == 'h')
			temptime = temptime + time * 1000 * 60 * 60;
		if(unit == 'd')
			temptime = temptime + time * 1000 * 60 * 60 * 24;
		
		if(temptime <= new Date().getTime())
		{
			//sender.sendMessage("" + temptime);
			//sender.sendMessage("" + unit);
			sender.sendMessage(ChatColor.RED + "invalid time.");
			return true;
		}
		if(jailedPlayers.contains(victim.getName()))
		{
			sender.sendMessage(ChatColor.RED + victim.getName() + " is already in jail.");
			return true;
		}
		if(enableBroadcast)
		{
			String broadcastMsg = broadcastJail.replaceAll("%time%", getTimeDifference(temptime+10));
			broadcastMsg = broadcastMsg.replaceAll("%admin%", sender.getName());
			broadcastMsg = broadcastMsg.replaceAll("%player%",victim.getName());
			getServer().broadcastMessage(formatMessage(broadcastMsg));
		}
		sendToJail(victim, temptime);
		return true;
		
	}
	
	public void sendToJail(Player player, long jailTime)
	{
		jailedPlayers.add(player.getName());
		jailTimes.add(jailTime);
		
		
		player.teleport(jailLocation);
		String jailMsg = this.beenJailed.replaceAll("%time%",getTimeDifference(jailTime+1000) );
		player.sendMessage(formatMessage(jailMsg));
		
		
	}
	public String expandName(String Name) {
		int m = 0;
		String Result = "";
		for (int n = 0; n < getServer().getOnlinePlayers().length; n++) {
			String str = getServer().getOnlinePlayers()[n].getName();
			if (str.matches("(?i).*" + Name + ".*")) {
				m++;
				Result = str;
				if(m==2) {
					return null;
				}
			}
			if (str.equalsIgnoreCase(Name))
				return str;
		}
		if (m == 1)
			return Result;
		if (m > 1) {
			return null;
		}
		if (m < 1) {
			return Name;
		}
		return Name;
	}
	protected void createDefaultConfiguration(String name) {
		File actual = new File(getDataFolder(), name);
		if (!actual.exists()) {

			InputStream input =
					this.getClass().getResourceAsStream("/defaults/" + name);
			if (input != null) {
				FileOutputStream output = null;

				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length = 0;
					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}

					System.out.println(getDescription().getName()
							+ ": Default configuration file written: " + name);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (input != null)
							input.close();
					} catch (IOException e) {}

					try {
						if (output != null)
							output.close();
					} catch (IOException e) {}
				}
			}
		}
	}

	public String getTimeDifference(long tempTime)
	{

		long difference = tempTime - new Date().getTime();
		long timespace;
		String timespaceUnit = " day(s)";
		long timespacedays = difference / (1000*60*60*24);
		long timespacehours = difference / (1000*60*60);
		long timespaceminutes = difference / (1000*60);
		long timespaceseconds = difference / (1000);

		timespace = timespacedays;
		if(timespace < 1)
		{
			timespace = timespacehours;
			timespaceUnit = " hour(s)";
		}
		if(timespace < 1)
		{
			timespace = timespaceminutes;
			timespaceUnit = " minute(s)";
		}
		if(timespace < 1)
		{
			timespace = timespaceseconds;
			timespaceUnit = " second(s)";
		}

		return timespace + timespaceUnit;
	}

	public String formatMessage(String str){
		String funnyChar = new Character((char) 167).toString();
		str = str.replaceAll("&", funnyChar);
		return str;
	}

}
