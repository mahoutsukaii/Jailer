package me.mahoutsukaii.plugins.jailer;

import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class JailerBlockListener extends BlockListener{
	
	Jailer plugin;

	public JailerBlockListener(Jailer instance) {
		this.plugin = instance;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.event.block.BlockListener#onBlockBreak(org.bukkit.event.block.BlockBreakEvent)
	 */
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		
		if(plugin.jailedPlayers.contains(event.getPlayer().getName()))
		{
			event.getPlayer().sendMessage(plugin.formatMessage(plugin.vandalMessage));
			event.setCancelled(true);
		}

	}

	/* (non-Javadoc)
	 * @see org.bukkit.event.block.BlockListener#onBlockPlace(org.bukkit.event.block.BlockPlaceEvent)
	 */
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if(plugin.jailedPlayers.contains(event.getPlayer().getName()))
		{
			event.getPlayer().sendMessage(plugin.formatMessage(plugin.vandalMessage));
			event.setCancelled(true);
		}
		
		
		
	}
	
	
	
	

	
	
}
