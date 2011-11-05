package util;


import me.mahoutsukaii.plugins.jailer.Jailer;

import org.bukkit.Location;
import org.bukkit.entity.Player;


public class RespawnHandler {
	
	Jailer plugin;
	
	public void initialise(Jailer instance)
	{
		this.plugin = instance;
	}
	
	public Location findCell(Player player)
	{
		for(int i = 0; i < plugin.cells.size(); i++)
		{
			if(plugin.cells.get(i).getPlayer().equals(player))
				return plugin.cells.get(i).location;
		}
		return null;
		
	}

}
