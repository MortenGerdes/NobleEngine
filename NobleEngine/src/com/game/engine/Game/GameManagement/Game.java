package com.game.engine.Game.GameManagement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.game.engine.GameEngine;
import com.game.engine.Ability.AbilityManager;
import com.game.engine.Chat.Chat;
import com.game.engine.CustomEvents.gameChangeEvent;
import com.game.engine.CustomEvents.gameStateChange;
import com.game.engine.Game.Kit;
import com.game.engine.Game.Spawn;
import com.game.engine.Game.SpectatorManager;
import com.game.engine.Game.Team;
import com.game.engine.Menu.MenuManager;
import com.game.engine.ScoreBoard.ScoreBoardFactory;
import com.game.engine.ScoreBoard.playerScoreBoard;
import com.game.engine.Timer.Countdown;
import com.game.engine.Timer.StartedGameFreeze;
import com.game.engine.Util.FireworkBuilder;
import com.game.engine.Util.UtilPlayer;
import com.game.engine.Util.WorldUtil;

public class Game implements Listener
{
	// Settings
	private boolean _freezePlayersAtStart;
	private boolean _isGameJoinable = true;
	
	//Class related
	private int _maxPlayers;
	private int _minPlayers;
	private int _fireworks;
	private int _task1;
	private int _once = 0;
	private GameState _state;
	private ItemStack _teamWool;
	private Kit _defaultKit;
	private Countdown _countdown;
	
	//Game related
	private String _name;
	private String[] _desc;
	private String[] _blockedCommands = { "plugins", "help", "pl", "about", "kill", "?", "ver", "version", "bukkit", "icanhasbukkit", "me", "banlist" };
	private GameWorld _lobby;
	private GameWorld _host;
	
	//Lists
	private List<Player> _spectators = new ArrayList<>();
	private List<Player> _winners = new ArrayList<>();
	private HashMap<Player, ScoreBoardFactory> _panels = new HashMap<>();
	private static HashMap<Player, Kit> _players = new HashMap<>();
	private static List<Player> _frozenPlayers = new ArrayList<>();
	private static List<Spawn> _spawns = new ArrayList<>();
	private static List<Team> _teams = new ArrayList<>();
	private static List<Kit> _kits = new ArrayList<>();

	public Game(String gameName, String[] gameDesc, Kit gameDefaultKit, GameWorld gameLobby, GameWorld gameHost, int gameMaxPlayers, int gameMinPlayers)
	{
		GameEngine.Register(this);
		_state = GameState.WAITING;
		_name = gameName;
		_desc = gameDesc;
		_defaultKit = gameDefaultKit;
		_lobby = gameLobby;
		_host = gameHost;
		_maxPlayers = gameMaxPlayers;
		_minPlayers = gameMinPlayers;
		_kits.add(_defaultKit);
	}
	public static void freezeAllPlayersAtStart()
	{
		for (Player players : _players.keySet())
		{
			_frozenPlayers.add(players);
		}
	}

