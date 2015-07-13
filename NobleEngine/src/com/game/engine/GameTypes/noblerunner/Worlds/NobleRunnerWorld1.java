package com.game.engine.GameTypes.noblerunner.Worlds;

import java.util.ArrayList;

import org.bukkit.Location;

import com.game.engine.Game.Spawn;
import com.game.engine.Game.GameManagement.Game;
import com.game.engine.Game.GameManagement.GameManager;
import com.game.engine.World.HostWorld;

public class NobleRunnerWorld1 extends HostWorld
{
	public NobleRunnerWorld1(String gameName, String world)
	{
		super(gameName, world);
		
	}

	@Override
	public void setTheSpawns()
	{
		ArrayList<Spawn> spawns = new ArrayList<Spawn>();
		
		spawns.add(new Spawn(this, Game.toTeam("Player"), -62.5, 62, -39.5));
		spawns.add(new Spawn(this, Game.toTeam("Player"), -62.5, 62, -30.5));

		setSpawns(spawns);
		setDefaultSpawnLocation(new Location(getWorld(), -34, 76, -32));
		
		GameManager.getCurrentGameExtender().setCorner1(new Location(this.getWorld(), -65,85,48));
		GameManager.getCurrentGameExtender().setCorner2(new Location(this.getWorld(), 5,118,-11));
		
		setWorldRain(false);
		setWorldTime(0, true);
		setMapCreator("CoolSpear1");
		

	}
	
	
}
