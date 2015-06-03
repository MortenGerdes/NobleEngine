package com.game.engine.ScoreBoard;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.game.engine.GameEngine;
import com.game.engine.Game.Game;
import com.game.engine.Game.GameManager;

public class playerScoreBoard extends ScoreBoardFactory
{
	Player player;
	Game game = GameEngine.getCurrentGame();

	public playerScoreBoard(Player player)
	{
		super("SparkEngine", GameEngine.getCurrentGame(), player);
		this.player = player;
	}

	@Override
	public void run()
	{
		addGameInformation();
		addNeeded();
		addServer();
	}

	public void addGameInformation()
	{
		String name = StringUtils.center(ChatColor.GREEN + "Online Players:", 16);
		addNewLine(name);
		
		String setting = StringUtils.center(ChatColor.WHITE + Integer.toString(game.GetPlayers().size()) + "/" + Integer.toString(game.GetMaxPlayers()), 16);
		addNewLine(setting);
		
		addNewLine("    ");
	}
	
	public void addNeeded()
	{
		String name = StringUtils.center(ChatColor.GREEN + "Needed", 16);
		addNewLine(name);
		
		String setting = StringUtils.center(ChatColor.WHITE + "" + GameManager.getCurrentGameExtender().getMinplayer(), 16);
		addNewLine(setting);
		
		addNewLine("     ");
	}
	
	public void addServer()
	{
		String name = StringUtils.center(ChatColor.GREEN + "Server", 16);
		addNewLine(name);
		
		String setting = StringUtils.center(ChatColor.WHITE + Bukkit.getServerName(), 16);
		addNewLine(setting);
		
		addNewLine("    ");
	}
}