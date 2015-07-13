package com.game.engine.Game.GameManagement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.game.engine.GameEngine;
import com.game.engine.Ability.AbilityManager;
import com.game.engine.Chat.Chat;
import com.game.engine.CustomEvents.gameChangeEvent;
import com.game.engine.CustomEvents.gameStateChange;
import com.game.engine.Game.Kit;
import com.game.engine.Game.Spawn;
import com.game.engine.Game.Team;

public abstract class GameExtender implements Listener
{
	private boolean _isForcingTeams = true;
	private boolean _isFreezingOnStart = true;
	private int _minplayer;
	private int _maxplayer;
	private String _name;
	private String _initials;
	private String[] _desc;
	private List<GameWorld> _worlds;
	private ArrayList<Spawn> _spawns = new ArrayList<>();
	private ArrayList<Team> _teams = new ArrayList<>();
	private ArrayList<Kit> _kits = new ArrayList<>();
	private Kit _defaultKit;
	private GameWorld _host;
	private Location _corner1;
	private Location _corner2;
	private IGameEvents[] _events;

	public GameExtender(String gameName, String initials, String[] gameDesc, Kit gameDefaultKit, GameWorld gameHost, int gameMaxPlayers, int gameMinPlayers,
			List<GameWorld> worlds, IGameEvents... events)
	{
		this._name = gameName;
		this._initials = initials;
		this._desc = gameDesc;
		this._defaultKit = gameDefaultKit;
		this._host = gameHost;
		this._maxplayer = gameMaxPlayers;
		this._minplayer = gameMinPlayers;
		this._worlds = worlds;
		this._events = events;
	}

	public abstract void loadGame();

	public void addBasicGameInfo()
	{
		Game game = GameEngine.getCurrentGame();
		game.setName(_name);
		game.setDesc(_desc);
		game.setDefaultKit(_defaultKit);
		game.setHost(_host);
		game.setMinPlayers(_minplayer);
		game.setMaxPlayers(_maxplayer);
	}

	private GameWorld nextMap(List<GameWorld> worlds)
	{
		for (GameWorld testing : worlds)
		{
			Chat.devMessage("The world " + testing.getName() + " has been registered");
		}
		this._worlds = worlds;
		GameWorld current = GameEngine.getCurrentGame().getHost();
		int position = worlds.indexOf(current) + 1;
		if (worlds.size() - 1 < position)
		{
			position = 0;
		}
		return worlds.get(position);
	}

	public void setNewHost()
	{
		GameEngine.getCurrentGame().getHost().unRegister();
		GameWorld newWorld = nextMap(_worlds);
		this._host = newWorld;
		for (IGameEvents e : _events)
		{
			e.setHostWorld(newWorld);
		}
		GameEngine.getCurrentGame().setHost(newWorld);
		GameEngine.Register(GameEngine.getCurrentGame().getHost().gameWorld());
	}

	public void addSpawn(Spawn spawn)
	{
		if (!_spawns.contains(spawn))
		{
			_spawns.add(spawn);
		}
	}

	public void addTeam(Team team)
	{
		for (Team teams : _teams)
		{
			if (teams.GetName() == team.GetName())
			{
				if (teams.GetHost().getName() == GameEngine.getCurrentGame().getName())
				{
					return;
				}
			}
		}
		_teams.add(team);
	}

	public void addKit(Kit kit)
	{
		for (Kit kits : _kits)
		{
			if (kits.getName() == kit.getName())
			{
				return;
			}
		}
		_kits.add(kit);
	}

	public void addWinner(Player player)
	{
		GameEngine.getCurrentGame().addWinner(player);
	}

	public boolean onWorld(Entity entity)
	{
		if (GameEngine.getCurrentGame().getState() != GameState.STARTED)
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
		Game.updateLists(_teams, _kits, _spawns, _defaultKit);
	}

	public void updateSpawns()
	{
		Game.replaceSpawn(_spawns);
	}

	public void registerEvents()
	{
		for (IGameEvents e : _events)
		{
			e.setHostWorld(_host);
			e.register();
		}
		Bukkit.getPluginManager().registerEvents(this, GameEngine.GetPlugin());
	}

	public void unRegisterEvents()
	{
		for (IGameEvents e : _events)
			e.unregister();
		HandlerList.unregisterAll(this);
	}

