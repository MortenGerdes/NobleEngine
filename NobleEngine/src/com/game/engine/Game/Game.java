package com.game.engine.Game;

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
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.CustomEvents.gameChangeEvent;
import com.game.engine.CustomEvents.gameStateChange;
import com.game.engine.Menu.MenuManager;
import com.game.engine.NMS.CustomNPCHandler;
import com.game.engine.NMS.CustomNPCType;
import com.game.engine.ScoreBoard.ScoreBoardFactory;
import com.game.engine.ScoreBoard.playerScoreBoard;
import com.game.engine.Timer.StartedGameFreeze;
import com.game.engine.Util.FileUtils;
import com.game.engine.Util.FireworkBuilder;
import com.game.engine.Util.UtilPlayer;
import com.game.engine.Util.WorldUtil;

public abstract class Game implements Listener
{
	private GameState state;
	private String name;
	private String[] desc;
	private GameWorld lobby;
	private GameWorld host;
	private ItemStack teamWool;
	private Kit defaultKit;
	private Countdown countdown;
	private int maxPlayers;
	private int minPlayers;
	private int fireworks;
	private int task1;
	private int once = 0;
	private static List<Spawn> spawns = new ArrayList<>();
	private static List<Team> teams = new ArrayList<>();
	private static List<Kit> kits = new ArrayList<>();
	private List<Player> spectators = new ArrayList<>();
	private List<Player> winners = new ArrayList<>();
	//private List<ItemStack> MenuItems = new ArrayList<>();
	private String[] BlockedCommands =
	{ "plugins", "help", "pl", "about", "kill", "?", "ver", "version", "bukkit", "icanhasbukkit", "me", "banlist", };
	//private ArrayList<String> BC = new ArrayList<String>(Arrays.asList(BlockedCommands));
	private HashMap<Player, ScoreBoardFactory> panels = new HashMap<>();
	private static HashMap<Player, Kit> players = new HashMap<>();
	private static List<Player> FrozenPlayers = new ArrayList<>();

	/**
	 * Default Game Constructor. Events are registered from this constructor.
	 * 
	 * @param gameName
	 *            The display name of the game.
	 * @param gameDesc
	 *            The display description of the game.
	 * @param gameLobby
	 *            The world that contains the game lobby.
	 * @param gameHost
	 *            The world that contains the game map.
	 * @param gameMaxPlayers
	 *            The maximum amount of players that can join.
	 * @param gameMinPlayers
	 *            The minimum amount of players required to start.
	 */
	public Game(String gameName, String[] gameDesc, Kit gameDefaultKit, GameWorld gameLobby, GameWorld gameHost, int gameMaxPlayers, int gameMinPlayers)
	{
		GameEngine.Register(this);
		state = GameState.WAITING;
		name = gameName;
		desc = gameDesc;
		defaultKit = gameDefaultKit;
		lobby = gameLobby;
		host = gameHost;
		maxPlayers = gameMaxPlayers;
		minPlayers = gameMinPlayers;
		// Add the default kit as well.
		kits.add(defaultKit);
		GameEngine.Debug("Creating " + gameName + " game with " + maxPlayers + " players.");
		FileUtils.DownloadMaps(GetName(), true);
	}

	public abstract void onStopGame();

	public abstract void onStartGame();

	public abstract void onJoinGame(Player player);

	public abstract void onLeaveGame(Player player);

	public abstract void onWinGame(Player[] players);

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if (players.size() == maxPlayers)
		{
			player.kickPlayer("Game is full, please try again later.");
		}
		event.setJoinMessage(Chat.format("Join", player.getName() + " has joined the game."));
		Join(player);
		onJoinGame(player);
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		String command = event.getMessage();
		for (String block : BlockedCommands)
		{
			if (command.equalsIgnoreCase("/" + block))
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
		Leave(player);
		if (FrozenPlayers.contains(player))
		{
			FrozenPlayers.remove(player);
		}
	}

