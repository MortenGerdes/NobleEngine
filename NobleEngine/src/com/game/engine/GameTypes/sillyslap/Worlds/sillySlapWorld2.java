package com.game.engine.GameTypes.sillyslap.Worlds;

import java.util.ArrayList;

import org.bukkit.Location;

import com.game.engine.Game.Spawn;
import com.game.engine.Game.GameManagement.Game;
import com.game.engine.Game.GameManagement.GameManager;
import com.game.engine.World.HostWorld;

public class sillySlapWorld2 extends HostWorld
{
	private String _gameName;
	
	public sillySlapWorld2(String gameName, String world)
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
			spawns.add(new Spawn(this, Game.toTeam("Player"), 53.5, 19, 49.5));
		}
		else if(_gameName == "SillySlapTeam")
		{
			spawns.add(new Spawn(this, Game.toTeam("Team Chicken Nugget"), 52.5, 19, 49.5));
			spawns.add(new Spawn(this, Game.toTeam("Team Fractis"), 56.5, 19, 52.5));
			spawns.add(new Spawn(this, Game.toTeam("Team Memory Leak"), 52.5, 19, 55.5));
			
		}
		setDefaultSpawnLocation(new Location(getWorld(), 53.5, 25, 49.5));
		setSpawns(spawns);
		
		GameManager.getCurrentGameExtender().setCorner1(new Location(this.getWorld(), 36.5,23.5,34));
		GameManager.getCurrentGameExtender().setCorner2(new Location(this.getWorld(), 70,8.1,70.5));
	}
}
