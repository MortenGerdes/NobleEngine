package com.game.engine.GameTypes.noblespleef;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.game.engine.GameEngine;
import com.game.engine.ScoreBoard.ScoreBoardFactory;

public class NobleSpleefScoreBoard extends ScoreBoardFactory
{
	public NobleSpleefScoreBoard(Player player)
	{
		super("NobleSpleef", GameEngine.getCurrentGame(), player);
	}

	@Override
	public void run()
	{
		int amountIngame = GameEngine.getCurrentGame().getPlayers().size();
		//int amountAlive = amountIngame - GameEngine.getCurrentGame().getSpectators().size();
		
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
