package com.game.engine.Commands.Type.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.Commands.GameCommands;
import com.game.engine.Game.GameExtender;
import com.game.engine.Game.GameManager;
import com.game.engine.Game.GameState;

public class GameControl extends GameCommands
{
	public GameControl()
	{
		super("game", "game.admin", 1, 2, "/game <start | stop>", true);
	}

	@Override
	public void doCommand(Player sender)
	{
		if (getArgs()[0].equalsIgnoreCase("start"))
		{
			if (GameEngine.getCurrentGame().getState() == GameState.WAITING)
			{
				GameEngine.getCurrentGame().Prepare();
				for (Player players : Bukkit.getOnlinePlayers())
				{
					players.sendMessage(Chat.format("GameEngine", sender.getName() + " has started the game!"));
					players.playSound(players.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
				}
			}
			else
			{
				sender.sendMessage(Chat.format("GameEngine", "The game has not ended yet!"));
			}
		}
		if (getArgs()[0].equalsIgnoreCase("stop"))
		{
			if (GameEngine.getCurrentGame().getState() == GameState.STARTED)
			{
				GameEngine.getCurrentGame().Stop();
				Bukkit.broadcastMessage(Chat.format("GameEngine", sender.getName() + " has stopped the game!"));
			}
			else
			{
				sender.sendMessage(Chat.format("GameEngine", "The game has not started yet!"));
			}
		}
		if (getArgs()[0].equalsIgnoreCase("check"))
		{
			if (GameEngine.getCurrentGame() == null)
			{
				sender.sendMessage("The Game is null!");
			}
			else
			{
				long start = System.currentTimeMillis();
				// sender.sendMessage("You have " + PlayerDataManager.getPlayerGold(sender) + " amount of gold");
				// sender.sendMessage("You have " + PlayerDataManager.getPlayerAmbrosia(sender) + " amount of Ambrosia");
				// sender.sendMessage("Your rank is " + PlayerDataManager.getPlayerRank(sender));
				sender.sendMessage("something happened!");
				sender.sendMessage("The game is currently " + GameEngine.getCurrentGame().GetName());
				sender.sendMessage("The gameworld is currently " + GameEngine.getCurrentGame().GetHost().getName());
				sender.sendMessage("The lobbyworld is currently " + GameEngine.getCurrentGame().GetLobby().getName());
				long end = System.currentTimeMillis() - start;
				sender.sendMessage("that took " + end + " milliseconds");
			}
		}
		if (getArgs()[0].equalsIgnoreCase("set"))
		{
			if (GameEngine.getCurrentGame().getState() == GameState.WAITING)
			{
				if (getArgs().length < 2)
				{
					sender.sendMessage(Chat.format("GameEngine", "Please provide enough Arguments!"));
					return;
				}
				for (GameExtender ge : GameManager.getGameBag())
				{
					if (getArgs()[1].equalsIgnoreCase(ge.getInitials()))
					{
						GameEngine.getCurrentGame().ChangeGame(ge);
						Bukkit.broadcastMessage(Chat.format("GameEngine", "Player " + ChatColor.GREEN + sender.getName() + ChatColor.GRAY + " changed the game to "
								+ ChatColor.GOLD + GameEngine.getCurrentGame().GetName()));
						return;
					}
				}
				sender.sendMessage(Chat.format("GameEngine", "Game &c" + getArgs()[1] + "&7 is not a valid game!"));
			}
			else
			{
				sender.sendMessage(Chat.format("GameEngine", "You cannot change to another game in an already started game!"));
			}
		}
	}
}
