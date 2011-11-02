package util;

import me.mahoutsukaii.plugins.jailer.Jailer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class JailBuilding extends Jailer {
	
	World world;
	int x;
	int y;
	int z;
	int dir;
	
	public JailBuilding(World world, int x, int y, int z, int dir)
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.dir = dir;
		
	}
	
	public void build()
	{
		Point cornerA = new Point(x-5,y,z-5);
		Point cornerB = new Point(x-5,y,z+5);
		Point cornerC = new Point(x+5,y,z-5);
		Point cornerD = new Point(x+5,y,z+5);
		
		int workingX;
		int workingY;
		int workingZ;
		int height = 5;
		Location workingLocation;
		
		//set everything to air first!
		
		for(workingY = y; workingY < y + height; workingY++)
		{
			for(workingX = x-5; workingX <= x+5; workingX++)
			{
				for(workingZ = z-5; workingZ <= z+5;workingZ++)
				{
					workingLocation = new Location(world, workingX, workingY, workingZ);
					workingLocation.getBlock().setTypeId(0);
				}
			}
		}

		for(workingY = y; workingY < y + height; workingY++)
		{
			for(workingX = cornerA.x; workingX <= cornerC.x; workingX++)
			{
				workingLocation = new Location(world, workingX, workingY, cornerA.z);
				workingLocation.getBlock().setTypeId(98);
			}

			for(workingX = cornerB.x; workingX <= cornerD.x; workingX++)
			{
				workingLocation = new Location(world, workingX, workingY, cornerB.z);
				workingLocation.getBlock().setTypeId(98);
			}

			for(workingZ = cornerA.z; workingZ <= cornerB.z; workingZ++)
			{
				workingLocation = new Location(world, cornerA.x, workingY, workingZ);
				workingLocation.getBlock().setTypeId(98);
			}
			for(workingZ = cornerC.z; workingZ <= cornerD.z; workingZ++)
			{
				workingLocation = new Location(world, cornerC.x, workingY, workingZ);
				workingLocation.getBlock().setTypeId(98);
			}
		}
		
		//floor and roof;
		
		for(workingX = x - 4; workingX <= x + 4; workingX++)
		{
			for(workingZ = z -4; workingZ <= z + 4; workingZ++)
			{
				workingLocation = new Location(world, workingX, y, workingZ);
				workingLocation.getBlock().setTypeId(1);
			}
				
		}
		for(workingX = x - 4; workingX <= x + 4; workingX++)
		{
			for(workingZ = z -4; workingZ <= z + 4; workingZ++)
			{
				workingLocation = new Location(world, workingX, y+height-1, workingZ);
				workingLocation.getBlock().setTypeId(1);
			}
				
		}
		
		//windows and a door;
		 
		workingLocation = new Location(world, x,y + 1,z-5);
		workingLocation.getBlock().setTypeId(71);
		workingLocation.getBlock().setData((byte) 1);
		
		workingLocation = new Location(world, x,y + 2,z-5);
		workingLocation.getBlock().setTypeId(71);
		workingLocation.getBlock().setData((byte) 9);
		
		
		//bars next to door
		
		workingLocation = new Location(world, x-2,y + 2,z-5);
		workingLocation.getBlock().setTypeId(101);
		
		workingLocation = new Location(world, x+2,y + 2,z-5);
		workingLocation.getBlock().setTypeId(101);
		
		//cells
		for(workingY = y+1; workingY < y+height -1; workingY++)
		{
			for(workingX = x + 2; workingX < x + 5; workingX++)
			{
				workingLocation = new Location(world, workingX, workingY, z-2);
				workingLocation.getBlock().setTypeId(101);
			}
		}
		for(workingY = y+1; workingY < y+height -1; workingY++)
		{
			for(workingX = x + 2; workingX < x + 5; workingX++)
			{
				workingLocation = new Location(world, workingX, workingY, z+2);
				workingLocation.getBlock().setTypeId(101);
			}
		}
		
		for(workingY = y+1; workingY < y+height -1; workingY++)
		{
			for(workingX = x - 2; workingX > x - 5; workingX--)
			{
				workingLocation = new Location(world, workingX, workingY, z-2);
				workingLocation.getBlock().setTypeId(101);
			}
		}
		
		for(workingY = y+1; workingY < y+height -1; workingY++)
		{
			for(workingX = x - 2; workingX > x - 5; workingX--)
			{
				workingLocation = new Location(world, workingX, workingY, z+2);
				workingLocation.getBlock().setTypeId(101);
			}
		}
		
		for(workingY = y+1; workingY < y+height -1; workingY++)
		{
			for(workingZ = z - 4; workingZ < z + 5; workingZ++)
			{
				workingLocation = new Location(world, x-1, workingY, workingZ);
				workingLocation.getBlock().setTypeId(101);
			}
		}
		for(workingY = y+1; workingY < y+height -1; workingY++)
		{
			for(workingZ = z - 4; workingZ < z + 5; workingZ++)
			{
				workingLocation = new Location(world, x+1, workingY, workingZ);
				workingLocation.getBlock().setTypeId(101);
			}
		}
		
		workingLocation = new Location(world, x+1, y+1, z+3);
		workingLocation.getBlock().setTypeId(0);
		workingLocation = new Location(world, x+1, y+2, z+3);
		workingLocation.getBlock().setTypeId(0);
		
		workingLocation = new Location(world, x+1, y+1, z);
		workingLocation.getBlock().setTypeId(0);
		workingLocation = new Location(world, x+1, y+2, z);
		workingLocation.getBlock().setTypeId(0);
		
		workingLocation = new Location(world, x+1, y+1, z-3);
		workingLocation.getBlock().setTypeId(0);
		workingLocation = new Location(world, x+1, y+2, z-3);
		workingLocation.getBlock().setTypeId(0);
		
		workingLocation = new Location(world, x-1, y+1, z+3);
		workingLocation.getBlock().setTypeId(0);
		workingLocation = new Location(world, x-1, y+2, z+3);
		workingLocation.getBlock().setTypeId(0);
		
		workingLocation = new Location(world, x-1, y+1, z);
		workingLocation.getBlock().setTypeId(0);
		workingLocation = new Location(world, x-1, y+2, z);
		workingLocation.getBlock().setTypeId(0);
		
		workingLocation = new Location(world, x-1, y+1, z-3);
		workingLocation.getBlock().setTypeId(0);
		workingLocation = new Location(world, x-1, y+2, z-3);
		workingLocation.getBlock().setTypeId(0);
		
		
		//back window:
		workingLocation = new Location(world, x-1, y+2, z+5);
		workingLocation.getBlock().setTypeId(101);
		workingLocation = new Location(world, x, y+2, z+5);
		workingLocation.getBlock().setTypeId(101);
		workingLocation = new Location(world, x+1, y+2, z+5);
		workingLocation.getBlock().setTypeId(101);
		
		//torches!
		
		//fuck that
		
	}

}
