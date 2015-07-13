package com.game.engine.Game.GameManagement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.GameTypes.gemhunt.GemHuntGame;
import com.game.engine.GameTypes.noblerunner.NobleRunnerGame;
import com.game.engine.GameTypes.noblespleef.NobleSpleefGame;
import com.game.engine.GameTypes.sillyslap.SillySlapGame;
import com.game.engine.GameTypes.sillyslap.SillySlapTeamGame;
import com.game.engine.Util.FileUtils;

public class GameManager
{
	private static GameManager _instance = null;
	private static GameExtender _currentGame = null;
	
	private GameManager()
	{
		// For the purpose of Singleton.
	}
	
	public static GameManager getInstance()
	{
		if(_instance == null)
		{
			_instance = new GameManager();
			// Add games here
			addGame(new NobleSpleefGame());
			addGame(new NobleRunnerGame());
			addGame(new SillySlapGame());
			addGame(new SillySlapTeamGame());
			addGame(new GemHuntGame());
		}
		return _instance;
	}
	
	private static List<GameExtender> _gameBag = new ArrayList<>();
	
	public static void addGame(GameExtender game)
	{
		if(!_gameBag.contains(game))
		{
			_gameBag.add(game);
			GameEngine.Debug("Adding " + game.getName() + "." );
		}
	}
	
	public static void addCurrentGame(final GameExtender game)
	{
		_currentGame = game;
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
//				Chat.devMessage("Maps to the game " + game.getName() + " are being downloaded. Lag will occure");
//				FileUtils.DownloadMaps(game.getName()); // Depends on a connection. Heavy method.
//				Chat.devMessage("Maps has finished downloading");
			}
		}.runTaskLater(GameEngine.GetPlugin(), 5);
		
	}
	
	public static void removeGame(GameExtender game)
	{
		if(_gameBag.contains(game))
		{
			_gameBag.remove(game);
			GameEngine.Debug("Removing" + game.getName() + "." );
		}
	}
	
	public static GameExtender getGame(String name)
	{
		for(GameExtender games: _gameBag)
		{
			if(games.getName().contains(name))
			{
				return games;
			}
		}
		return null;
	}
	
	public static GameExtender getCurrentGameExtender()
	{
		return _currentGame;
	}
	
	public static List<GameExtender> getGameBag()
	{
		return _gameBag;
	}
}
