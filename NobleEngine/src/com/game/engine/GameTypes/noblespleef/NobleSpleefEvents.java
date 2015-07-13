package com.game.engine.GameTypes.noblespleef;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.defaults.ReloadCommand;
import org.bukkit.craftbukkit.libs.org.ibex.nestedvm.UnixRuntime.DevFS;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingSand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.CustomEvents.gameStateChange;
import com.game.engine.Game.SpectatorManager;
import com.game.engine.Game.GameManagement.IGameEvents;
import com.game.engine.Game.GameManagement.GameManager;
import com.game.engine.Game.GameManagement.GameState;
import com.game.engine.Game.GameManagement.GameWorld;
import com.game.engine.GameTypes.sillyslap.Score.SillySlapScoreboard;
import com.game.engine.ScoreBoard.ScoreBoardFactory;
import com.game.engine.Util.LocationsUtil;
import com.game.engine.Util.MiscUtil;
import com.game.engine.Util.UtilPlayer;

public class NobleSpleefEvents implements IGameEvents, Listener
{
	private int _index;
	private int _every3Sec;
	private GameWorld _world;
	private ArrayList<String> _alive = new ArrayList<String>();

	@Override
	public void register()
	{
		if (_world == null)
		{
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
		_world = w;
	}

	@EventHandler
	public void onGameStart(gameStateChange event)
	{
		if (event.getToState() == GameState.STARTED)
		{
			for (Player players : GameEngine.getCurrentGame().getPlayers().keySet())
			{
				_alive.add(players.getName());
			}
			assignScoreBoard();
			startSatuationDrainer();
			startMeteor(40);
		}
	}

	@EventHandler
	public void onGameEnd(gameStateChange event)
	{
		if (event.getToState() == GameState.ENDED)
		{
			_alive.clear();
		}
	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event)
	{
		if (GameEngine.getCurrentGame().getState() != GameState.STARTED)
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBrockplace(BlockPlaceEvent event)
	{
		if (GameEngine.getCurrentGame().getState() == GameState.STARTED)
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void voidProtection(EntityDamageEvent event)
	{
		if (GameManager.getCurrentGameExtender().validateSpectator(event.getEntity()))
		{
			if (event.getEntity() instanceof Player)
			{
				Player player = (Player) event.getEntity();
				if (event.getCause().equals(DamageCause.VOID))
				{
					SpectatorManager.addSpectator(player);
					player.getWorld().strikeLightningEffect(player.getLocation());
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 3, 1));
					event.getEntity().teleport(GameEngine.getCurrentGame().getHost().getWorld().getSpawnLocation());
					((Player) event.getEntity()).playSound(event.getEntity().getLocation(), Sound.CAT_MEOW, 0.5F, 1.0F);
					Bukkit.broadcastMessage(Chat.format(GameEngine.getCurrentGame().getName(), "The player &c" + player.getName() + "&e has been spleefed"));
					if (GameEngine.getCurrentGame().getState() == GameState.STARTED)
					{
						_alive.remove(player.getName());
						ScoreBoardFactory.globalScoreBoardUpdate();
						if (_alive.size() == 1)
						{
							Bukkit.broadcastMessage(Chat.format(GameEngine.getCurrentGame().getName(), "&aThe player &c" + _alive.get(0) + "&a has won the game!"));
							GameEngine.getCurrentGame().stop();
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onArrowHitBlock(ProjectileHitEvent event)
	{
		if (event.getEntity() instanceof Arrow)
		{
			Projectile proj = event.getEntity();
			Arrow arrow = (Arrow) proj;
			if (arrow.getShooter() instanceof Player)
			{
				Player shooter = (Player) arrow.getShooter();
				if (GameEngine.getCurrentGame().getState() != GameState.STARTED)
				{
					arrow.remove();
					return;
				}
				if(UtilPlayer.isOnWater(shooter))
				{
					shooter.sendMessage(Chat.format(GameEngine.getCurrentGame().getName(), "Your arrows are &cuseless &ewhile you're in water!"));
					return;
				}
				if (shooter instanceof Player)
				{
					if (!GameManager.getCurrentGameExtender().validateSpectator(shooter))
					{
						arrow.remove();
						return;
					}
					Block hitBlock = MiscUtil.getHitBlock(arrow);
					Location hitBlockLocation = hitBlock.getLocation().add(0, 1, 0);
					int range = 3;
					int minX = hitBlockLocation.getBlockX() - range / 2;
					int minY = hitBlockLocation.getBlockY() - range / 2;
					int minZ = hitBlockLocation.getBlockZ() - range / 2;
					for (int y = minY; y < minY + 1; y++)
					{
						for (int x = minX; x < minX + range; x++)
						{
							for (int z = minZ; z < minZ + range; z++)
							{
								Block relativeBlocks = GameEngine.getCurrentGame().getHost().getWorld().getBlockAt(x, y, z);
								relativeBlocks.getWorld().playEffect(relativeBlocks.getLocation(), Effect.STEP_SOUND, relativeBlocks.getTypeId());
								relativeBlocks.setType(Material.AIR);
							}
						}
					}
				}
				MiscUtil.changePlayerHunger(shooter, 5);
				arrow.remove();
			}
		}
	}

	@EventHandler
	public void onPlayerHitByArrow(EntityDamageByEntityEvent event)
	{
		if (event.getDamager() instanceof Arrow && event.getEntity() instanceof Player)
		{
			Arrow arrow = (Arrow) event.getDamager();
			Player shooter = (Player) arrow.getShooter();
			if (shooter == null)
			{
				event.setDamage(0);
				arrow.setKnockbackStrength(12);
			}
			else
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerInteractWithBlock(PlayerInteractEvent event)
	{
		if (GameEngine.getCurrentGame().getState() != GameState.STARTED)
		{
			return;
		}
		if (event.getPlayer().getItemInHand().getType() != Material.IRON_SPADE)
		{
			return;
		}
		Action action = event.getAction();
		if (action == Action.LEFT_CLICK_BLOCK)
		{
			if(UtilPlayer.isOnWater(event.getPlayer()))
			{
				event.getPlayer().sendMessage(Chat.format(GameEngine.getCurrentGame().getName() , "You &ccannot &ebreak blocks while in water!"));
				return;
			}
			
			Block clickedBlock = event.getClickedBlock();
			if (clickedBlock.getType() != Material.BEDROCK)
			{
				if (GameManager.getCurrentGameExtender().validateSpectator(event.getPlayer()))
				{
					clickedBlock.getWorld().playEffect(clickedBlock.getLocation(), Effect.STEP_SOUND, clickedBlock.getTypeId());
					clickedBlock.setType(Material.AIR);
					MiscUtil.changePlayerHunger(event.getPlayer(), 1);
				}
			}
		}
	}

	@EventHandler
	public void onItemPickUp(PlayerPickupItemEvent event)
	{
		if (event.getItem().getItemStack().getType() == Material.COOKIE)
		{
			event.setCancelled(true);
			return;
		}
		else
		{
			event.getItem().remove();
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event)
	{
		if (GameManager.getCurrentGameExtender().onWorld(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDamagedByPlayer(EntityDamageByEntityEvent event)
	{
		if (GameEngine.getCurrentGame().getState() != GameState.STARTED)
		{
			return;
		}
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player)
		{
			//			Player damager = (Player)event.getDamager();
			//			Player damaged = (Player)event.getEntity();
			//			
			//			Vector knockback = damaged.getLocation().toVector().subtract(damager.getLocation().toVector()).multiply(0.2).normalize();
			//			damaged.setVelocity(knockback);
			event.setDamage(0);
		}
		else if (event.getDamager() instanceof Arrow)
		{
			Arrow arrow = (Arrow) event.getDamager();
			if (arrow.getShooter() == null)
			{
				return;
			}
		}
		else
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			if (event.getCause() == DamageCause.ENTITY_ATTACK)
			{
				return;
			}
			if (event.getCause() == DamageCause.PROJECTILE)
			{
				return;
			}
			if (event.getCause() != DamageCause.VOID)
			{
				event.setCancelled(true);
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
		killed.getWorld().strikeLightningEffect(killed.getLocation());
		killed.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 3, 1));
		killed.teleport(GameEngine.getCurrentGame().getHost().getWorld().getSpawnLocation());
		Bukkit.broadcastMessage(Chat.format(GameEngine.getCurrentGame().getName(), "The player &c" + killed.getName() + "&e has been killed"));
		SpectatorManager.addSpectator(killed);
		if (GameEngine.getCurrentGame().getState() == GameState.STARTED)
		{
			_alive.remove(killed.getName());
			ScoreBoardFactory.globalScoreBoardUpdate();
			if (_alive.size() == 1)
			{
				Bukkit.broadcastMessage(Chat.format(GameEngine.getCurrentGame().getName(), "&aThe player &c" + _alive.get(0) + "&a has won the game!"));
				GameEngine.getCurrentGame().stop();
			}
		}
	}

	public void assignScoreBoard()
	{
		GameEngine.getCurrentGame().getPanels().clear();
		for (Player player : GameEngine.getCurrentGame().getPlayers().keySet())
		{
			GameEngine.getCurrentGame().getPanels().put(player, new NobleSpleefScoreBoard(player));
		}
		ScoreBoardFactory.globalScoreBoardUpdate();
	}

	private void startSatuationDrainer()
	{
		_every3Sec = 0;
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				_every3Sec++;
				if (GameEngine.getCurrentGame().getState() != GameState.STARTED)
				{
					this.cancel();
				}
				for (String names : _alive)
				{
					Player player = Bukkit.getServer().getPlayer(names);
					MiscUtil.changePlayerHunger(player, -1);
					if (_every3Sec % 2 == 0)
					{
						if (player.getFoodLevel() <= 0)
						{
							player.damage(1);
						}
					}
				}
			}
		}.runTaskTimer(GameEngine.GetPlugin(), 20 * 2, 20 * 1);
	}

	public void startMeteor(int seconds)
	{
		final String gameCheck = GameEngine.getCurrentGame().getName();
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if (gameCheck == GameEngine.getCurrentGame().getName() && GameEngine.getCurrentGame().getState() == GameState.STARTED)
				{
					Bukkit.broadcastMessage(Chat.format(GameEngine.getCurrentGame().getName(), "&cThe skies are rumbling by an unknown source!"));
					Chat.devMessage("Meteor strike was set to 40 sec (quite early) because of show ^-^");
					strikeMeteor(true);
				}
			}
		}.runTaskLater(GameEngine.GetPlugin(), 20 * seconds);
	}

	public Location getRandomLocation()
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
		Location loc = new Location(GameEngine.getCurrentGame().getHost().getWorld(), x, y, z);
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

	private void strikeMeteor(final boolean indefinitely)
	{
		_index = 0;
		Location locStart = GameEngine.getCurrentGame().getHost().getDefaultSpawn().add(10, 20, -10);
		Location locEnd = getRandomLocation();
		final List<Location> line = LocationsUtil.getLineBetweenPositions(locStart, locEnd);
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if (GameEngine.getCurrentGame().getState() == GameState.STARTED)
				{
					if (_index + 1 < line.size())
					{
						Location current = line.get(_index);
						for (int i = 0; i < 4; i++)
						{
							GameEngine.getCurrentGame().getHost().getWorld().playEffect(current, Effect.EXPLOSION_LARGE, null);
						}
						GameEngine.getCurrentGame().getHost().getWorld().playSound(current, Sound.SUCCESSFUL_HIT, 1, 1);
						_index++;
					}
					else
					{
						Location current = line.get(_index);
						GameEngine.getCurrentGame().getHost().getWorld().createExplosion(current, 6);
						GameEngine.getCurrentGame().getHost().getWorld().createExplosion(current, 6);
						this.cancel();
						if (indefinitely == true)
						{
							//recussion
							strikeMeteor(true);
						}
					}
				}
				else
				{
					this.cancel();
				}
			}
		}.runTaskTimer(GameEngine.GetPlugin(), 0, 2);
	}
}
