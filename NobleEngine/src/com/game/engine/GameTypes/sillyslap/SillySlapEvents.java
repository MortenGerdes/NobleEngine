package com.game.engine.GameTypes.sillyslap;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.org.ibex.nestedvm.UnixRuntime.DevFS;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingSand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.CustomEvents.gameStateChange;
import com.game.engine.Game.SpectatorManager;
import com.game.engine.Game.GameManagement.IGameEvents;
import com.game.engine.Game.GameManagement.GameManager;
import com.game.engine.Game.GameManagement.GameState;
import com.game.engine.Game.GameManagement.GameWorld;
import com.game.engine.GameTypes.sillyslap.Score.SillySlapScoreManager;
import com.game.engine.GameTypes.sillyslap.Score.SillySlapScoreboard;
import com.game.engine.ScoreBoard.ScoreBoardFactory;

public class SillySlapEvents implements IGameEvents, Listener
{
	private SillySlapScoreManager scores = SillySlapScoreManager.getInstance();
	static int gameTime = 180;
	int task;
	int task2;
	int deathTime = 3;
	GameWorld world;

	@Override
	public void register()
	{
		if (world == null)
		{
			GameEngine.Debug("You must add a game world instance first before registering game events m8");
			return;
		}
		GameEngine.Register(this);
	}

	@Override
	public void unregister()
	{
		GameEngine.unRegister(this);
	}

	@Override
	public void setHostWorld(GameWorld w)
	{
		world = w;
	}

	@EventHandler
	public void onPlayerInventoryInteract(InventoryClickEvent event)
	{
		if (world.validateSpectator(event.getWhoClicked()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockBlockBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		if (world.validateSpectator(player)) event.setCancelled(true);
	}

	@EventHandler
	public void blockBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		if (world.validateSpectator(player))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void foodLevelChange(FoodLevelChangeEvent event)
	{
		if (world.validateSpectator(event.getEntity()))
		{
			event.setFoodLevel(20);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event)
	{
		if (world.validateSpectator(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onGameStart(gameStateChange event)
	{
		if (event.getToState() == GameState.STARTED)
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					startGameTimer();
					assignScoreBoard();
				}
			}.runTaskLater(GameEngine.GetPlugin(), 20);
		}
	}

	@EventHandler
	public void onPlayerDamagedByPlayer(EntityDamageByEntityEvent event)
	{
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player)
		{
			if (GameEngine.getCurrentGame().getState() != GameState.STARTED)
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerFallDamage(EntityDamageEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			if (event.getCause() == DamageCause.FALL)
			{
				if (player.getFallDistance() < 20)
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		final Player killed = event.getEntity();
		event.setKeepInventory(true);
		event.getDrops().clear();
		event.getEntity().setHealth(20);
		event.setDeathMessage(null);
		SillySlapScoreManager.getInstance().creditKiller(killed);
		killed.getWorld().strikeLightningEffect(killed.getLocation());
		killed.teleport(GameManager.getCurrentGameExtender().getSpawns().get(0).GetLocation());
		killed.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 3, 1));
		SpectatorManager.timedSpectator(event.getEntity(), deathTime);
		Bukkit.getScheduler().scheduleSyncDelayedTask(GameEngine.GetPlugin(), new Runnable()
		
		{
			@Override
			public void run()
			{
				if(GameEngine.getCurrentGame().getState() == GameState.STARTED)
				{
					killed.sendMessage(Chat.format("Respawn", "Get slapping!"));
					GameEngine.getCurrentGame().respawnPlayer(killed, getRespawnLocation());
				}
			}
		}, 20 * deathTime);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExplosion(EntityExplodeEvent e)
	{
		Random r = new Random();
		double maxx = 1;
		double maxy = 1;
		double maxz = 1;
		double minx = -1;
		double miny = 0.2;
		double minz = -1;
		e.setYield(0);
		for (Block b : e.blockList())
		{
			double x = minx + (maxx - minx) * r.nextDouble();
			double y = miny + (maxy - miny) * r.nextDouble();
			double z = minz + (maxz - minz) * r.nextDouble();
			FallingSand ent = (FallingSand) b.getWorld().spawnFallingBlock(b.getLocation().add(0, 1, 0), b.getType(), b.getData());
			ent.setDropItem(false);
			ent.setVelocity(new Vector(x, y, z));
			b.setType(Material.AIR);
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
					if (gameTime == 100)
					{
						new BukkitRunnable()
						{
							@Override
							public void run()
							{
								strikeLightning();
								if (gameTime <= 95)
								{
									this.cancel();
								}
							}
						}.runTaskTimer(GameEngine.GetPlugin(), 0, 10);
					}
					if (gameTime == 30)
					{
						strikeLightning();
						strikeLightning();
						strikeLightning();
						strikeLightning();
						strikeLightning();
						strikeLightning();
					}
					if (gameTime == 20)
					{
						strikeLightning();
						strikeLightning();
						strikeLightning();
						strikeLightning();
						strikeLightning();
						strikeLightning();
					}
					if (gameTime == 10)
					{
						strikeLightning();
						strikeLightning();
						strikeLightning();
						strikeLightning();
						strikeLightning();
						strikeLightning();
					}
					if (gameTime < 0)
					{
						GameEngine.Debug("Time ran out. Game stopped");
						GameEngine.getCurrentGame().addWinner(Bukkit.getPlayer(scores.getWinner()));
						Bukkit.broadcastMessage(Chat.format(GameEngine.getCurrentGame().getName(), "&aThe player &c" + SillySlapScoreManager.getInstance().getWinner() + "&a has won the game!"));
						Bukkit.getServer().getScheduler().cancelTask(task2);
						gameTime = 180;
						GameEngine.getCurrentGame().stop();
					}
				}
				else
				{
					Bukkit.getServer().getScheduler().cancelTask(task2);
					gameTime = 180;
				}
			}
		}, 20, 20);
	}

	public void assignScoreBoard()
	{
		GameEngine.getCurrentGame().getPanels().clear();
		for (Player player : GameEngine.getCurrentGame().getPlayers().keySet())
		{
			GameEngine.getCurrentGame().getPanels().put(player, new SillySlapScoreboard(player));
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
		}, 20 * 2, 20 * 1);
	}

