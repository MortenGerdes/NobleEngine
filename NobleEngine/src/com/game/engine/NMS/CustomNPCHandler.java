package com.game.engine.NMS;

import java.util.ArrayList;

import net.minecraft.server.v1_8_R2.World;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.game.engine.GameEngine;
import com.game.engine.NMS.types.customZombie;

public class CustomNPCHandler
{
	private static CustomNPCHandler instance = null;
	private static BukkitTask testing;
	
	private ArrayList<customZombie> _entities = new ArrayList<customZombie>();

	private CustomNPCHandler()
	{
		TPBackifMoved();
	}

	public static CustomNPCHandler getInstance()
	{
		if (instance == null)
		{
			instance = new CustomNPCHandler();
		}
		return instance;
	}

	public customZombie returnHandledZombie(String name, Location location, World world)
	{
		customZombie zombie = new customZombie(world, location);
		
		zombie.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
		zombie.setCustomNameVisible(true);
		zombie.setPosition(location.getX(), location.getY(), location.getZ());
		((Zombie) zombie.getBukkitEntity()).setRemoveWhenFarAway(false);
		
		addEntitytoArrayList(zombie);
		return zombie;
	}

	private void TPBackifMoved()
	{
		testing = new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if (!getEntities().isEmpty())
				{
					for (customZombie mobs : getEntities())
					{
						if (!mobs.getBukkitEntity().getLocation().equals(mobs.getLocation()))
						{
							mobs.getBukkitEntity().teleport(mobs.getLocation());
						}
					}
				}
			}
		}.runTaskTimer(GameEngine.GetPlugin(), 20 * 4, 20 * 2);
	}

	public void addEntitytoArrayList(customZombie entity)
	{
		GameEngine.Debug("Adding entity " + entity.getBukkitEntity().getType().getName() + " " + entity.getCustomName());
		_entities.add(entity);
	}

	public void removeEntityfromArrayList(customZombie entity)
	{
		_entities.remove(entity);
	}

	public void removeAllEntitiesfromArrayList()
	{
		removeAllCustomEntity();
		_entities.clear();
	}

	public void removeAllCustomEntity()
	{
		for (customZombie e : _entities)
		{
			GameEngine.Debug("Removed entity: " + e.getCustomName()); // Fix manager problem
			e.getBukkitEntity().remove();
		}
	}

	public void removeAllEntity()
	{
		for (Entity e : GameEngine.getCurrentGame().getLobby().getWorld().getEntities())
		{
			if (e instanceof Monster)
			{
				GameEngine.Debug("Removed entity: " + e.getCustomName());
				e.remove();
			}
		}
	}

	public ArrayList<customZombie> getEntities()
	{
		return _entities;
	}
}
