package com.game.engine.Game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.game.engine.GameEngine;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public abstract class GameExtender implements Listener
{
	private ArrayList<Spawn> spawns = new ArrayList<>();
	private ArrayList<Team> teams = new ArrayList<>();
	private ArrayList<Kit> kits = new ArrayList<>();
	private String name;
	private String initials;
	private String[] desc;
	private Kit defaultKit;
	private GameWorld host;
	private List<GameWorld> worlds;
	private int minplayer;
	private int maxplayer;
	
	private GameEvents[] events;
	
	public GameExtender(String gameName, String initials, String[] gameDesc, Kit gameDefaultKit, GameWorld gameHost, int gameMaxPlayers, int gameMinPlayers, List<GameWorld> worlds, GameEvents... events)
	{
		this.name = gameName;
		this.initials = initials;
		this.desc = gameDesc;
		this.defaultKit = gameDefaultKit;
		this.host = gameHost;
		this.maxplayer = gameMaxPlayers;
		this.minplayer = gameMinPlayers;
		this.worlds = worlds;
		this.events = events;
	}

	public abstract void loadGame();

	public void addBasicGameInfo()
	{
		Game game = GameEngine.getCurrentGame();
		game.setName(name);
		game.setDesc(desc);
		game.setDefaultKit(defaultKit);
		game.setHost(host);
		game.setMinPlayers(minplayer);
		game.setMaxPlayers(maxplayer);
	}

	private GameWorld nextMap(List<GameWorld> worlds)
	{
		for(GameWorld testing: worlds)
		{
			Bukkit.broadcastMessage("The world " + testing.getName() + " has been registered");
		}
		
		this.worlds = worlds;
		GameWorld current = GameEngine.getCurrentGame().GetHost();
		int position = worlds.indexOf(current)+1;
		
		if (worlds.size()-1 < position)
		{
			position = 0;
		}
		Bukkit.broadcastMessage("World size = " + (worlds.size()-1) + " and position is " + position);
		return worlds.get(position);
	}

	public void setNewHost()
	{
		GameEngine.getCurrentGame().GetHost().unRegister();
		
		GameWorld newWorld = nextMap(worlds);
		this.host = newWorld;
		for(GameEvents e: events)
		{
			e.setHostWorld(newWorld);
		}
		
		GameEngine.getCurrentGame().setHost(newWorld);
		
		GameEngine.Register(GameEngine.getCurrentGame().GetHost().gameWorld());
	}

	public void addSpawn(Spawn spawn)
	{
		if (!spawns.contains(spawn))
		{
			spawns.add(spawn);
		}
	}

	public void addTeam(Team team)
	{
		if (!teams.contains(team))
		{
			teams.add(team);
		}
	}

	public void addKit(Kit kit)
	{
		if (!kits.contains(kit))
		{
			kits.add(kit);
		}
	}

	public void addWinner(Player player)
	{
		GameEngine.getCurrentGame().AddWinner(player);
	}

	public boolean onWorld(Entity entity)
	{
		if(GameEngine.getCurrentGame().getState() != GameState.STARTED)
		{
			return false;
		}
		else
		{
		return getHost().getWorld().equals(entity.getWorld());
		}
	}

	public void updateGame()
	{
		addBasicGameInfo();
		Game.updateLists(teams, kits, spawns, defaultKit);
	}

	public void updateSpawns()
	{
		Game.replaceSpawn(spawns);
	}

	public void registerEvents()
	{	
		for(GameEvents e : events)
		{
			e.setHostWorld(host);
			e.register();
		}
		
		Bukkit.getPluginManager().registerEvents(this, GameEngine.GetPlugin());
	}

	public void unRegisterEvents()
	{
		for(GameEvents e : events)
			e.unregister();
		HandlerList.unregisterAll(this);
	}

	/**
	 * returns false if entity is not a player<br>
	 * return false if the entity is a player but not a spectator<br>
	 * <br>
	 * Only use this when working with spectators!
	 * 
	 * @param entity
	 * @return
	 */
	public boolean validateEntity(Entity entity)
	{
		if (onWorld(entity))
		{
			if (entity instanceof Player)
			{
				if (!GameEngine.getCurrentGame().GetSpectators().contains(entity))
				{
					return true;
				}
			}
		}
		return false;
	}

	@EventHandler
	public void onItemPickUp(PlayerPickupItemEvent event)
	{
		if (!validateEntity(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (!validateEntity(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event)
	{
		if (!validateEntity(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockBlockBreak(BlockBreakEvent e)
	{
		Bukkit.broadcastMessage("Testing: BlockBreak for spectators");
		Player player = e.getPlayer();
		if (!validateEntity(player)) e.setCancelled(true);
	}

	@EventHandler
	public void blockBlockPlace(BlockPlaceEvent e)
	{
		Player player = e.getPlayer();
		if (!validateEntity(player)) e.setCancelled(true);
	}

	@EventHandler
	public void onTeamDamage(EntityDamageByEntityEvent e)
	{
		if (!onWorld(e.getEntity()))
		{
			return;
		}
		if (e.getEntity() instanceof Player)
		{
			if (e.getDamager() instanceof Player)
			{
				if (!validateEntity(e.getDamager()))
				{
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void voidProtection(EntityDamageEvent e)
	{
		if (!onWorld(e.getEntity()))
		{
			return;
		}
		if (e.getEntity() instanceof Player)
		{
			if (e.getCause().equals(DamageCause.VOID))
			{
				GameEngine.getCurrentGame().Spectate((Player) e.getEntity());
				e.getEntity().teleport(GameEngine.getCurrentGame().GetHost().getWorld().getSpawnLocation().add(0, 4, 0));
				((Player) e.getEntity()).playSound(e.getEntity().getLocation(), Sound.CAT_MEOW, 0.5F, 1.0F);
			}
		}
	}

	@EventHandler
	public void fire(BlockIgniteEvent event)
	{
		try
		{
			if (!validateEntity(event.getIgnitingEntity()))
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
		if (!validateEntity(e.getEntity()))
		{
			e.setFoodLevel(20);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event)
	{
		if (!validateEntity(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void entityGrief(EntityChangeBlockEvent e)
	{
		if (!validateEntity(e.getEntity()))
		{
			if (e.getEntity() instanceof LivingEntity)
			{
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if (!validateEntity(event.getWhoClicked()))
		{
			event.setCancelled(true);
		}
	}

	public String getName()
	{
		return name;
	}

	public String getInitials()
	{
		return initials;
	}

	public String[] getDesc()
	{
		return desc;
	}

	public Kit getDefaultKit()
	{
		return defaultKit;
	}

	public GameWorld getHost()
	{
		return host;
	}

	public int getMinplayer()
	{
		return minplayer;
	}

	public int getMaxplayer()
	{
		return maxplayer;
	}

	public List<Player> getWinners()
	{
		return GameEngine.getCurrentGame().GetWinners();
	}

	public ArrayList<Spawn> getSpawns()
	{
		return spawns;
	}

	public ArrayList<Team> getTeams()
	{
		return teams;
	}

	public ArrayList<Kit> getKits()
	{
		return kits;
	}

	public Game getGame()
	{
		return GameEngine.getCurrentGame();
	}
}
