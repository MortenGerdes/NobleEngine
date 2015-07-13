package com.game.engine.GameTypes.sillyslap.Worlds;

import java.util.ArrayList;

import org.bukkit.Location;

import com.game.engine.Game.Spawn;
import com.game.engine.Game.GameManagement.Game;
import com.game.engine.Game.GameManagement.GameManager;
import com.game.engine.World.HostWorld;

public class sillySlapWorld1 extends HostWorld
{
	private String _gameName;
	
	public sillySlapWorld1(String gameName, String world)
	{
		super(gameName, world);
		this._gameName = gameName;
	}
	
	@Override
	public void setTheSpawns()
	{
		ArrayList<Spawn> spawns = new ArrayList<Spawn>();
		
		if(_gameName == "SillySlap")
		{
		spawns.add(new Spawn(this, Game.toTeam("Player"), -23.5, 69, 5.5));
		}
		else if(_gameName == "SillySlapTeam")
		{
			spawns.add(new Spawn(this, Game.toTeam("Team Chicken Nugget"), -21.5, 69, 5.5));
			spawns.add(new Spawn(this, Game.toTeam("Team Fractis"), -23.5, 69, 5.5));
			spawns.add(new Spawn(this, Game.toTeam("Team Memory Leak"), -25.5, 69, 5.5));
		}
		setDefaultSpawnLocation(new Location(getWorld(), -23.5, 87, 5.5));
		setSpawns(spawns);
		
		GameManager.getCurrentGameExtender().setCorner1(new Location(this.getWorld(), 2.5,86,38.5));
		GameManager.getCurrentGameExtender().setCorner2(new Location(this.getWorld(), -55,63,-27));
	}
}