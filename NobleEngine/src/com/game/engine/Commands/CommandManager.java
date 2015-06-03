package com.game.engine.Commands;

import java.util.ArrayList;
import java.util.List;

import com.game.engine.GameEngine;
import com.game.engine.Commands.Type.player.commandHub;
import com.game.engine.Commands.Type.staff.GameControl;

public class CommandManager
{
	private static CommandManager instance = null;

	public static CommandManager getInstance()
	{
		if (instance == null)
		{
			instance = new CommandManager();
			// Doesn't register commands?? Look console after dinner
			AddCommand(new commandHub());
			AddCommand(new GameControl());

			for (GameCommands command : commandBag)
			{
				command.registerCommand();
			}

		}
		return instance;
	}

	private static List<GameCommands> commandBag = new ArrayList<>();

	public static void AddCommand(GameCommands command)
	{
		if (!commandBag.contains(command))
		{
			commandBag.add(command);
			GameEngine.Debug("Registering command: " + command.getName());
		}
	}

	public static List<GameCommands> getCommandBag()
	{
		return commandBag;
	}
}
