package com.game.engine.Commands.Type.player;

import org.bukkit.entity.Player;

import com.game.engine.Commands.GameCommands;
import com.game.engine.Game.BungeeCordSupport;

public class commandHub extends GameCommands
{
	public commandHub()
	{
		super("hub", null, 0, 1, "Return to hub: /hub", false);
	}

	@Override
	public void doCommand(Player sender)
	{
		BungeeCordSupport.sendPlayer(sender, "lobby-1");
	}
}
