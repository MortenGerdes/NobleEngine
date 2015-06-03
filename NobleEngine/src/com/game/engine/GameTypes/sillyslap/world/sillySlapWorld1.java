package com.game.engine.GameTypes.sillyslap.world;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.game.engine.GameEngine;
import com.game.engine.Game.Game;
import com.game.engine.Game.Spawn;
import com.game.engine.World.HostWorld;

public class sillySlapWorld1 extends HostWorld
{
	private static Location point1 = new Location(GameEngine.getCurrentGame().GetHost().getWorld(), -55.5,87.5,-28.6);
	private static Location point2 = new Location(GameEngine.getCurrentGame().GetHost().getWorld(), 12,63,40);
	
	public sillySlapWorld1(String gameName, String world)
	{
		super(gameName, world);
	}
	
	@Override
	public void setTheSpawns()
	{
		ArrayList<Spawn> spawns = new ArrayList<Spawn>();
		
		spawns.add(new Spawn(this, Game.toTeam("Player"), -23.5, 67, 5.5));
		
		setSpawns(spawns);
	}
	
	public static Location getPoint1()
	{
		return point1;
	}
	
	public static Location getPoint2()
	{
		return point2;
	}
}