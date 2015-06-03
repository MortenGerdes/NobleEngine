package com.game.engine.ScoreBoard;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.game.engine.GameEngine;
import com.game.engine.Game.Game;

public abstract class ScoreBoardFactory
{
	private Scoreboard board;
	private Objective object;
	private ScoreboardManager manager;
	private Game game;
	private Player player;
	private ArrayList<Score> scores = new ArrayList<Score>();
	private int LineNumber = 16;

	public ScoreBoardFactory(String ObjectiveName, Game game, Player player)
	{
		this.game = game;
		this.manager = Bukkit.getScoreboardManager();
		this.board = manager.getNewScoreboard();
		this.player = player;
		this.object = board.registerNewObjective(ObjectiveName, "test");
		object.setDisplaySlot(DisplaySlot.SIDEBAR);
		createScoreBoard();
	}

	public abstract void run();

	public void createScoreBoard()
	{
		object.setDisplayName(ChatColor.GOLD + game.GetName());
	}

	public void updateScoreBoard()
	{
		resetScoreBoard();
		createScoreBoard();
		run();
		player.setScoreboard(board);
	}

	public void addNewLine(String line)
	{
		Score score = object.getScore(line);
		score.setScore(LineNumber);
		scores.add(score);
		LineNumber--;
	}

	public void resetScoreBoard()
	{
		this.LineNumber = 16;
		for (Score thescores : getScores())
		{
			player.getScoreboard().resetScores(thescores.getEntry());
		}
	}

	public ArrayList<Score> getScores()
	{
		return scores;
	}

	public static void globalScoreBoardUpdate()
	{
		if ((GameEngine.getCurrentGame().GetPanels() != null) || (!GameEngine.getCurrentGame().GetPanels().isEmpty()))
		{
			for (Player playerPanels : GameEngine.getCurrentGame().GetPanels().keySet())
			{
				GameEngine.getCurrentGame().GetPanels().get(playerPanels).updateScoreBoard();
			}
		}
	}
}