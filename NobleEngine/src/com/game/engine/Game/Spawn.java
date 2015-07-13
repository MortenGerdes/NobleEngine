package com.game.engine.Game;

import org.bukkit.Location;

import com.game.engine.Chat.Chat;
import com.game.engine.Game.GameManagement.GameWorld;

public class Spawn
{
	private boolean _isSpawnTaken = false;
	private Team _team;
	private Location _location;

	public Spawn(GameWorld world, Team team, double x, double y, double z)
	{
		if(world.getWorld() == null)
		{
			Chat.devMessage("the world is Null");
		}
		this._team = team;
		this._location = new Location(world.getWorld(), x, y, z);
	}

	public Team GetTeam()
	{
		return _team;
	}

	public Location GetLocation()
	{
		if(_location.getWorld() == null)
			Chat.devMessage("the world is Null2");
		return _location;
	}
	
	public void setIsTaken(boolean isTaken)
	{
		this._isSpawnTaken = isTaken;
	}
	
	public boolean getIsTaken()
	{
		return _isSpawnTaken;
	}
}
