package com.game.engine.Game.GameManagement;

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
import com.game.engine.Chat.Chat;
import com.game.engine.Game.Spawn;
import com.game.engine.Util.WorldUtil;
import com.game.engine.World.CustomWorldType;

public abstract class GameWorld implements Listener
{
	private String _name;
	private String _location;
	private String _mapCreator;
	private World _world;
	private CustomWorldType _type;

	public GameWorld(String GameName, String worldName, WorldType worldType)
	{
		if (worldName == "NobleLobby")
		{
			if (_world == null)
			{
				_world = Bukkit.getServer().createWorld(new WorldCreator(worldName));
			}
		}
		_location = GameEngine.GetPlugin().getDataFolder() + File.separator + GameName + File.separator + "maps";
		new File(_location).mkdirs();
		_location = GameEngine.GetPlugin().getDataFolder() + File.separator + GameName + File.separator + "maps" + File.separator + worldName;
		if (_world != null)
		{
			_world = Bukkit.getWorld(worldName);
		}
		_name = worldName;
		GameEngine.Debug("Creating " + _name + " world.");
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

	public void load()
	{
		WorldUtil.createWorld(new File(_location), _name);
		this._world = Bukkit.getWorld(_name);
	}

	public void unload()
	{
		WorldUtil.deleteWorld(_name);
		_world = null;
	}

	public void teleport(Player player)
	{
		player.teleport(_world.getSpawnLocation().add(0, 2, 0));
	}

	public void teleportToLocation(Player player, Location location)
	{
		player.teleport(location);
	}
	
	public void setWorldTime(final long time, boolean lockTime)
	{
		_world.setTime(time);
		
		if(lockTime == true)
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					if(GameEngine.getCurrentGame().getState() == GameState.PREPARING || GameEngine.getCurrentGame().getState() == GameState.STARTED)
					{
						_world.setTime(time);
					}
				}
			}.runTaskTimer(GameEngine.GetPlugin(), 0, 20);
		}
	}

	public void setWorldRain(boolean condition)
	{
		if(condition == false)
		{
			_world.setStorm(false);
			_world.setThundering(false);
			_world.setWeatherDuration(99999);
		}
		else
		{
			_world.setStorm(true);
			_world.setWeatherDuration(99999);
		}
	}
	
	public boolean validateSpectator(Entity entity)
	{
		if (onWorld(entity))
		{
			if (entity instanceof Player)
			{
				if (!GameEngine.getCurrentGame().getSpectators().contains(entity))
				{
					return true;
				}
			}
			else
				return true;
		}
		return false;
	}

	public boolean onWorld(Entity entity)
	{
		return entity.getWorld().equals(_world);
	}

	public boolean onWorld(Player player)
	{
		return player.getWorld().equals(_world);
	}

	public int onlinePlayers()
	{
		return _world.getPlayers().size();
	}

	public void unRegister()
	{
		HandlerList.unregisterAll(this);
	}

	public List<Player> getPlayerList()
	{
		return _world.getPlayers();
	}

	public int getPlayerSize()
	{
		return _world.getPlayers().size();
	}

	public Location getDefaultSpawn()
	{
		return _world.getSpawnLocation();
	}
	
	public String getMapCreator()
	{
		if(_mapCreator != null)
		{
			return _mapCreator;
		}
		else
		{
			return "Unknown";
		}
	}

	public World getWorld()
	{
		return _world;
	}

	public String getName()
	{
		return _name;
	}

	public CustomWorldType getType()
	{
		return _type;
	}

	public void setDefaultSpawnLocation(Location location)
	{
		_world.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public void setSpawns(ArrayList<Spawn> theSpawns)
	{
		GameManager.getCurrentGameExtender().getSpawns().clear();
		for (Spawn spawn : theSpawns)
		{
			Chat.devMessage("Spawns added into the GameWorld for world " + spawn.GetLocation().getWorld().getName());
			//this.spawns.add(spawn);
			GameManager.getCurrentGameExtender().addSpawn(spawn);
		}
	}

	public void setType(CustomWorldType type)
	{
		this._type = type;
	}

	public void setWorld(String name)
	{
		this._world = Bukkit.getWorld(name);
	}
	
	public void setMapCreator(String name)
	{
		this._mapCreator = name;
	}

	public File getPluginFolderLocation()
	{
		return new File(_location);
	}
}
