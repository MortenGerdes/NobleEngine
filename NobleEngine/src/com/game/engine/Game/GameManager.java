package com.game.engine.Game;

import java.util.ArrayList;
import java.util.List;

import com.game.engine.GameEngine;
import com.game.engine.GameTypes.gemhunt.GemHuntGame;

public class GameManager
{
	private static GameManager instance = null;
	private static GameExtender currentGame = null;
	
	private GameManager()
	{
		
	}
	
	public static GameManager getInstance()
	{
		if(instance == null)
		{
			instance = new GameManager();
			// Add games here
			//addGame(new KingOfTheLadder());
			addGame(new GemHuntGame());
		}
		return instance;
	}
	
	private static List<GameExtender> gameBag = new ArrayList<>();
	
	public static void addGame(GameExtender game)
	{
		if(!gameBag.contains(game))
		{
			gameBag.add(game);
			GameEngine.Debug("Adding " + game.getName() + "." );
		}
	}
	
	public static void addCurrentGame(GameExtender game)
	{
		currentGame = game;
	}
	
	public static void removeGame(GameExtender game)
	{
		if(gameBag.contains(game))
		{
			gameBag.remove(game);
			GameEngine.Debug("Removing" + game.getName() + "." );
		}
	}
	
	public static GameExtender getGame(String name)
	{
		for(GameExtender games: gameBag)
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
		return currentGame;
	}
	
	public static List<GameExtender> getGameBag()
	{
		return gameBag;
	}
}