	public static void unFreezeAllPlayersAtStart()
	{
		_frozenPlayers.clear();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
//		if (_players.size() == _maxPlayers)
//		{
//			player.kickPlayer("Game is full, please try again later.");
//			return;
//		}
		event.setJoinMessage(Chat.format("Join", player.getName() + " has joined the game."));
		join(player);
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		String command = event.getMessage();
		
		if(player.isOp())
		{
			return;
		}
		
		for (String block : _blockedCommands)
		{
			if (command.equalsIgnoreCase("/" + block) || command.contains("minecraft"))
			{
				GameEngine.Debug("Player " + player.getName() + " tried to run the " + event.getMessage() + " command");
				if (command.equals("/help"))
				{
					player.sendMessage(Chat.format("GameEngine", "This game is &6powered &7 by &aThe GameEngine"));
				}
				else
				{
					player.sendMessage(Chat.format("GameEngine", "Sorry but the command &c'" + event.getMessage() + "' &7is blocked"));
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		event.setQuitMessage(Chat.format("Quit", player.getName() + " has left the game."));
		leave(player);
		if (_frozenPlayers.contains(player))
		{
			_frozenPlayers.remove(player);
		}
	}

	@EventHandler
	public void onFrozenMove(PlayerMoveEvent event)
	{
		if (_frozenPlayers.contains(event.getPlayer()))
		{
			Location f = event.getFrom();
			Location t = event.getTo();
			if (f.getX() != t.getX() || f.getZ() != t.getZ())
			{
				event.getPlayer().teleport(f);
			}
		}
	}

	@EventHandler
	public void cancelDamageOnFrozenPlayers(EntityDamageEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			if (_frozenPlayers.contains(player))
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onGameChange(gameChangeEvent event)
	{
		for (Team theTeams : getTeams())
		{
			theTeams.Empty();
		}
	}
	
	@EventHandler
	public void onGameEnd(gameStateChange event)
	{
		if(event.getFromState() == GameState.ENDED)
		{
			GameManager.getCurrentGameExtender().unRegisterEvents();
		}
	}

	@EventHandler
	public void onGameStart(gameStateChange event)
	{
		if (event.getToState() == GameState.STARTED)
		{
			if (getHost().getWorld().getPlayers().isEmpty())
			{
				Bukkit.broadcastMessage(Chat.format("PlayerChecker", "&cNo Players online! Game Stopping!"));
				stop();
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void playerChatFormat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		
		if(player.getName().contentEquals("Morten"))
		{
			event.setFormat(Chat.structure("DEV", ChatColor.RED, player.getName(), event.getMessage()));
		}
		else if(player.isOp())
		{
			event.setFormat(Chat.structure("OP", ChatColor.GOLD, player.getName(), event.getMessage()));
		}
		else
		{
			event.setFormat(Chat.structure("", ChatColor.WHITE, player.getName(), event.getMessage()));
		}
	}

	
	@EventHandler
	public void basicGameRotation(gameStateChange event)
	{
		if(event.getFromState() == GameState.ENDED && event.getToState() == GameState.WAITING)
		{
			if(GameManager.getCurrentGameExtender().getName() == "NobleRunner")
			{
				Bukkit.broadcastMessage(Chat.format("GameRotation", "Changing game to &aNobleSpleef &ein 5 seconds"));
				
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						// Double checking since an OP could have changed the game in the meanwhile
						if(GameManager.getCurrentGameExtender().getName() == "NobleRunner")
						{
							changeGame(GameManager.getGame("NobleSpleef"));
						}
					}
				}.runTaskLater(GameEngine.GetPlugin(), 20*7);

			}
			if(GameManager.getCurrentGameExtender().getName() == "NobleSpleef")
			{
				Bukkit.broadcastMessage(Chat.format("GameRotation", "Changing game to &aNobleRunner &ein 5 seconds"));
				
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						if(GameManager.getCurrentGameExtender().getName() == "NobleSpleef")
						{
							changeGame(GameManager.getGame("NobleRunner"));
						}
					}
				}.runTaskLater(GameEngine.GetPlugin(), 20*7);
				
			}
		}
	}

	public void playerWin()
	{
		for (Player player : _players.keySet())
		{
			player.sendMessage(Chat.format("Game", "Game ended. Winner: " + getWinners().toString()));
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
		}
	}

	public void prepare()
	{
		prepare(15);	
	}
	
	public void prepare(int time)
	{
		changeState(GameState.PREPARING);
		_countdown = new Countdown(this, time);
		_countdown.runTaskTimer(GameEngine.GetPlugin(), 0, 20);
		randomTeamSelect();
	}

	public void start()
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GameEngine.GetPlugin(), new Runnable()
		{
			@Override
			public void run()
			{
				changeState(GameState.STARTED);
			}
		}, 20 * 10);
		for (Player player : _players.keySet())
		{
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
			Kit kit = _players.get(player);
			kit.equip(player);
			addTeamWool(player);
		}
		GameManager.getCurrentGameExtender().registerEvents();
		spawnPlayersEvenly();
		
		if (GameManager.getCurrentGameExtender().getFreezingOnStart() == true)
		{
			freezeAllPlayersAtStart();
		}
		setIsGameJoinable(false);
		StartedGameFreeze freezeTime = new StartedGameFreeze(10);
		freezeTime.GameFreeze();
	}
	
