package com.game.engine.World;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.game.engine.GameEngine;
import com.game.engine.CustomEvents.gameStateChange;
import com.game.engine.Game.GameState;
import com.game.engine.Game.GameWorld;
import com.game.engine.ScoreBoard.ScoreBoardFactory;
import com.game.engine.ScoreBoard.playerScoreBoard;

public class HubWorld extends GameWorld
{
	public HubWorld()
	{
		super("Dummy", "NobleLobby");
		this.setWorld("NobleLobby");
		
		setType(CustomWorldType.WAITINGLOBBY);
		lockTime(6000);
		setDefaultSpawnLocation(new Location(getWorld(),0,156,0));
	}
	
	@EventHandler
	public void blockBlockBreak(BlockBreakEvent e)
	{
		Player player = e.getPlayer();
		if (onWorld(player))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onMobIgnite(EntityCombustEvent event)
	{
		if(onWorld(event.getEntity()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockBlockPlace(BlockPlaceEvent e)
	{
		Player player = e.getPlayer();
		if (onWorld(player))
			e.setCancelled(true);
	}

	@EventHandler
	public void combatProtection(EntityDamageEvent e)
	{
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

	@EventHandler
	public void entityGrief(EntityChangeBlockEvent e)
	{
		if (onWorld(e.getEntity()))
		{
			if (e.getEntity() instanceof LivingEntity)
			{
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void fire(BlockIgniteEvent event)
	{
		try
		{
			if (onWorld(event.getIgnitingEntity()))
			{
				if (true)
				{
					event.setCancelled(true);
				}
			}
		}
		catch (Exception e)
		{
			Bukkit.getLogger().info("Failed BlockIgniteEvent in " + getName());
		}
	}

	@EventHandler
	public void foodLevelChange(FoodLevelChangeEvent e)
	{
		if (onWorld(e.getEntity()))
		{
			e.setFoodLevel(20);
			e.setCancelled(true);
		}
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
	public void onItemDrop(PlayerDropItemEvent event)
	{
		if(onWorld(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if(onWorld(event.getWhoClicked()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void entityExplosion(EntityExplodeEvent e)
	{
		try
		{
			if (onWorld(e.getEntity()))
				e.setCancelled(true);
		}
		catch (NullPointerException npe)
		{}
	}

	@EventHandler
	public void onRain(WeatherChangeEvent event)
	{
		if (event.getWorld().equals(getWorld()))
		{
			if (event.toWeatherState())
			{
				event.setCancelled(true);
			}
		}
	}

	public void lockTime(final int time)
	{
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GameEngine.GetPlugin(), new Runnable()
		{
			@Override
			public void run()
			{
				Bukkit.getWorld("NobleLobby").setTime(time);
			}
		}, 0, 20);
	}
	
	@EventHandler
	public void resetPlayerScoreBoardToDefault(gameStateChange event)
	{
		if(event.getFromState() == GameState.ENDED)
		{
			GameEngine.getCurrentGame().GetPanels().clear();
			
			for(Player player: GameEngine.getCurrentGame().GetPlayers().keySet())
			{
				GameEngine.getCurrentGame().GetPanels().put(player, new playerScoreBoard(player));
			}
			
			ScoreBoardFactory.globalScoreBoardUpdate();
		}
	}

	@Override
	public void setSpawnPointsFromHostWorld()
	{
		
	}
}