package com.game.engine.World;

import org.bukkit.Bukkit;
import org.bukkit.WorldType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.game.engine.GameEngine;
import com.game.engine.Game.GameManagement.GameWorld;

public abstract class HostWorld extends GameWorld
{
	private String _worldName;
	
	public HostWorld(String GameName, String name)
	{
		super(GameName, name, WorldType.FLAT);
		this._worldName = name;
		setType(CustomWorldType.INGAME);
	}
	
	public abstract void setTheSpawns();
	
	@Override
	public void setSpawnPointsFromHostWorld()
	{
		setTheSpawns();
	}
		
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		if (onWorld(event.getEntity()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onChunckUnload(ChunkUnloadEvent event)
	{
		if (event.getChunk().getWorld().equals(getWorld()))
		{
			event.setCancelled(true);
		}
	}
}