	public int getGameTime()
	{
		return gameTime;
	}

	public Location getRespawnLocation()
	{
		Location loc1 = GameManager.getCurrentGameExtender().getCorner1();
		Location loc2 = GameManager.getCurrentGameExtender().getCorner2();
		int minx = (int) Math.min(loc1.getX(), loc2.getX());
		int miny = (int) Math.min(loc1.getY(), loc2.getY());
		int minz = (int) Math.min(loc1.getZ(), loc2.getZ());
		int maxx = (int) Math.max(loc1.getX(), loc2.getX());
		int maxy = (int) Math.max(loc1.getY(), loc2.getY());
		int maxz = (int) Math.max(loc1.getZ(), loc2.getZ());
		Random r = new Random();
		int x = 0;
		int y = 0;
		int z = 0;
		
		Location loc = new Location(world.getWorld(), x, y, z);
		while (loc.getBlock().getType() != Material.AIR || loc.add(0, 1, 0).getBlock().getType() != Material.AIR || loc.add(0, 2, 0).getBlock().getType() != Material.AIR
				|| loc.subtract(0, 1, 0).getBlock().getType() == Material.AIR)
		{
			x = r.nextInt((maxx - minx) + 1) + minx;
			y = r.nextInt((maxy - miny) + 1) + miny;
			z = r.nextInt((maxz - minz) + 1) + minz;
			loc.setX(x);
			loc.setY(y);
			loc.setZ(z);
		}
		loc.add(0, 1, 0);
		return loc;
		}

	public void strikeLightning()
	{
		Location loc = this.getRespawnLocation();
		loc.getWorld().strikeLightningEffect(loc);
		loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
	}

	public SillySlapScoreManager getScoreManager()
	{
		return this.scores;
	}
}