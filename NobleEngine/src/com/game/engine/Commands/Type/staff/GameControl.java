package com.game.engine.Commands.Type.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.Commands.GameCommands;
import com.game.engine.CustomEvents.gameStateChange;
import com.game.engine.Game.GameManagement.GameExtender;
import com.game.engine.Game.GameManagement.GameManager;
import com.game.engine.Game.GameManagement.GameState;
import com.game.engine.Util.FileUtils;

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
				GameEngine.getCurrentGame().prepare();
				for (Player players : Bukkit.getOnlinePlayers())
				{
					players.sendMessage(Chat.format("GameEngine", sender.getName() + " has started the game!"));
					players.playSound(players.getLocation(), Sound.ENDERDRAGON_HIT, 1, 1);
				}
			}
			else
			{
				sender.sendMessage(Chat.format("GameEngine", "The game has not ended yet!"));
			}
			return;
		}
		if (getArgs()[0].equalsIgnoreCase("stop"))
		{
			if (GameEngine.getCurrentGame().getState() == GameState.STARTED)
			{
				GameEngine.getCurrentGame().stop();
				Bukkit.broadcastMessage(Chat.format("GameEngine", sender.getName() + " has stopped the game!"));
			}
			else if(GameEngine.getCurrentGame().getState() == GameState.PREPARING && GameEngine.getCurrentGame().getIsGameJoinable() == true)
			{
				GameEngine.getCurrentGame().stopPreparingCountDown();
				Bukkit.broadcastMessage(Chat.format("GameEngine", sender.getName() + " has stopped the game!"));
				for (Player onlineplayers : Bukkit.getOnlinePlayers())
				{
					GameEngine.getCurrentGame().getLobby().teleport(onlineplayers);
					onlineplayers.playSound(onlineplayers.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
				}

			}
			else
			{
				sender.sendMessage(Chat.format("GameEngine", "The game has not fully started yet!"));
			}
			return;
		}
		if(getArgs()[0].equalsIgnoreCase("update"))
		{
			if(GameEngine.getCurrentGame().getState() == GameState.WAITING)
			{
				Chat.devMessage("Maps to the game " + GameManager.getCurrentGameExtender().getName() + " are being downloaded. Lag will occure");
				FileUtils.DownloadMaps(GameManager.getCurrentGameExtender().getName()); // Depends on a connection. Heavy method.
				Chat.devMessage("Maps has finished downloading");
				return;
			}
			else
			{
				sender.sendMessage(Chat.format("Command", "You cannot update a game while it's in progress!"));
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
				sender.sendMessage("The game is currently " + GameEngine.getCurrentGame().getName());
				sender.sendMessage("The gameworld is currently " + GameEngine.getCurrentGame().getHost().getName());
				sender.sendMessage("The lobbyworld is currently " + GameEngine.getCurrentGame().getLobby().getName());
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
						GameEngine.getCurrentGame().changeGame(ge);
						Bukkit.broadcastMessage(Chat.format("GameEngine", "Player " + ChatColor.GREEN + sender.getName() + ChatColor.YELLOW + " changed the game to "
								+ ChatColor.GOLD + GameEngine.getCurrentGame().getName()));
						return;
					}
				}
				sender.sendMessage(Chat.format("GameEngine", "Game &c" + getArgs()[1] + "&7 is not a valid game!"));
			}
			else
			{
				sender.sendMessage(Chat.format("GameEngine", "You cannot change to another game in an already started game!"));
				return;
			}
		}
	}
}