	public void stop()
	{
		changeState(GameState.ENDED);
		endGameFireworks();
		AbilityManager.getInstance().getAvailableAbilities().clear();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GameEngine.GetPlugin(), new Runnable()
		{
			@Override
			public void run()
			{
				for (Player thePlayer : Bukkit.getOnlinePlayers())
				{
					if (_spectators.contains(thePlayer))
					{
						_spectators.remove(thePlayer);
						SpectatorManager.removeSpectator(thePlayer);
						if (!_players.containsKey(thePlayer))
						{
							_players.put(thePlayer, _defaultKit);
						}
					}
					
					UtilPlayer.reset(thePlayer);
					_lobby.teleport(thePlayer);
					thePlayer.getInventory().clear();
					lobbyItems(thePlayer);
				}
				setIsGameJoinable(true);
				changeState(GameState.WAITING);
			}
		}, 20 * 10);
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						startGameIfEnoughPlayers();
					}
				}.runTaskLater(GameEngine.GetPlugin(), 20 * 5);
				
				WorldUtil.deleteWorld(GameManager.getCurrentGameExtender().getHost().getName());
				GameManager.getCurrentGameExtender().setNewHost();
			}
		}.runTaskLater(GameEngine.GetPlugin(), 20 * 15);
	}

	public void respawnPlayer(Player player)
	{
		for (Spawn spawn : _spawns)
		{
			if (spawn.GetTeam().equals(getTeam(player)))
			{
				player.getInventory().clear();
				Kit kit = _players.get(player);
				kit.equip(player);
				addTeamWool(player);
				player.teleport(spawn.GetLocation());
				break;
			}
		}
	}

	public void respawnPlayer(Player player, Location location)
	{
		player.getInventory().clear();
		Kit kit = _players.get(player);
		kit.equip(player);
		addTeamWool(player);
		player.teleport(location);
	}

	public void spawnPlayersEvenly()
	{
		for (Player player : getPlayers().keySet())
		{
			for (Spawn spawn : getSpawns())
			{
				if (getSpawns().indexOf(spawn) == getSpawns().size() - 1)
				{
					for (Spawn spawns : getSpawns())
					{
						spawns.setIsTaken(false);
					}
				}
				if (spawn.getIsTaken() == false && getTeam(player).GetName() == (spawn.GetTeam().GetName()))
				{
					player.teleport(spawn.GetLocation());
					spawn.setIsTaken(true);
					break;
				}
			}
		}
		
//		if(GameEngine.getCurrentGame().getLobby().getPlayerSize() != 0)
//		{
//			for(Player player: getPlayers().keySet())
//			{
//				if(player.getWorld().getName() == getLobby().getWorld().getName())
//				{
//					for(Spawn spawn: getSpawns())
//					{
//						if(spawn.GetTeam().GetName() == getTeam(player).GetName())
//						{
//							player.teleport(spawn.GetLocation());
//							break;
//						}
//					}
//				}
//					
//			}
//		}
	}

	public void lobbyItems(Player player)
	{
		MenuManager mm = MenuManager.getInstance();
		for (ItemStack items : mm.getMenuItems())
		{
			player.getInventory().addItem(items);
		}
	}

	public void startGameIfEnoughPlayers()
	{
		if (getState() == GameState.WAITING)
		{
			if (_players.size() >= _minPlayers)
			{
				prepare();
			}
			else
			{
				Bukkit.broadcastMessage(Chat.format(_name, "Waiting for more players to join"));
			}
		}
	}
	
	public void stopPreparingCountDown()
	{
		if(getState() == GameState.PREPARING)
		{
			changeState(GameState.WAITING);
			_countdown.countDownCancel();
		}
	}

	public void endGameFireworks()
	{
		_fireworks = 47;
		final FireworkBuilder firework = new FireworkBuilder();
		firework.BuildFirework(Color.BLUE, Type.BALL_LARGE, true, false);
		
		_task1 = GameEngine.GetPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(GameEngine.GetPlugin(), new Runnable()
		{
			public void run()
			{
				if (_fireworks > 0)
				{
					Random x = new Random();
					Random z = new Random();
					int max = x.nextInt(120) - 60;
					int low = z.nextInt(120) - 60;
					firework.SpawnFirework(new Location(_host.getWorld(), max, 30, low), 0);
					_fireworks--;
				}
				if (_fireworks < 0)
				{
					GameEngine.GetPlugin().getServer().getScheduler().cancelTask(_task1);
				}
			}
		}, 20, 4);
	}

	public void join(Player player)
	{
		if (!_players.containsKey(player))
		{
			if(_isGameJoinable == true && getState() == GameState.PREPARING)
			{
				selectTeam(player);
			}
			else if(_isGameJoinable == false && getState() == GameState.PREPARING)
			{
				spectate(player);
				_host.teleport(player);
				return;
			}
			if (_state == GameState.STARTED || _state == GameState.ENDED)
			{
				spectate(player);
				_host.teleport(player);
				return;
			}
			if (!_players.containsKey(player))
			{
				_players.put(player, _defaultKit);
			}
			
			playerScoreBoard panel = new playerScoreBoard(player);
			_panels.put(player, panel);
			UtilPlayer.reset(player);
			_lobby.teleport(player);
			lobbyItems(player);
			ScoreBoardFactory.globalScoreBoardUpdate();
			GameEngine.Debug("Joining " + player.getName() + " on " + _name + ".");
			if (_state == GameState.WAITING && _players.size() >= _minPlayers)
			{
				prepare(20);
			}
		}
	}

	public void leave(Player player)
	{
		if (_players.containsKey(player))
		{
			_players.remove(player);
			_panels.remove(player);
			player.getInventory().clear();
			ScoreBoardFactory.globalScoreBoardUpdate();
			if (getTeam(player) != null)
			{
				getTeam(player).Leave(player);
			}
			if (_spectators.contains(player))
			{
				_spectators.remove(player);
				SpectatorManager.removeSpectator(player);
			}
			if (_players.size() < _minPlayers && (getState() == GameState.PREPARING))
			{
				changeState(GameState.WAITING);
				_countdown.countDownCancel();
			}
			if (_players.size() == 0 && getState() == GameState.STARTED)
			{
				stop();
			}
		}
	}

	public void changeGame(GameExtender theGame)
	{
		MenuManager mm = MenuManager.getInstance();
		gameChangeEvent event = new gameChangeEvent(theGame);
		mm.destroyInstance();
		AbilityManager.getInstance().getAvailableAbilities().clear();
		getHost().unRegister();	
		
		Bukkit.getServer().getPluginManager().callEvent(event);
		GameManager.addCurrentGame(theGame);
		if (_once > 0)
		{
			GameEngine.Debug("LOADING GAME.........");
			theGame.loadGame();
		}
		_once++;
		theGame.updateGame();
		GameEngine.Register(getHost().gameWorld());
		
		for (Player onlineplayers : Bukkit.getOnlinePlayers())
		{
			_lobby.teleport(onlineplayers);
			onlineplayers.playSound(onlineplayers.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
			onlineplayers.getInventory().clear();
			lobbyItems(onlineplayers);
		}
		ScoreBoardFactory.globalScoreBoardUpdate();
	}

	public void selectTeam(Player player)
	{
		Random random = new Random();
		Team team = _teams.get(random.nextInt(_teams.size()));
		team.Join(player);
	}
	
	public void advancedSelectTeam(Player player, String teamname)
	{
		Team team = toTeam(teamname);
		int PlayersOnTeam = team.GetThePlayers().size();
		int MaxTeams = 0;
		int Addition = 0;
		if (GameManager.getCurrentGameExtender().getForcableTeams() == false)
		{
			if (getTeam(player) != null)
			{
				if (getTeam(player).equals(team))
				{
					player.sendMessage(Chat.format("TeamSelector", "You are already on " + team.GetColor() + team.GetName()));
					return;
				}
			}
			if (getTeam(player) != null)
			{
				getTeam(player).Leave(player);
			}
			team.Join(player);
			return;
		}
		
		if (getTeam(player) != null)
		{
			Addition = 1;
			if (getTeam(player).equals(team))
			{
				player.sendMessage(Chat.format("TeamSelector", "You are already on " + team.GetColor() + team.GetName()));
				return;
			}
		}
		if (getTeams().size() == 1)
		{
			if (getTeam(player) != null)
			{
				getTeam(player).Leave(player);
			}
			team.Join(player);
			return;
		}
		if (PlayersOnTeam == 0)
		{
			if (getTeam(player) != null)
			{
				getTeam(player).Leave(player);
			}
			team.Join(player);
			return;
		}
		for (Team teams : getTeams())
		{
			MaxTeams++;
			if (teams.equals(team))
			{
				if (getTeams().size() == MaxTeams)
				{
					player.sendMessage(Chat.format("GameEngine", "This team is too full!"));
					return;
				}
				continue;
			}
			if (teams.GetThePlayers().size() >= PlayersOnTeam + Addition)
			{
				if (getTeam(player) != null)
				{
					GameEngine.Debug("player " + player.getName() + " was removed from " + getTeam(player).GetColor() + getTeam(player).GetName());
					getTeam(player).Leave(player);
				}
				team.Join(player);
				return;
			}
			if (getTeams().size() == MaxTeams)
			{
				player.sendMessage(Chat.format("GameEngine", "This team is too full!"));
				return;
			}
			else
			{
				continue;
			}
		}
	}

	public void randomTeamSelect()
	{
		ArrayList<Player> PlayersSelect = new ArrayList<Player>();
		for (Player OnlinePlayers : Bukkit.getOnlinePlayers())
		{
			if (getTeam(OnlinePlayers) == null)
			{
				PlayersSelect.add(OnlinePlayers);
			}
		}
		Collections.shuffle(PlayersSelect);
		while (!PlayersSelect.isEmpty())
		{
			for (Team AvailableTeams : _teams)
			{
				if (!PlayersSelect.isEmpty())
				{
					Random random = new Random();
					Player ThePlayer = PlayersSelect.get(random.nextInt(PlayersSelect.size()));
					if (AvailableTeams.GetThePlayers().contains(ThePlayer))
					{
						PlayersSelect.remove(ThePlayer);
						continue;
					}
					AvailableTeams.Join(ThePlayer);
					PlayersSelect.remove(ThePlayer);
				}
			}
		}
	}

	public void spectate(Player player)
	{
		if (!_spectators.contains(player))
		{
			if (_players.containsKey(player))
			{
				_players.remove(player);
			}
			_spectators.add(player);
			SpectatorManager.addSpectator(player);
		}
	}

	public void removeSpectate(Player player)
	{
		if (_spectators.contains(player) && !_players.containsKey(player))
		{
			_spectators.remove(player);
		}
	}

	private void addTeamWool(Player player)
	{
		player.getInventory().setItem(8, getTeam(player).TeamColorWool());
	}

	public Team getTeam(Player player)
	{
		for (Team team : _teams)
		{
			if (team.GetThePlayers().contains(player)) return team;
		}
		return null;
	}

	public void setTeam(Player player, Team team)
	{
		for (Team TheTeams : _teams)
		{
			if (TheTeams.GetName().contains(team.GetName()))
			{
				TheTeams.Join(player);
			}
		}
	}

	public void setTeam(Player player, String team)
	{
		for (Team TheTeams : _teams)
		{
			if (TheTeams.GetName().contains(team))
			{
				TheTeams.Join(player);
			}
		}
	}

	public static Team toTeam(String team)
	{
		for (Team TheTeams : _teams)
		{
			if (TheTeams.GetName().contains(team))
			{
				return TheTeams;
			}
		}
		return null;
	}

	public void setKit(Player player, Kit kit)
	{
		if (_players.containsKey(player))
		{
			_players.remove(player);
			_players.put(player, kit);
		}
		else
		{
			_players.put(player, kit);
		}
		player.sendMessage(Chat.format("GameEngine", "You have selected &a" + kit.getName() + "&f"));
	}

	public ItemStack getTeamWool()
	{
		return _teamWool;
	}

	public void addWinner(Player player)
	{
		if (!_winners.contains(player))
		{
			_winners.contains(player);
		}
	}

	public void addTeam(Team team)
	{
		if (!_teams.contains(team))
		{
			_teams.add(team);
		}
	}

	public void addKit(Kit kit)
	{
		if (!_kits.contains(kit))
		{
			_kits.add(kit);
		}
	}

	public void addSpawn(Spawn spawn)
	{
		if (!_spawns.contains(spawn))
		{
			_spawns.add(spawn);
		}
	}

	public static void replaceTeam(ArrayList<Team> list)
	{
		_teams.clear();
		for (Team team : list)
		{
			_teams.add(team);
		}
	}

	public static void replaceKit(ArrayList<Kit> list)
	{
		_kits.clear();
		for (Kit kit : list)
		{
			_kits.add(kit);
		}
	}

	public static void replaceSpawn(ArrayList<Spawn> list)
	{
		_spawns.clear();
		for (Spawn spawn : list)
		{
			_spawns.add(spawn);
		}
	}

	public static void replaceDefaultKit(Kit kit)
	{
		_players.clear();
		for (Player thePlayer : Bukkit.getOnlinePlayers())
		{
			if (!_players.containsKey(thePlayer))
			{
				_players.put(thePlayer, kit);
			}
		}
	}

	public static void updateLists(ArrayList<Team> Lteams, ArrayList<Kit> Lkits, ArrayList<Spawn> Lspawns, Kit defaultKit)
	{
		replaceTeam(Lteams);
		replaceKit(Lkits);
		replaceSpawn(Lspawns);
		replaceDefaultKit(defaultKit);
	}

	public void changeState(GameState gameState)
	{
		if (gameState != _state)
		{
			gameStateChange event = new gameStateChange(gameState);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (!event.isCancelled())
			{
				_state = gameState;
				GameEngine.Debug("Changing game state for " + _name + " to " + _state.toString());
			}
		}
	}

	public Game getGame()
	{
		return Game.this;
	}

	public Kit getPlayerKit(Player player)
	{
		if (_players.containsKey(player))
		{
			return _players.get(player);
		}
		return null;
	}

	public GameState getState()
	{
		return _state;
	}

	public String getName()
	{
		return _name;
	}

	public String[] getDesc()
	{
		return _desc;
	}

	public GameWorld getLobby()
	{
		return _lobby;
	}

	public GameWorld getHost()
	{
		return _host;
	}

	public int getMaxPlayers()
	{
		return _maxPlayers;
	}

	public int getMinPlayers()
	{
		return _minPlayers;
	}

	public Kit getDefaultKit()
	{
		return _defaultKit;
	}

	public List<Spawn> getSpawns()
	{
		return _spawns;
	}

	public List<Team> getTeams()
	{
		return _teams;
	}

	public List<Kit> getKits()
	{
		return _kits;
	}

	public HashMap<Player, Kit> getPlayers()
	{
		return _players;
	}

	public List<Player> getSpectators()
	{
		return _spectators;
	}

	public List<Player> getWinners()
	{
		return _winners;
	}
	
	public boolean getIsGameJoinable()
	{
		return _isGameJoinable;
	}

	public HashMap<Player, ScoreBoardFactory> getPanels()
	{
		return _panels;
	}

	public boolean getFreezingOnStart()
	{
		return _freezePlayersAtStart;
	}
	
	public static List<Player> getFrozenPlayers()
	{
		return _frozenPlayers;
	}

	public void setName(String name)
	{
		this._name = name;
	}

	public void setDesc(String[] desc)
	{
		this._desc = desc;
	}

	public void setLobby(GameWorld lobby)
	{
		this._lobby = lobby;
	}

	public void setHost(GameWorld host)
	{
		this._host = host;
	}

	public void setDefaultKit(Kit defaultKit)
	{
		this._defaultKit = defaultKit;
	}

	public void setMaxPlayers(int maxPlayers)
	{
		this._maxPlayers = maxPlayers;
	}

	public void setMinPlayers(int minPlayers)
	{
		this._minPlayers = minPlayers;
	}
	
	public void setIsGameJoinable(boolean condition)
	{
		this._isGameJoinable = condition;
	}
	
}