	/**
	 * returns false if entity is not a player<br>
	 * return false if the entity is a player but not a spectator<br>
	 * <br>
	 * Only use this when working with spectators! <br>
	 * Use the "OnWorld" Method when working with players/entities.
	 * 
	 * @param entity
	 * @return
	 */
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
		}
		return false;
	}

	@EventHandler
	public void onGameStateChange(gameStateChange event)
	{
		if (event.getToState() == GameState.STARTED)
		{
			for (Kit kit : getKits())
			{
				kit.loadAbilities();
			}
		}
	}

	@EventHandler
	public void onItemPickUp(PlayerPickupItemEvent event)
	{
		if (!validateSpectator(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}

	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (!validateSpectator(event.getPlayer()))
		{
			event.setCancelled(true);
			return;
		}
		if (GameEngine.getCurrentGame().getState() == GameState.STARTED)
		{
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if (GameEngine.getCurrentGame().getTeam(event.getPlayer()) != null)
				{
					if (event.getPlayer().getItemInHand().equals(GameEngine.getCurrentGame().getTeam(event.getPlayer()).TeamColorWool()))
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBowShot(EntityShootBowEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			if (!validateSpectator(event.getEntity()))
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event)
	{
		if (!validateSpectator(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockBlockBreak(BlockBreakEvent e)
	{
		Player player = e.getPlayer();
		if (!validateSpectator(player))
		{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void blockBlockPlace(BlockPlaceEvent e)
	{
		Player player = e.getPlayer();
		if (!validateSpectator(player))
		{
			e.setCancelled(true);
		}
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
				if (!validateSpectator(e.getDamager()))
				{
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void voidProtection(EntityDamageEvent e)
	{
		if (!validateSpectator(e.getEntity()))
		{
			if (e.getEntity() instanceof Player)
			{
				if (e.getCause().equals(DamageCause.VOID))
				{
					GameEngine.getCurrentGame().spectate((Player) e.getEntity());
					e.getEntity().teleport(GameEngine.getCurrentGame().getHost().getWorld().getSpawnLocation().add(0, 4, 0));
				}
			}
		}
	}

	@EventHandler
	public void fire(BlockIgniteEvent event)
	{
		try
		{
			if (!validateSpectator(event.getIgnitingEntity()))
			{
				if (true)
				{
					event.setCancelled(true);
				}
			}
		}
		catch (Exception e)
		{
		}
	}

	@EventHandler
	public void foodLevelChange(FoodLevelChangeEvent e)
	{
		if (!validateSpectator(e.getEntity()))
		{
			e.setFoodLevel(20);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event)
	{
		if (!validateSpectator(event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void entityGrief(EntityChangeBlockEvent e)
	{
		if (!validateSpectator(e.getEntity()))
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
		if (!validateSpectator(event.getWhoClicked()))
		{
			event.setCancelled(true);
		}
	}

	public String getName()
	{
		return _name;
	}

	public String getInitials()
	{
		return _initials;
	}

	public String[] getDesc()
	{
		return _desc;
	}

	public Kit getDefaultKit()
	{
		return _defaultKit;
	}

	public GameWorld getHost()
	{
		return _host;
	}

	public int getMinplayer()
	{
		return _minplayer;
	}

	public int getMaxplayer()
	{
		return _maxplayer;
	}

	public List<Player> getWinners()
	{
		return GameEngine.getCurrentGame().getWinners();
	}

	public ArrayList<Spawn> getSpawns()
	{
		return _spawns;
	}

	public ArrayList<Team> getTeams()
	{
		return _teams;
	}

	public ArrayList<Kit> getKits()
	{
		return _kits;
	}

	public Game getGame()
	{
		return GameEngine.getCurrentGame();
	}

	public IGameEvents[] getEvents()
	{
		return this._events;
	}

	public Location getCorner1()
	{
		return _corner1;
	}

	public Location getCorner2()
	{
		return _corner2;
	}

	public boolean getForcableTeams()
	{
		return _isForcingTeams;
	}

	public boolean getFreezingOnStart()
	{
		return _isFreezingOnStart;
	}

	public void setForcableTeams(boolean condition)
	{
		this._isForcingTeams = condition;
	}

	public void setFreezingOnStart(boolean condition)
	{
		this._isFreezingOnStart = condition;
	}

	public void setCorner1(Location corner1)
	{
		this._corner1 = corner1;
	}

	public void setCorner2(Location corner2)
	{
		this._corner2 = corner2;
	}
}
