package com.game.engine.World;

import org.bukkit.Sound;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.game.engine.GameEngine;
import com.game.engine.Game.GameState;
import com.game.engine.Game.GameWorld;

public abstract class HostWorld extends GameWorld
{
	public HostWorld(String GameName, String name)
	{
		super(GameName, name, WorldType.FLAT);
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

	@EventHandler
	public void entityGrief(EntityChangeBlockEvent e)
	{
		if(GameEngine.getCurrentGame().getState() == GameState.STARTED)
		{
			return;
		}

		if (onWorld(e.getEntity()))
		{
			if (e.getEntity() instanceof LivingEntity)
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void blockBlockBreak(BlockBreakEvent e)
	{
		if(GameEngine.getCurrentGame().getState() == GameState.STARTED)
		{
			return;
		}

		Player player = e.getPlayer();
		if (onWorld(player))
			e.setCancelled(true);
	}

	@EventHandler
	public void blockBlockPlace(BlockPlaceEvent e)
	{
		if(GameEngine.getCurrentGame().getState() == GameState.STARTED)
		{
			return;
		}

		Player player = e.getPlayer();
		if (onWorld(player))
			e.setCancelled(true);
	}

	@EventHandler
	public void combatProtection(EntityDamageEvent e)
	{
		if(GameEngine.getCurrentGame().getState() == GameState.STARTED)
		{
			return;
		}
		if (!onWorld(e.getEntity()))
		{
			return;
		}
		if (e.getEntity() instanceof Entity)
		{
			if (onWorld(e.getEntity()))
				e.setCancelled(true);
		}
		if (e.getEntity() instanceof Player)
		{
			if (e.getCause().equals(DamageCause.VOID))
			{
				e.getEntity().teleport(getWorld().getSpawnLocation().add(0, 4, 0));
				((Player) e.getEntity()).playSound(e.getEntity().getLocation(), Sound.CAT_MEOW, 0.5F, 1.0F);
			}
			else if (e.getEntity() instanceof Entity)
			{
				if (onWorld(e.getEntity()))
					e.setCancelled(true);
			}
			if (onWorld(e.getEntity()))
				e.setCancelled(true);
		}
	}

}
