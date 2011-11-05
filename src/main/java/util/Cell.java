package util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Cell {
	
	World world;
	Location location;
	boolean filled = false;
	Player player;

	public Cell(World world, Location location)
	{
		this.world = world;
		this.location = location;
	}
	
	public boolean isFilled()
	{
		return filled;
	}
	
	public void assignPlayer(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
}
