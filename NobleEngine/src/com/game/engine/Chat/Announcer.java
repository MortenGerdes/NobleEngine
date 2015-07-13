package com.game.engine.Chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.game.engine.GameEngine;
import com.game.engine.Game.Kit;
import com.game.engine.Game.GameManagement.GameManager;

public class Announcer
{
	public static void announceGame()
	{
		Bukkit.broadcastMessage(Chat.seperator());
		Bukkit.broadcastMessage(Chat.colored("&eGame - &a" + GameEngine.getCurrentGame().getName()));
		Bukkit.broadcastMessage("");
		for(String message: GameManager.getCurrentGameExtender().getDesc())
		{
			Bukkit.broadcastMessage(Chat.colored(" &f" + message));
		}
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(Chat.colored("&eMap - &a" + GameEngine.getCurrentGame().getHost().getName() + " &7created by &a" + GameEngine.getCurrentGame().getHost().getMapCreator()));
		Bukkit.broadcastMessage(Chat.seperator());
	}
	
	public static void describeKit(Player player, Kit kit)
	{
		player.sendMessage(Chat.seperator());
		player.sendMessage(Chat.colored("&eKit - &a" + kit.getName()));
		player.sendMessage("");
		for(String message: kit.getDescription())
		{
			player.sendMessage(Chat.colored("&a  -&f " + message));
		}
		player.sendMessage("");
		player.sendMessage(Chat.seperator());
	}

}
