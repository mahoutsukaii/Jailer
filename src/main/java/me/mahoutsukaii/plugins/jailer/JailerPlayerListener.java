package me.mahoutsukaii.plugins.jailer;



import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;



public class JailerPlayerListener extends PlayerListener {


	
	Jailer plugin;
	
	public JailerPlayerListener(Jailer instance)
	{
		this.plugin = instance;
	}


	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		
		if(plugin.jailedPlayers.contains(player.getName()))
		{
			int index = plugin.jailedPlayers.indexOf(player.getName());
			long tempTime = plugin.jailTimes.get(index);
			String failMsg = plugin.failedLeave;
			failMsg = failMsg.replaceAll("%time%", plugin.getTimeDifference(tempTime));
			player.sendMessage(plugin.formatMessage(failMsg));
			event.setTo(plugin.jailLocation);
			
		}
	}


	/* (non-Javadoc)
	 * @see org.bukkit.event.player.PlayerListener#onPlayerChat(org.bukkit.event.player.PlayerChatEvent)
	 */
	@Override
	public void onPlayerChat(PlayerChatEvent event) {

		Player player = event.getPlayer();
		if(plugin.jailedPlayers.contains(player.getName()));
		{
			player.sendMessage(ChatColor.DARK_RED + "You cannot speak in the jail!");
			event.setCancelled(true);
		}
	}


	public void onPlayerMove(PlayerMoveEvent event) {
		
		if(plugin.jailedPlayers.contains(event.getPlayer().getName()))
		{

			Player player = event.getPlayer();


			int index = plugin.jailedPlayers.indexOf(player.getName());
			long tempTime = plugin.jailTimes.get(index);

			if(tempTime <= new Date().getTime())
			{
				player.sendMessage(plugin.formatMessage(plugin.releasedMsg));
				plugin.jailedPlayers.remove(index);
				plugin.jailTimes.remove(index);
			}

		}

	}


	
	public void onPlayerInteract(PlayerInteractEvent event) {
		

	
				Block block = event.getClickedBlock();
				Player player = event.getPlayer();
				player.sendMessage(block.getTypeId() + ":" + block.getData());
				
	}
	
	



	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		if(plugin.jailedPlayers.contains(event.getPlayer().getName()))
		{
			event.getPlayer().sendMessage(plugin.formatMessage(plugin.vandalMessage));
			event.setCancelled(true);
		}
		
	}


	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		if(plugin.jailedPlayers.contains(event.getPlayer().getName()))
		{
			event.getPlayer().sendMessage(plugin.formatMessage(plugin.vandalMessage));
			event.setCancelled(true);
		}
		
	}


	public void onPlayerRespawn(PlayerRespawnEvent event) {
			Player player = event.getPlayer();
			
			if(plugin.jailedPlayers.contains(player.getName()))
			{
				event.setRespawnLocation(plugin.jailLocation);
			}
			
	}
	
	
	
		

}
