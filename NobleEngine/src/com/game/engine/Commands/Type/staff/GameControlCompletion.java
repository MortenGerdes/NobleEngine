package com.game.engine.Commands.Type.staff;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.game.engine.Game.GameExtender;
import com.game.engine.Game.GameManager;

public class GameControlCompletion implements TabCompleter
{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("game") && args.length >= 2 && args[0].equalsIgnoreCase("set"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
			}
			
			List<String> suggestions = new ArrayList<String>();
			
			for(GameExtender initials: GameManager.getGameBag())
			{
				suggestions.add(initials.getInitials());
			}
			return suggestions;
		}
		return null;
	}
	
}
