package com.game.engine.GameTypes.gemhunt;

import java.util.HashMap;

import noble.craft.core.NobleCore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.CustomEvents.gameStateChange;
import com.game.engine.Game.Game;
import com.game.engine.Game.GameEvents;
import com.game.engine.Game.GameState;
import com.game.engine.Game.GameWorld;
import com.game.engine.Game.SpectatorManager;
import com.game.engine.Game.Team;
import com.game.engine.ScoreBoard.ScoreBoardFactory;
import com.game.engine.Util.ItemStackBuilder;

public class GemHuntEvents implements GameEvents,Listener{	
	
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

	public void onGameEndWinner()
	{
		Game game = GameEngine.getCurrentGame();
		Team winningTeam = null;
		int redTeamGems = 0;
		int blueTeamGems = 0;
		for (Player player : game.GetPlayers().keySet())
		{
			if(GameEngine.getCurrentGame().GetSpectators().contains(player))
			{
				continue;
			}
			
			if (game.GetTeam(player).equals(Game.toTeam("Red Team")))
			{
				redTeamGems = redTeamGems + player.getInventory().getItem(4).getAmount();
			}
			else if (game.GetTeam(player).equals(Game.toTeam("Blue Team")))
			{
				blueTeamGems = blueTeamGems + player.getInventory().getItem(4).getAmount();
			}
			else
			{
				GameEngine.Debug("Something went wrong counting a players gems. Continuing...");
			}
		}
		if (redTeamGems > blueTeamGems)
		{
			Bukkit.broadcastMessage(Chat.format("GameEngine", "&4RedTeam &6is the official winners!"));
			winningTeam = Game.toTeam("Red Team");
			// Do winning stuff for winners
		}
		else if (redTeamGems < blueTeamGems)
		{
			Bukkit.broadcastMessage(Chat.format("GameEngine", "&1BlueTeam &6is the official winners!"));
			winningTeam = Game.toTeam("Blue Team");
			// Do winning stuff for winners
		}
		else if (redTeamGems == blueTeamGems)
		{
			Bukkit.broadcastMessage(Chat.format("GameEngine", "&fThe game ended in a tie! GG"));
			// do nothing
		}
		else
		{
			GameEngine.Debug("Something went wrong broadcasting the winner. Check code!");
		}
		for (Player player : GameEngine.getCurrentGame().GetPlayers().keySet())
		{
			int reward = 0;
			if(GameEngine.getCurrentGame().GetSpectators().contains(player))
			{
				player.sendMessage(Chat.format("Reward", "You earned 50 + 10 gold"));
				reward = reward + 50 + 10;
			}
			else
			{
				player.sendMessage(Chat.format("Reward", "You earned 50 + 10*" + player.getInventory().getItem(4).getAmount() + " gold"));
				reward = reward + 50 + 10 * player.getInventory().getItem(4).getAmount();
			}
			if (winningTeam != null)
			{
				if (winningTeam.hasPlayer(player))
				{
					player.sendMessage(Chat.format("Reward", "You were also on the winning team! + 250 gold"));
					reward = reward + 250;
				}
			}
			player.sendMessage(Chat.format("Reward", "You earned in a total of " + ChatColor.GOLD + reward + " gold!"));
			NobleCore.getCurrencyManager().addTokens(player.getUniqueId().toString(), reward);
		}
	}

	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent event)
	{
		if (world.validateEntity(event.getEntity()))
		{
			int deathTime = 5;
			int amount;
			ItemStack dropableEmerald;
			
			event.setKeepInventory(true);
			event.getDrops().clear();
			event.getEntity().setHealth(20);
			event.setDeathMessage(null);
			
			amount = event.getEntity().getInventory().getItem(4).getAmount();
			dropableEmerald = new ItemStackBuilder(Material.EMERALD, amount, "Bibiaas Gem", ChatColor.GREEN).buildItem();
			
			world.getWorld().dropItem(event.getEntity().getLocation(), dropableEmerald);
			event.getEntity().getInventory().clear();
			
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
	public void onTeamDamage(EntityDamageByEntityEvent e)
	{
		if (!world.onWorld(e.getEntity()))
		{
			return;
		}
		// Blocks melee
		if (e.getEntity() instanceof Player)
		{
			if (e.getDamager() instanceof Player)
			{
				if (GameEngine.getCurrentGame().GetTeam((Player) e.getDamager()).equals(GameEngine.getCurrentGame().GetTeam((Player) e.getEntity())))
				{
					e.setCancelled(true);
				}
			}
		}
		// Blocks arrow
		if (e.getDamager() instanceof Arrow && e.getEntity() instanceof Player)
		{
			Arrow arrow = (Arrow) e.getDamager();
			Player shooter = (Player) arrow.getShooter();
			Player target = (Player) e.getEntity();
			if (GameEngine.getCurrentGame().GetTeam(shooter).equals(GameEngine.getCurrentGame().GetTeam(target)))
			{
				e.setCancelled(true);
			}
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

	private HashMap<String, Long> cooldown = new HashMap<String, Long>();

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		if (!world.onWorld(e.getPlayer()))
		{
			return;
		}
		Player p = e.getPlayer();
		if (GameEngine.getCurrentGame().getPlayerKit(e.getPlayer()).GetName().contentEquals("Leaper"))
		{
			if (cooldown.containsKey(p.getName()))
			{
				Long time = System.currentTimeMillis();
				Long lastusage = cooldown.get(p.getName());
				if (lastusage + 8 * 1000 > time)
				{
					p.setAllowFlight(false);
					p.setFlying(false);
					return;
				}
			}
			if (GameEngine.getCurrentGame().getPlayerKit(e.getPlayer()).GetName().contentEquals("Leaper"))
			{
				if (e.getPlayer().getGameMode() != GameMode.CREATIVE && e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR)
				{
					e.getPlayer().setAllowFlight(true);
				}
			}
		}
	}

	@EventHandler
	public void onFly(PlayerToggleFlightEvent e)
	{
		final Player p = e.getPlayer();
		if (!world.onWorld(p))
		{
			return;
		}
		if (GameEngine.getCurrentGame().getState() != GameState.STARTED)
		{
			return;
		}
		if (p.getGameMode() != GameMode.CREATIVE)
		{
			if (GameEngine.getCurrentGame().getPlayerKit(e.getPlayer()).GetName().contentEquals("Leaper"))
			{
				Long time = System.currentTimeMillis();
				if (cooldown.containsKey(p.getName()))
				{
					Long lastusage = cooldown.get(p.getName());
					if (lastusage + 8 * 1000 > time)
					{
						p.setAllowFlight(false);
						p.setFlying(false);
						Long endtime = time - lastusage;
						Long endtime1 = endtime / 1000;
						Long endtime2 = 8 - endtime1;
						Long time2 = endtime / 100;
						Long time3 = 100 - time2;
						String time4 = String.valueOf(time3);
						String time5 = time4.substring(1);
						p.sendMessage(Chat.format("GameEngine", "§7You can't use leap for " + ChatColor.GREEN + endtime2 + "." + time5 + ChatColor.GRAY + " Seconds"));
						return;
					}
				}
				e.setCancelled(true);
				p.setAllowFlight(false);
				p.setFlying(false);
				p.setVelocity(p.getLocation().getDirection().multiply(1.3D));
				p.getLocation().getWorld().playSound(p.getLocation(), Sound.GHAST_FIREBALL, 2.0F, 0.9F);
				cooldown.put(p.getName(), time);
				p.sendMessage(Chat.format("GameEngine", "§7You used §aLeap§7!"));
				Bukkit.getScheduler().scheduleSyncDelayedTask(GameEngine.GetPlugin(), new Runnable()
				{
					public void run()
					{
						p.sendMessage(Chat.format("GameEngine", "§aLeap §7Recharged!"));
					}
				}, 20L * 8);
				return;
			}
			return;
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
						onGameEndWinner();
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