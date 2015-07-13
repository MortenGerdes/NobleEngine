package com.game.engine.GameTypes.sillyslap.Score;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.game.engine.GameEngine;
import com.game.engine.Game.GameManagement.Game;
import com.game.engine.Game.GameManagement.GameManager;
import com.game.engine.GameTypes.sillyslap.SillySlapEvents;
import com.game.engine.ScoreBoard.ScoreBoardFactory;

public class SillySlapScoreboard extends ScoreBoardFactory
{
	Player player;
	Game game = GameEngine.getCurrentGame();
	SillySlapEvents ev = (SillySlapEvents) GameManager.getCurrentGameExtender().getEvents()[0];

	public SillySlapScoreboard(Player player)
	{
		super("SillySlap", GameEngine.getCurrentGame(), player);
		this.player = player;
	}

	@Override
	public void run()
	{
		addNewLine(ChatColor.RED + "Top Player");
		addNewLine(StringUtils.substring(SillySlapScoreManager.getInstance().getWinner(), 0, 30));
		addNewLine(" ");
		addNewLine(ChatColor.BLUE + "Top 3 Players");
		Map<String, Integer> scores = SillySlapScoreManager.getInstance().getTopPlayers(3);
		for (String names : scores.keySet())
		{
			addNewLine(StringUtils.substring(names + ChatColor.YELLOW + " " + scores.get(names), 0, 30));
		}
		addNewLine("   ");
		addNewLine(ChatColor.GOLD + "Your score");
		addNewLine(SillySlapScoreManager.getInstance().getScore(player.getName()) + "");
		addNewLine("    ");
		addNewLine(ChatColor.WHITE + "Time left: " + ChatColor.AQUA + ev.getGameTime());
	}
}
