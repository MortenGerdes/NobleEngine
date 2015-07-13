package com.game.engine.GameTypes.noblespleef.Worlds;

import java.util.ArrayList;

import org.bukkit.Location;

import com.game.engine.Game.Spawn;
import com.game.engine.Game.GameManagement.Game;
import com.game.engine.Game.GameManagement.GameManager;
import com.game.engine.World.HostWorld;

public class NobleSpleefWorld1 extends HostWorld
{
	public NobleSpleefWorld1(String gameName, String world)
	{
		super(gameName, world);
	}
	
	@Override
	public void setTheSpawns()
	{
		ArrayList<Spawn> spawns = new ArrayList<Spawn>();
		
		spawns.add(new Spawn(this, Game.toTeam("Player"), -51.5, 100, 29.5));
		spawns.add(new Spawn(this, Game.toTeam("Player"), -51.5, 100, 19.5));

		setSpawns(spawns);
		setDefaultSpawnLocation(new Location(getWorld(), -23, 114, 22));
		setMapCreator("CoolSpear1");
		
		GameManager.getCurrentGameExtender().setCorner1(new Location(this.getWorld(), -65,85,48));
		GameManager.getCurrentGameExtender().setCorner2(new Location(this.getWorld(), 5,118,-11));
	}
}
