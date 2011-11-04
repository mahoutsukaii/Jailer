package util;

import org.bukkit.Location;
import org.bukkit.World;

public class Cell {
	
	World world;
	Location location;
	boolean filled = false;

	public Cell(World world, Location location)
	{
		this.world = world;
		this.location = location;
	}
	
	public boolean isFilled()
	{
		return filled;
	}
	
	
	
}
