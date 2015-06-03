package com.game.engine.GameTypes.gemhunt.world;

import java.util.ArrayList;
import com.game.engine.Game.Game;
import com.game.engine.Game.Spawn;
import com.game.engine.World.HostWorld;

public class gemhuntWorld extends HostWorld
{
	public gemhuntWorld(String gameName, String world)
	{
		super(gameName, world);
	}
	
	@Override
	public void setTheSpawns()
	{
		ArrayList<Spawn> spawns = new ArrayList<Spawn>();
		
		spawns.add(new Spawn(this, Game.toTeam("Blue Team"), -6.5, 5, -8.5));
		
		spawns.add(new Spawn(this, Game.toTeam("Red Team"), -4.5, 5, 19));
		
		setSpawns(spawns);
	}
}