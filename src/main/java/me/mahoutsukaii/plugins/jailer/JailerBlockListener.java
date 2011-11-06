package me.mahoutsukaii.plugins.jailer;

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
			if(!Jailer.allowBuild)
			{
				event.getPlayer().sendMessage(plugin.formatMessage(Jailer.vandalMessage));
				event.setCancelled(true);
			}
			else
			{
				if(event.getBlock().getTypeId() == 1 |  
						event.getBlock().getTypeId() == 98 |
						event.getBlock().getTypeId() == 101 |
						event.getBlock().getTypeId() == 71)
					
				plugin.getServer().broadcastMessage(plugin.formatMessage(Jailer.escapedMessage.replaceAll("%player%", event.getPlayer().getDisplayName())));
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.bukkit.event.block.BlockListener#onBlockPlace(org.bukkit.event.block.BlockPlaceEvent)
	 */
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if(plugin.jailedPlayers.contains(event.getPlayer().getName())) 
		{
			if(!Jailer.allowBuild)
			{
				event.getPlayer().sendMessage(plugin.formatMessage(Jailer.vandalMessage));
				event.setCancelled(true);
			}
			
		}
		
	}
	
	
	
	

	
	
}
