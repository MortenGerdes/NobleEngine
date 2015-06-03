package com.game.engine.GameTypes.sillyslap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.CustomEvents.gameStateChange;
import com.game.engine.Game.GameEvents;
import com.game.engine.Game.GameState;
import com.game.engine.Game.GameWorld;
import com.game.engine.Game.SpectatorManager;
import com.game.engine.GameTypes.gemhunt.gemhuntScoreBoard;
import com.game.engine.ScoreBoard.ScoreBoardFactory;
import com.game.engine.Util.ItemStackBuilder;

public class SillySlapEvents implements GameEvents,Listener{	
	
	int task;
	int task2;
	static int gameTime = 200;
	GameWorld world;
	
	@Override
	public void register() {
		
		if(world == null){
			GameEngine.Debug("You must add a game world instance first before registering game events m8");
			return;
		}
		
		GameEngine.Register(this);
	}

	@Override
	public void unregister() {
		GameEngine.unRegister(this);
	}
	
	@Override
	public void setHostWorld(GameWorld w) {
		world = w;
	}
	
	@EventHandler
	public void onPlayerInventoryInteract(InventoryClickEvent event)
	{
		if (world.validateEntity(event.getWhoClicked()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockBlockBreak(BlockBreakEvent event)
	{
		Bukkit.broadcastMessage("Testing: BlockBreak");
		Player player = event.getPlayer();
		if (world.validateEntity(player)) event.setCancelled(true);
	}

	@EventHandler
	public void blockBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		if (world.validateEntity(player))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void foodLevelChange(FoodLevelChangeEvent event)
	{
		if (world.validateEntity(event.getEntity()))
		{
			event.setFoodLevel(20);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event)
	{
		if (world.validateEntity(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onGameStart(gameStateChange event)
	{
		if (event.getToState() == GameState.STARTED)
		{
			Bukkit.broadcastMessage("Testing: GameStartEvent");
			for (Player player : GameEngine.getCurrentGame().GetPlayers().keySet())
			{
				player.getInventory().setItem(4, new ItemStackBuilder(Material.EMERALD, 1, "Bibiaas Gem", ChatColor.GREEN).buildItem());
			}
			startGameTimer();
			assignScoreBoard();
		}
	}

	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent event)
	{
		if (world.validateEntity(event.getEntity()))
		{
			int deathTime = 3;
			int amount;
			
			event.setKeepInventory(true);
			event.getDrops().clear();
			event.getEntity().setHealth(20);
			event.setDeathMessage(null);
			
			SpectatorManager.timedSpectator(event.getEntity(), deathTime);
			Bukkit.getScheduler().scheduleSyncDelayedTask(GameEngine.GetPlugin(), new Runnable()
			{
				@Override
				public void run()
				{
					GameEngine.getCurrentGame().respawnPlayer(event.getEntity());
					ItemStack emerald = new ItemStackBuilder(Material.EMERALD, 1, "Bibiaas Gem", ChatColor.GREEN).buildItem();
					event.getEntity().getInventory().setItem(4, emerald);
					event.getEntity().sendMessage(Chat.format("Respawn", "&cYou have been respawned"));
				}
			}, 20 * deathTime);
		}
	}

	@EventHandler
	public void cancelFallDamage(EntityDamageEvent event)
	{
		if (!world.onWorld(event.getEntity()))
		{
			return;
		}
		if (event.getEntity() instanceof Player)
		{
			if (event.getCause() == DamageCause.FALL)
			{
				
				event.setCancelled(true);
			}
		}
	}

	public void startGameTimer()
	{
		task2 = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GameEngine.GetPlugin(), new Runnable()
		{
			@Override
			public void run()
			{
				if (GameEngine.getCurrentGame().getState() == GameState.STARTED)
				{
					gameTime--;
					if (gameTime < 0)
					{
						GameEngine.Debug("Time ran out. Game stopped");
						GameEngine.getCurrentGame().Stop();
						//onGameEndWinner();
						Bukkit.getServer().getScheduler().cancelTask(task2);
						gameTime = 200;
					}
				}
				else
				{
					Bukkit.getServer().getScheduler().cancelTask(task2);
					gameTime = 200;
				}
			}
		}, 20, 20);
	}

	public void assignScoreBoard()
	{
		GameEngine.getCurrentGame().GetPanels().clear();
		for (Player player : GameEngine.getCurrentGame().GetPlayers().keySet())
		{
			GameEngine.getCurrentGame().GetPanels().put(player, new gemhuntScoreBoard(player));
		}
		ScoreBoardFactory.globalScoreBoardUpdate();
		startScoreBoardAutoUpdate();
	}

	public void startScoreBoardAutoUpdate()
	{
		task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GameEngine.GetPlugin(), new Runnable()
		{
			@Override
			public void run()
			{
				if (GameEngine.getCurrentGame().getState() == GameState.STARTED)
				{
					ScoreBoardFactory.globalScoreBoardUpdate();
				}
				else
				{
					Bukkit.getServer().getScheduler().cancelTask(task);
				}
			}
		}, 20 * 5, 20 * 5);
	}

	public static int getGameTime()
	{
		return gameTime;
	}
}
