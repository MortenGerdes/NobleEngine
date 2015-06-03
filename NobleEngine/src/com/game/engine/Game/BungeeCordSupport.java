package com.game.engine.Game;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;

public class BungeeCordSupport implements Listener
{
	public static void sendPlayer(Player player, String server)
	{
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try
		{
			out.writeUTF("Connect");
			out.writeUTF(server);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			player.sendMessage(Chat.format("Connector", "Failed to connect you to " + server));
		}
		player.sendPluginMessage(GameEngine.GetPlugin(), "BungeeCord", b.toByteArray());
	}
}