	@EventHandler
	public void onFrozenMove(PlayerMoveEvent event)
	{
		if (FrozenPlayers.contains(event.getPlayer()))
		{
			Location f = event.getFrom();
			Location t = event.getTo();
			if (f.getX() != t.getX() || f.getY() != t.getY() || f.getZ() != t.getZ())
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
			if (FrozenPlayers.contains(player))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onGameChange(gameChangeEvent event)
	{
		for(Team theTeams: GetTeams())
		{
			theTeams.Empty();
		}
	}
	
	@EventHandler
	public void onGameStart(gameStateChange event)
	{
		if(event.getToState() == GameState.STARTED)
		{
			if(GetHost().getWorld().getPlayers().isEmpty())
			{
				Bukkit.broadcastMessage(Chat.format("PlayerChecker", "&cNo Players online! Game Stopping!"));
				Stop();
			}
		}
	}

	public static void FreezeAllPlayersAtStart()
	{
		for (Player players : Bukkit.getOnlinePlayers())
		{
			FrozenPlayers.add(players);
		}
	}

	public static void UnFreezeAllPlayersAtStart()
	{
		FrozenPlayers.clear();
	}

	public void playerWin()
	{
		for (Player player : players.keySet())
		{
			player.sendMessage(Chat.format("Game", "Game ended. Winner: " + GetWinners().toString()));
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
		}
	}

	public void Start()
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GameEngine.GetPlugin(), new Runnable()
		{
			@Override
			public void run()
			{
				ChangeState(GameState.STARTED);
			}
		}, 20 * 10);
		
		for (Player player : players.keySet())
		{
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
			Kit kit = players.get(player);
			kit.Equip(player);
			AddTeamWool(player);
		}
		spawnPlayersEvenly();
		FreezeAllPlayersAtStart();
		StartedGameFreeze freezeTime = new StartedGameFreeze(10);
		freezeTime.GameFreeze();
		onStartGame();
	}

	public void respawnPlayer(Player player)
	{
		for (Spawn spawn : spawns)
		{
			if (spawn.GetTeam().equals(GetTeam(player)))
			{
				player.getInventory().clear();
				Kit kit = players.get(player);
				kit.Equip(player);
				AddTeamWool(player);
				player.teleport(spawn.GetLocation());
				break;
			}
		}
	}

	public void spawnPlayersEvenly()
	{
		Bukkit.broadcastMessage("Spawn: Starting to spawn players");
		for (Player player : GetPlayers().keySet())
		{
			for (Spawn spawn : GetSpawns())
			{
				Bukkit.broadcastMessage("Spawn: Looping though players");
				if (GetSpawns().indexOf(spawn) == GetSpawns().size() - 1)
				{
					for (Spawn spawns : GetSpawns())
					{
						Bukkit.broadcastMessage("Spawn: Setting spawn as taken");
						spawns.setIsTaken(false);
					}
				}

				if (spawn.getIsTaken() == false && GetTeam(player).equals(spawn.GetTeam()))
				{
					Bukkit.broadcastMessage("Spawn: Teleporting player" + player.getName() + " to a location in world " + spawn.GetLocation().getWorld().getName());
					player.teleport(spawn.GetLocation());
					spawn.setIsTaken(true);
					break;
				}
			}
		}
	}

	public void LobbyItems(Player player)
	{
		MenuManager mm = MenuManager.getInstance();
		for (ItemStack items : mm.getMenuItems())
		{
			player.getInventory().addItem(items);
		}
	}

	public void Prepare()
	{
		ChangeState(GameState.PREPARING);
		countdown = new Countdown(this, 15);
		countdown.runTaskTimer(GameEngine.GetPlugin(), 0, 20);
		RandomTeamSelect();
	}

