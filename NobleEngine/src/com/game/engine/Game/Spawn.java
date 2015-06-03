package com.game.engine.Game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.game.engine.GameEngine;

public class Spawn
{
	private Team team;
	private Location location;
	private boolean isTaken = false;

	public Spawn(GameWorld world, Team team, double x, double y, double z)
	{
		this.team = team;
		if(world.getWorld() == null)
			Bukkit.broadcastMessage("the world is Null");
		this.location = new Location(world.getWorld(), x, y, z);
	}

	public Team GetTeam()
	{
		return team;
	}

	public Location GetLocation()
	{
		if(location.getWorld() == null)
			Bukkit.broadcastMessage("the world is Null2");
		return location;
	}
	
	public void setIsTaken(boolean isTaken)
	{
		this.isTaken = isTaken;
	}
	
	public boolean getIsTaken()
	{
		return isTaken;
	}
}
