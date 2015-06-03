package com.game.engine.GameTypes.gemhunt;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.game.engine.GameEngine;
import com.game.engine.Game.Game;
import com.game.engine.Game.Team;
import com.game.engine.ScoreBoard.ScoreBoardFactory;

public class gemhuntScoreBoard extends ScoreBoardFactory
{
	Player player;
	Game game = GameEngine.getCurrentGame();
	
	public gemhuntScoreBoard(Player player)
	{
		super("GemHunt", GameEngine.getCurrentGame(), player);
		this.player = player;
	}
	
	@Override
	public void run()
	{
		addBothTeamsGems();
	}
	
	public void addBothTeamsGems()
	{
		addNewLine(ChatColor.RED + "RedTeam");
		addNewLine(ChatColor.WHITE + "RedTeam gems: " + ChatColor.RED + getTeamsGems(Game.toTeam("Red Team")));
		addNewLine(" ");
		addNewLine(ChatColor.BLUE + "BlueTeam");
		addNewLine(ChatColor.WHITE + "BlueTeam gems: "+ ChatColor.BLUE + getTeamsGems(Game.toTeam("Blue Team")));
		addNewLine("   ");
		addNewLine(ChatColor.WHITE + "Time left: " + ChatColor.AQUA + GemHuntEvents.getGameTime());
	}
	
	public int getTeamsGems(Team team)
	{
		int amountOfGems = 0;
		
		for(Player player: game.GetPlayers().keySet())
		{
			if(game.GetTeam(player).equals(team))
			{
				if(!GameEngine.getCurrentGame().GetSpectators().contains(player))
				{
				amountOfGems = amountOfGems + player.getInventory().getItem(4).getAmount();
				}
			}
		}
		return amountOfGems;
	}
}
