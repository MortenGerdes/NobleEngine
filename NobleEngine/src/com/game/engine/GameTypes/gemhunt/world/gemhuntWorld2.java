package com.game.engine.GameTypes.gemhunt.world;

import java.util.ArrayList;

import com.game.engine.Game.Game;
import com.game.engine.Game.Spawn;
import com.game.engine.World.HostWorld;

public class gemhuntWorld2 extends HostWorld
{
	public gemhuntWorld2(String gameName, String world)
	{
		super(gameName, world);
	}
	
	@Override
	public void setTheSpawns()
	{
		ArrayList<Spawn> spawns = new ArrayList<Spawn>();
		
	    spawns.add(new Spawn(this, Game.toTeam("Blue Team"), 1.5, 5.0D, 0.5));
	    
	    spawns.add(new Spawn(this, Game.toTeam("Red Team"), 2.5, 5.0, 20.5));
	    
		setSpawns(spawns);

	}
}
