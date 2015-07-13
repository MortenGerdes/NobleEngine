package com.game.engine.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;

public abstract class GameCommands implements Listener, CommandExecutor
{
	private String commandName;
	private String usage;
	private String[] args;
	private String perm;
	private int minimumLength;
	private int maxiumLength;
	private boolean allowYoutubers;

	public GameCommands(String commandName, String permission, int minimumLength, int maxiumLength, String usage, boolean allowYoutubers)
	{
		this.commandName = commandName;
		this.minimumLength = minimumLength;
		this.maxiumLength = maxiumLength;
		this.usage = usage;
		this.perm = permission;
		this.allowYoutubers = allowYoutubers;
		GameEngine.Register(this);
	}

	public abstract void doCommand(Player sender);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			this.args = args;
			
			if (!cmd.getName().equalsIgnoreCase(commandName))
			{
				GameEngine.Debug("Command caught" + cmd.getName());
				return true;
			}
			if (args.length < minimumLength)
			{
				player.sendMessage(Chat.format("Command", "Please provide enough argument(s)! " + minimumLength + " arguments are required"));
				player.sendMessage(Chat.format("Command Usage", usage));
				return true;
			}
			if (args.length > maxiumLength)
			{
				player.sendMessage(Chat.format("Command", "Please provide fewer arguments! " + maxiumLength + " arguments are max!"));
				player.sendMessage(Chat.format("Command Usage", usage));
				return true;
			}
			if (!(player.hasPermission(perm)) || !(player.isOp()))
			{
				sender.sendMessage(Chat.format("Command", "&cYou don't have permission to the command: &6" + commandName));
				return true;
			}
			try
			{
				doCommand(player);
			}
			catch (Exception e)
			{
				if (player.hasPermission("dev.info"))
				{
					//StackTraceElement st = Thread.currentThread().getStackTrace()[1];
					player.playSound(player.getLocation(), Sound.HORSE_SKELETON_IDLE, 1.0F, 1.0F);
					player.sendMessage(Chat.seperator());
					player.sendMessage(Chat.format("GameEngine", "An &cError &7has accured!"));
					player.sendMessage(Chat.format("GameEngine", "Please forward a &9ScreenShot &7to &aMorten or Lew"));
					player.sendMessage(Chat.format("GameEngine", "and clarify what " + ChatColor.UNDERLINE + "command" + ChatColor.GRAY + " you were using"));
					player.sendMessage(Chat.format("GameEngine", "Used the command: &e" + commandName + "&7 with arguments of &e" + getArgs().length));
					//player.sendMessage(Chat.format("GameEngine","Error; &e" + e.getClass().getSimpleName() + "&7 in file: &e" + st.getFileName() + "&7 in method: &e" + st.getMethodName() + "&7 at line: &e" + st.getLineNumber()));
					player.sendMessage(Chat.seperator());
				}
				else
				{
					player.sendMessage(Chat.format("GameEngine", "Ohh nooo. Seems like an Error occured #SadFace :("));
				}
				e.printStackTrace();
			}
		}
		return false;
	}

	public String[] getArgs()
	{
		return args;
	}

	public String getName()
	{
		return commandName;
	}

	public void registerCommand()
	{
		Bukkit.getPluginCommand(this.commandName).setExecutor(this);
	}
}
