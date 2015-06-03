package com.game.engine;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.game.engine.Commands.CommandManager;
import com.game.engine.Commands.Type.staff.GameControlCompletion;
import com.game.engine.Game.Game;
import com.game.engine.Game.GameManager;
import com.game.engine.GemHunt.GemHunt;
import com.game.engine.NMS.CustomNPCHandler;
import com.game.engine.NMS.CustomNPCType;

public class GameEngine extends JavaPlugin
{
	private static boolean debug = true;
	private static ChatColor C;
	private static Logger logger = Logger.getLogger("GameEngine");

	private static Game serverGame;

	public void onEnable()
	{
		Debug("Intializing GameEngine Components...");

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		serverGame = new GemHunt();
		CommandManager.getInstance();
		getCommand("game").setTabCompleter(new GameControlCompletion());
		//CustomNPCHandler.getInstance().removeAllEntity();
		GameManager.getInstance();
		GameManager.addCurrentGame(GameManager.getGame("GemHunt"));
		
		Register(getCurrentGame().GetLobby().gameWorld());
		Register(getCurrentGame().GetHost().gameWorld());
		//CustomNPCType.registerEntities();
		
		getCurrentGame().ChangeGame(GameManager.getGame("GemHunt"));
		
	}

	public void onDisable()
	{
		serverGame.Stop();
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
		logger.log(level, ChatColor.translateAlternateColorCodes('&', message));
	}

	public static void Debug(String message)
	{
		if (debug)
		{
			String formatted = C.BLUE + "Debug> " + C.WHITE + message;
			Bukkit.getConsoleSender().sendMessage(formatted);
		}
	}

	public static Game getCurrentGame()
	{
		return serverGame;
	}

	public static void setCurrentGame(Game game)
	{
		serverGame = game;
	}
}
