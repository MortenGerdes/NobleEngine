package com.game.engine.GameTypes.noblerunner;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.FallingSand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.CustomEvents.gameStateChange;
import com.game.engine.Game.SpectatorManager;
import com.game.engine.Game.GameManagement.IGameEvents;
import com.game.engine.Game.GameManagement.GameManager;
import com.game.engine.Game.GameManagement.GameState;
import com.game.engine.Game.GameManagement.GameWorld;
import com.game.engine.ScoreBoard.ScoreBoardFactory;
import com.game.engine.Util.LocationsUtil;

public class NobleRunnerEvents implements IGameEvents, Listener
{
	private GameWorld _world;
	private ArrayList<Player> _alive;

	@Override
	public void register()
	{
		if (_world == null)
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
		_world = w;
	}

	@EventHandler
	public void onGameStart(gameStateChange event)
	{
		if (event.getToState() == GameState.STARTED)
		{
			this._alive = new ArrayList<Player>(GameEngine.getCurrentGame().getPlayers().keySet());
			
			fallingBlocks();
			assignScoreBoard();
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (GameEngine.getCurrentGame().getState() != GameState.STARTED)
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (GameEngine.getCurrentGame().getState() == GameState.STARTED)
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event)
	{
		if (GameEngine.getCurrentGame().getState() == GameState.STARTED)
		{
			if (GameManager.getCurrentGameExtender().onWorld(event.getPlayer()))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void foodLevelChange(FoodLevelChangeEvent event)
	{
		if (GameEngine.getCurrentGame().getHost().validateSpectator(event.getEntity()))
		{
			event.setFoodLevel(20);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onFallingSandLand(EntityChangeBlockEvent event)
	{
		if (GameEngine.getCurrentGame().getState() == GameState.STARTED)
		{
			Entity theEntity = event.getEntity();
			
			if(theEntity instanceof FallingBlock)
			{
				FallingBlock fb = (FallingBlock)theEntity;
				
				if(fb.isOnGround())
				{
					fb.getWorld().playEffect(fb.getLocation(), Effect.STEP_SOUND, fb.getBlockId());
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onItemPickUp(PlayerPickupItemEvent event)
	{
		if (event.getItem().getItemStack().getType() == Material.DIAMOND_BLOCK)
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
					player.teleport(GameEngine.getCurrentGame().getHost().getDefaultSpawn());
					player.playSound(player.getLocation(), Sound.CAT_MEOW, 0.5F, 1.0F);
					Bukkit.broadcastMessage(Chat.format(GameEngine.getCurrentGame().getName(), "The player &c" + player.getName() + "&e died"));
					_alive.remove(player);
					ScoreBoardFactory.globalScoreBoardUpdate();
					
					if (_alive.size() == 1)
					{
						Bukkit.broadcastMessage(Chat.format(GameEngine.getCurrentGame().getName(), "&aThe player &c" + _alive.get(0).getName() + "&a has won the game!"));
						GameEngine.getCurrentGame().stop();
					}
				}
				else
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
		killed.getWorld().strikeLightningEffect(killed.getLocation());
		killed.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 3, 1));
		killed.teleport(GameEngine.getCurrentGame().getHost().getDefaultSpawn());
		Bukkit.broadcastMessage(Chat.format(GameEngine.getCurrentGame().getName(), "The player &c" + killed.getName() + "&e died"));
		SpectatorManager.addSpectator(killed);
		_alive.remove(killed);
		ScoreBoardFactory.globalScoreBoardUpdate();
		
		if (_alive.size() == 1)
		{
			Bukkit.broadcastMessage(Chat.format(GameEngine.getCurrentGame().getName(), "&aThe player &c" + _alive.get(0).getName() + "&a has won the game!"));
			GameEngine.getCurrentGame().stop();
		}
	}

	public void fallingBlocks()
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if (GameEngine.getCurrentGame().getState() == GameState.STARTED)
				{
					for (Player player : GameEngine.getCurrentGame().getPlayers().keySet())
					{
						if (!GameEngine.getCurrentGame().getSpectators().contains(player))
						{
							Location loc1 = player.getLocation().add(-1.4, -1, -1.4);
							Location loc2 = player.getLocation().add(0.08, -0.5, 0.08);
							List<Location> locationOfBlocks = LocationsUtil.getAllBlocksInsideCuboid(loc1, loc2);
							for (Location location : locationOfBlocks)
							{
								Block block = GameEngine.getCurrentGame().getHost().getWorld().getBlockAt(location);
								if (block.getType() == Material.AIR)
								{
									continue;
								}
								if(block.getWorld().getBlockAt(block.getLocation().add(0,1,0)).getType() != Material.AIR)
								{
									
									if(block.getWorld().getBlockAt(block.getLocation().add(0,1,0)).getType() != Material.TORCH &&  (block.getWorld().getBlockAt(block.getLocation().add(0,1,0)).getType() != Material.COBBLE_WALL))
									{
										continue;
									}
								}
								if (block.getType() != Material.STAINED_CLAY)
								{
									createFallingBlock(block);
								}
							}
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

	public void createFallingBlock(final Block block)
	{
		new BukkitRunnable()
		{
			int control = 0;

			@Deprecated
			@Override
			public void run()
			{
				if (control == 0)
				{
					block.setType(Material.STAINED_CLAY);
					block.setData(DyeColor.GREEN.getData());
					control++;
					return;
				}
				if (control == 1)
				{
					block.setType(Material.STAINED_CLAY);
					block.setData(DyeColor.YELLOW.getData());
					control++;
					return;
				}
				if (control == 2)
				{
					block.setType(Material.STAINED_CLAY);
					block.setData(DyeColor.ORANGE.getData());
					control++;
					return;
				}
				if (control == 3)
				{
					block.setType(Material.STAINED_CLAY);
					block.setData(DyeColor.PURPLE.getData());
					control++;
					return;
				}
				if (control == 4)
				{
					block.setType(Material.STAINED_CLAY);
					block.setData(DyeColor.RED.getData());
					control++;
					return;
				}
				if (control == 5)
				{
					FallingSand fs = (FallingSand) block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
					fs.setDropItem(false);
					block.setType(Material.AIR);
					this.cancel();
					return;
				}
			}
		}.runTaskTimer(GameEngine.GetPlugin(), 0, 5);
	}
	
	public void assignScoreBoard()
	{
		GameEngine.getCurrentGame().getPanels().clear();
		for (Player player : GameEngine.getCurrentGame().getPlayers().keySet())
		{
			GameEngine.getCurrentGame().getPanels().put(player, new NobleRunnerScoreBoard(player));
		}
		ScoreBoardFactory.globalScoreBoardUpdate();
	}

	
}
