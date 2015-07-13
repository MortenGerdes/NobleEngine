package com.game.engine;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.game.engine.Ability.AbilityManager;
import com.game.engine.Commands.CommandManager;
import com.game.engine.Commands.Type.staff.GameControlCompletion;
import com.game.engine.Game.GameManagement.Game;
import com.game.engine.Game.GameManagement.GameManager;
import com.game.engine.GameInstance.GameIns;

public class GameEngine extends JavaPlugin
{
	private static boolean _debug = true;
	private static ChatColor _C;
	private static Logger _logger = Logger.getLogger("GameEngine");

	private static Game _serverGame;

	public void onEnable()
	{
		Debug("Intializing GameEngine Components...");

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		_serverGame = new GameIns();
		CommandManager.getInstance();
		getCommand("game").setTabCompleter(new GameControlCompletion());
		//CustomNPCHandler.getInstance().removeAllEntity();
		GameManager.getInstance();
		GameManager.addCurrentGame(GameManager.getGame("GemHunt"));
		AbilityManager.getInstance().registerAvailableAbilities();
		
		Register(getCurrentGame().getLobby().gameWorld());
		Register(getCurrentGame().getHost().gameWorld());
		//CustomNPCType.registerEntities();
		
		getCurrentGame().changeGame(GameManager.getGame("NobleSpleef"));
		getCurrentGame().changeGame(GameManager.getGame("NobleSpleef"));
		
	}

	public void onDisable()
	{
		_serverGame.stop();
		//CustomNPCType.unRegisterEntities();
	}

	public static Plugin GetPlugin()
	{
		return Bukkit.getPluginManager().getPlugin("GameEngine");
	}

	public static void Register(Listener listener)
	{
		Debug("Registering events for " + listener.getClass().getSimpleName() + " class.");
		Bukkit.getPluginManager().registerEvents(listener, GetPlugin());
	}
	
	public static void unRegister(Listener listener)
	{
		HandlerList.unregisterAll(listener);
	}

	public static void Log(String message, Level level)
	{
		_logger.log(level, ChatColor.translateAlternateColorCodes('&', message));
	}

	public static void Debug(String message)
	{
		if (_debug)
		{
			String formatted = _C.BLUE + "Debug> " + _C.WHITE + message;
			Bukkit.getConsoleSender().sendMessage(formatted);
		}
	}

	public static Game getCurrentGame()
	{
		return _serverGame;
	}

	public static void setCurrentGame(Game game)
	{
		_serverGame = game;
	}
}
