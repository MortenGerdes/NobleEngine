package com.game.engine.Game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.game.engine.GameEngine;
import com.game.engine.Util.WorldUtil;
import com.game.engine.World.CustomWorldType;

public abstract class GameWorld implements Listener
{
	private World world;
	private String name;
	private CustomWorldType type;

	private String location;

	public GameWorld(String GameName, String worldName, WorldType worldType)
	{
		if(worldName == "NobleLobby")
		{
			if(world == null)
			{
				world = Bukkit.getServer().createWorld(new WorldCreator(worldName));
			}
		}

		location = GameEngine.GetPlugin().getDataFolder() + File.separator + GameName + File.separator + "maps";
		new File(location).mkdirs();
		
		location = GameEngine.GetPlugin().getDataFolder() + File.separator + GameName + File.separator + "maps" +  File.separator + worldName;
		if (world != null)
		{
			world = Bukkit.getWorld(worldName);
		}

		name = worldName;
		GameEngine.Debug("Creating " + name + " world.");
	}
	
	public abstract void setSpawnPointsFromHostWorld();

	public GameWorld(String GameName, String worldName)
	{
		this(GameName, worldName, WorldType.FLAT);
	}

	public GameWorld gameWorld()
	{
		return this;
	}

	public void load(){
		WorldUtil.createWorld(new File(location), name);
		this.world = Bukkit.getWorld(name);
	}

	public void unload(){
		WorldUtil.deleteWorld(name);
		world = null;
	}

	public void Teleport(Player player)
	{
		player.teleport(world.getSpawnLocation().add(0, 2, 0));
	}

	public void TeleportToLocation(Player player, Location location)
	{
		player.teleport(location);
	}

	public boolean validateEntity(Entity entity)
	{
		if(onWorld(entity))
		{
			if(entity instanceof Player)
			{
				if(!GameEngine.getCurrentGame().GetSpectators().contains(entity))
				{
					return true;
				}
			}
			else return true;
		}
		return false;
	}

	public boolean onWorld(Entity entity)
	{
		return entity.getWorld().equals(world);
	}

	public boolean onWorld(Player player)
	{
		return player.getWorld().equals(world);
	}

	public int onlinePlayers()
	{
		return world.getPlayers().size();
	}
	
	public void unRegister()
	{
		HandlerList.unregisterAll(this);
	}

	public List<Player> getPlayerList()
	{
		return world.getPlayers();
	}

	public int getPlayerSize()
	{
		return world.getPlayers().size();
	}

	public Location getDefaultSpawn()
	{
		return world.getSpawnLocation();
	}

	public World getWorld()
	{
		return world;
	}

	public String getName()
	{
		return name;
	}

	public CustomWorldType getType()
	{
		return type;
	}
	
	public void setDefaultSpawnLocation(Location location)
	{
		world.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
	
	public void setSpawns(ArrayList<Spawn> theSpawns)
	{
		GameManager.getCurrentGameExtender().getSpawns().clear();
		for(Spawn spawn: theSpawns)
		{
			Bukkit.broadcastMessage("Spawns added into the GameWorld for world " + spawn.GetLocation().getWorld().getName());
			//this.spawns.add(spawn);
			GameManager.getCurrentGameExtender().addSpawn(spawn);
		}
	}

	public void setType(CustomWorldType type)
	{
		this.type = type;
	}

	public void setWorld(String name)
	{
		this.world = Bukkit.getWorld(name);
	}
	
	public File getPluginFolderLocation(){
		return new File(location);
	}
}
