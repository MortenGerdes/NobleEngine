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
import com.game.engine.Game.GameManagement.Game;

public abstract class ScoreBoardFactory
{
	private Scoreboard _board;
	private Objective _object;
	private ScoreboardManager _manager;
	private Game _game;
	private Player _player;
	private ArrayList<Score> _scores = new ArrayList<Score>();
	private int _LineNumber = 16;

	public ScoreBoardFactory(String ObjectiveName, Game game, Player player)
	{
		this._game = game;
		this._manager = Bukkit.getScoreboardManager();
		this._board = _manager.getNewScoreboard();
		this._player = player;
		this._object = _board.registerNewObjective(ObjectiveName, "test");
		_object.setDisplaySlot(DisplaySlot.SIDEBAR);
		createScoreBoard();
	}

	public abstract void run();

	public void createScoreBoard()
	{
		_object.setDisplayName(ChatColor.GOLD + _game.getName());
	}

	public void updateScoreBoard()
	{
		resetScoreBoard();
		createScoreBoard();
		run();
		_player.setScoreboard(_board);
	}

	public void addNewLine(String line)
	{
		Score score = _object.getScore(line);
		score.setScore(_LineNumber);
		_scores.add(score);
		_LineNumber--;
	}

	public void resetScoreBoard()
	{
		this._LineNumber = 16;
		for (Score thescores : getScores())
		{
			_player.getScoreboard().resetScores(thescores.getEntry());
		}
	}

	public ArrayList<Score> getScores()
	{
		return _scores;
	}

	public static void globalScoreBoardUpdate()
	{
		if ((GameEngine.getCurrentGame().getPanels() != null) || (!GameEngine.getCurrentGame().getPanels().isEmpty()))
		{
			for (Player playerPanels : GameEngine.getCurrentGame().getPanels().keySet())
			{
				GameEngine.getCurrentGame().getPanels().get(playerPanels).updateScoreBoard();
			}
		}
	}
}