package com.game.engine.Commands;

import java.util.ArrayList;
import java.util.List;

import com.game.engine.GameEngine;
import com.game.engine.Commands.Type.player.commandHub;
import com.game.engine.Commands.Type.staff.GameControl;

public class CommandManager
{
	private static CommandManager instance = null;
	private static List<GameCommands> _commandBag = new ArrayList<>();

	public static CommandManager getInstance()
	{
		if (instance == null)
		{
			instance = new CommandManager();
			addCommand(new commandHub());
			addCommand(new GameControl());

			for (GameCommands command : _commandBag)
			{
				command.registerCommand();
			}

		}
		return instance;
	}

	public static void addCommand(GameCommands command)
	{
		if (!_commandBag.contains(command))
		{
			_commandBag.add(command);
			GameEngine.Debug("Registering command: " + command.getName());
		}
	}

	public static List<GameCommands> getCommandBag()
	{
		return _commandBag;
	}
}