	public void Stop()
	{
		ChangeState(GameState.ENDED);
		onStopGame();
		EndGameFireworks();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GameEngine.GetPlugin(), new Runnable()
		{
			@Override
			public void run()
			{
				
				for (Player players : Bukkit.getOnlinePlayers())
				{
					UtilPlayer.reset(players);
					lobby.Teleport(players);
					players.getInventory().clear();
					LobbyItems(players);
					if (GetTeam(players) != null)
					{
						GetTeam(players).Leave(players);
					}
					if (spectators.contains(players))
					{
						SpectatorManager.removeSpectator(players);
					}
				}
				ChangeState(GameState.WAITING);
			}
		}, 20 * 10);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				WorldUtil.deleteWorld(GameManager.getCurrentGameExtender().getHost().getName());
				GameManager.getCurrentGameExtender().setNewHost();
			}
		}.runTaskLater(GameEngine.GetPlugin(), 15 * 20);
	}

	public void EndGameFireworks()
	{
		fireworks = 47;
		final FireworkBuilder firework = new FireworkBuilder();
		firework.BuildFirework(Color.BLUE, Type.BALL_LARGE, true, false);
		task1 = GameEngine.GetPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(GameEngine.GetPlugin(), new Runnable()
		{
			public void run()
			{
				if (fireworks > 0)
				{
					Random x = new Random();
					Random z = new Random();
					int max = x.nextInt(120) - 60;
					int low = z.nextInt(120) - 60;
					firework.SpawnFirework(new Location(host.getWorld(), max, 30, low), 0);
					fireworks--;
					// Bukkit.getServer().broadcastMessage("Fireworks: " + fireworks + ". max: " + max + ". low: " + low);
				}
				if (fireworks < 0)
				{
					GameEngine.GetPlugin().getServer().getScheduler().cancelTask(task1);
				}
			}
		}, 20, 4);
	}

	public void Join(Player player)
	{
		if (!players.containsKey(player))
		{
			if (state == GameState.STARTED || state == GameState.ENDED)
			{
				Spectate(player);
				host.Teleport(player);
				return;
			}
			if (!players.containsKey(player))
			{
				players.put(player, defaultKit);
			}
			playerScoreBoard panel = new playerScoreBoard(player);
			panels.put(player, panel);
			UtilPlayer.reset(player);
			lobby.Teleport(player);
			LobbyItems(player);
			ScoreBoardFactory.globalScoreBoardUpdate();
			if (state == GameState.PREPARING)
			{
				SelectTeam(player);
			}
			GameEngine.Debug("Joining " + player.getName() + " on " + name + ".");
			if (state == GameState.WAITING && players.size() >= minPlayers)
			{
				Prepare();
			}
		}
	}

	public void Leave(Player player)
	{
		if (players.containsKey(player))
		{
			players.remove(player);
			GameEngine.Debug("Leaving " + player.getName() + " on " + name + ".");
			player.getInventory().clear();
			
			panels.remove(player);
			ScoreBoardFactory.globalScoreBoardUpdate();
			if (GetTeam(player) != null)
			{
				GetTeam(player).Leave(player);
			}
			if (spectators.contains(player))
			{
				SpectatorManager.removeSpectator(player);
			}
			if (players.size() < minPlayers && (getState() == GameState.PREPARING))
			{
				ChangeState(GameState.WAITING);
				countdown.countDownCancel();
			}
			if (players.size() == 0 && getState() == GameState.STARTED)
			{
				Stop();
			}
		}
	}

	public void ChangeGame(GameExtender thegame)
	{
		MenuManager mm = MenuManager.getInstance();
		gameChangeEvent event = new gameChangeEvent();
		mm.destroyInstance();
		//CustomNPCType.unRegisterEntities();
		//CustomNPCHandler.getInstance().removeAllEntitiesfromArrayList();
		GetHost().unRegister();
		if (GameManager.getCurrentGameExtender() != null)
		{
			GameManager.getCurrentGameExtender().unRegisterEvents();
			GameEngine.Debug("Unregistering event for " + ChatColor.GOLD + GameManager.getCurrentGameExtender().getName());
		}
		
		Bukkit.getServer().getPluginManager().callEvent(event);
		GameManager.addCurrentGame(thegame);
		
		if(once > 0)
		{
			GameEngine.Debug("LOADING GAME.........");
			thegame.loadGame();
		}
		once++;
		thegame.updateGame();
		GameEngine.Register(GetHost().gameWorld());
		//CustomNPCType.registerEntities();
		thegame.registerEvents();
		for (Player onlineplayers : Bukkit.getOnlinePlayers())
		{
			lobby.Teleport(onlineplayers);
			onlineplayers.playSound(onlineplayers.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
			onlineplayers.getInventory().clear();
			LobbyItems(onlineplayers);
		}
		ScoreBoardFactory.globalScoreBoardUpdate();
	}

	public void SelectTeam(Player player)
	{
		Random random = new Random();
		Team team = teams.get(random.nextInt(teams.size()));
		team.Join(player);
	}

	public void AdvancedSelectTeam(Player player, String teamname)
	{
		Team team = toTeam(teamname);
		int PlayersOnTeam = team.GetThePlayers().size();
		int maxteams = 0;
		int addition = 0;
		if (GetTeam(player) != null)
		{
			addition = 1;
			if (GetTeam(player).equals(team))
			{
				player.sendMessage(Chat.format("TeamSelector", "You are already on " + team.GetColor() + team.GetName()));
				return;
			}
		}
		if (GetTeams().size() == 1)
		{
			if (GetTeam(player) != null)
			{
				GetTeam(player).Leave(player);
			}
			team.Join(player);
			return;
		}
		if (PlayersOnTeam == 0)
		{
			if (GetTeam(player) != null)
			{
				GetTeam(player).Leave(player);
			}
			team.Join(player);
			return;
		}
		for (Team teams : GetTeams())
		{
			maxteams++;
			if (teams.equals(team))
			{
				if (GetTeams().size() == maxteams)
				{
					player.sendMessage(Chat.format("GameEngine", "This team is too full!"));
					return;
				}
				continue;
			}
			if (teams.GetThePlayers().size() >= PlayersOnTeam + addition)
			{
				if (GetTeam(player) != null)
				{
					GameEngine.Debug("player " + player.getName() + " was removed from " + GetTeam(player).GetColor() + GetTeam(player).GetName());
					GetTeam(player).Leave(player);
				}
				team.Join(player);
				return;
			}
			if (GetTeams().size() == maxteams)
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
	
	public void RandomTeamSelect()
	{
		ArrayList<Player> PlayersSelect = new ArrayList<Player>();
		for (Player OnlinePlayers : Bukkit.getOnlinePlayers())
		{
			if (GetTeam(OnlinePlayers) == null)
			{
				PlayersSelect.add(OnlinePlayers);
			}
		}
		Collections.shuffle(PlayersSelect);
		while (!PlayersSelect.isEmpty())
		{
			for (Team AvailableTeams : teams)
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

	public void Spectate(Player player)
	{
		if (players.containsKey(player) && !spectators.contains(player))
		{
			players.remove(player);
			spectators.add(player);
			SpectatorManager.addSpectator(player);
		}
	}

	public void RemoveSpectate(Player player)
	{
		if (spectators.contains(player) && !players.containsKey(player))
		{
			spectators.remove(player);
		}
	}

	private void AddTeamWool(Player player)
	{
		player.getInventory().setItem(8, GetTeam(player).TeamColorWool());
	}

	public Team GetTeam(Player player)
	{
		for (Team team : teams)
		{
			if (team.GetThePlayers().contains(player)) return team;
		}
		return null;
	}

	public void SetTeam(Player player, Team team)
	{
		for (Team TheTeams : teams)
		{
			if (TheTeams.GetName().contains(team.GetName()))
			{
				TheTeams.Join(player);
			}
		}
	}

	public void SetTeam(Player player, String team)
	{
		for (Team TheTeams : teams)
		{
			if (TheTeams.GetName().contains(team))
			{
				TheTeams.Join(player);
			}
		}
	}

	public static Team toTeam(String team)
	{
		for (Team TheTeams : teams)
		{
			if (TheTeams.GetName().contains(team))
			{
				return TheTeams;
			}
		}
		return null;
	}

	public void SetKit(Player player, Kit kit)
	{
		if (players.containsKey(player))
		{
			players.remove(player);
			players.put(player, kit);
		}
		else
		{
			players.put(player, kit);
		}
		player.sendMessage(Chat.format("GameEngine", "You have selected &a" + kit.GetName() + "&f"));
	}

	public ItemStack getTeamWool()
	{
		return teamWool;
	}

	public void AddWinner(Player player)
	{
		if (!winners.contains(player))
		{
			winners.contains(player);
		}
	}

	public void AddTeam(Team team)
	{
		if (!teams.contains(team))
		{
			teams.add(team);
		}
	}

	public void AddKit(Kit kit)
	{
		if (!kits.contains(kit))
		{
			kits.add(kit);
		}
	}

	public void AddSpawn(Spawn spawn)
	{
		if (!spawns.contains(spawn))
		{
			spawns.add(spawn);
		}
	}

	public static void replaceTeam(ArrayList<Team> list)
	{
		teams.clear();
		for (Team team : list)
		{
			teams.add(team);
		}
	}

	public static void replaceKit(ArrayList<Kit> list)
	{
		kits.clear();
		for (Kit kit : list)
		{
			kits.add(kit);
		}
	}

	public static void replaceSpawn(ArrayList<Spawn> list)
	{
		spawns.clear();
		for (Spawn spawn : list)
		{
			spawns.add(spawn);
		}
	}

	public static void replaceDefaultKit(Kit kit)
	{
		players.clear();
		for (Player thePlayer : Bukkit.getOnlinePlayers())
		{
			if (!players.containsKey(thePlayer))
			{
				players.put(thePlayer, kit);
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

	public void ChangeState(GameState gameState)
	{
		if (gameState != state)
		{
			gameStateChange event = new gameStateChange(gameState);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (!event.isCancelled())
			{
				state = gameState;
				GameEngine.Debug("Changing game state for " + name + " to " + state.toString());
			}
		}
	}

	public Game getGame()
	{
		return Game.this;
	}

	public Kit getPlayerKit(Player player)
	{
		if (players.containsKey(player))
		{
			return players.get(player);
		}
		return null;
	}

	public GameState getState()
	{
		return state;
	}

	public String GetName()
	{
		return name;
	}

	public String[] GetDesc()
	{
		return desc;
	}

	public GameWorld GetLobby()
	{
		return lobby;
	}

	public GameWorld GetHost()
	{
		return host;
	}

	public int GetMaxPlayers()
	{
		return maxPlayers;
	}

	public int GetMinPlayers()
	{
		return minPlayers;
	}

	public Kit GetDefaultKit()
	{
		return defaultKit;
	}

	public List<Spawn> GetSpawns()
	{
		return spawns;
	}

	public List<Team> GetTeams()
	{
		return teams;
	}

	public List<Kit> GetKits()
	{
		return kits;
	}

	public HashMap<Player, Kit> GetPlayers()
	{
		return players;
	}

	public List<Player> GetSpectators()
	{
		return spectators;
	}

	public List<Player> GetWinners()
	{
		return winners;
	}

	public HashMap<Player, ScoreBoardFactory> GetPanels()
	{
		return panels;
	}

	public static List<Player> getFrozenPlayers()
	{
		return FrozenPlayers;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setDesc(String[] desc)
	{
		this.desc = desc;
	}

	public void setLobby(GameWorld lobby)
	{
		this.lobby = lobby;
	}

	public void setHost(GameWorld host)
	{
		this.host = host;
	}

	public void setDefaultKit(Kit defaultKit)
	{
		this.defaultKit = defaultKit;
	}

	public void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}

	public void setMinPlayers(int minPlayers)
	{
		this.minPlayers = minPlayers;
	}
}
