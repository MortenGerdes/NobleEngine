package com.game.engine.GameTypes.noblerunner;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.game.engine.GameEngine;
import com.game.engine.ScoreBoard.ScoreBoardFactory;

public class NobleRunnerScoreBoard extends ScoreBoardFactory
{
	public NobleRunnerScoreBoard(Player player)
	{
		super("NobleRunner", GameEngine.getCurrentGame(), player);
	}

	@Override
	public void run()
	{
		int amountIngame = GameEngine.getCurrentGame().getPlayers().size();
		
		addNewLine(ChatColor.YELLOW + "Players alive:");
		if((amountIngame) <= 15)
		{
			for(Player players: GameEngine.getCurrentGame().getPlayers().keySet())
			{
				if(GameEngine.getCurrentGame().getSpectators().contains(players))
				{
					addNewLine(ChatColor.GRAY + players.getName());
				}
				else
				{
					addNewLine(ChatColor.GREEN + players.getName());
				}
			}
		}
		else
		{
			addNewLine(ChatColor.GREEN + "" + amountIngame + ChatColor.WHITE + " players are alive!");
		}
	}

}
