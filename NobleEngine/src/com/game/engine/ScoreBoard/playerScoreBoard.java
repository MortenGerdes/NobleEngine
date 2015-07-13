package com.game.engine.ScoreBoard;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.game.engine.GameEngine;
import com.game.engine.Game.GameManagement.Game;
import com.game.engine.Game.GameManagement.GameManager;

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
		addNewLine(" ");
		String name = ChatColor.GREEN + "Online Players";
		addNewLine(name);
		
		String setting = StringUtils.center(ChatColor.WHITE + Integer.toString(game.getPlayers().size()) + "/" + Integer.toString(game.getMaxPlayers()), 16);
		addNewLine(setting);
		
		addNewLine("       ");
	}
	
	public void addNeeded()
	{
		String name = ChatColor.GREEN + "Needed";
		addNewLine(name);
		
		String setting = ChatColor.WHITE + "" + GameManager.getCurrentGameExtender().getMinplayer();
		addNewLine(setting);
		
		addNewLine("     ");
	}
	
	public void addServer()
	{
		String name = ChatColor.GREEN + "Server";
		addNewLine(name);
		
		String setting = ChatColor.WHITE + Bukkit.getServerName();
		addNewLine(setting);
		
		addNewLine("   ");
	}
}