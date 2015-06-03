package com.game.engine.Chat;

import org.bukkit.ChatColor;

public abstract class Chat
{
	private static ChatColor C;
	private static ChatColor cReset = C.RESET;
	private static ChatColor cBold = C.BOLD;
	private static ChatColor cHead = C.BLUE;
	private static ChatColor cDivider = C.YELLOW;
	private static ChatColor cBody = C.WHITE;
	private static ChatColor cPlayer = C.YELLOW;
	private static ChatColor cTool = C.AQUA;
	private static ChatColor cLink = C.GOLD;
	private static ChatColor cRank = C.RED;
	private static ChatColor cKit = C.GREEN;
	private static ChatColor cTime = C.YELLOW;
	private static ChatColor cAbility = C.AQUA;
	private static ChatColor cGadget = C.GREEN;
	private static ChatColor cCommand = C.RED;
	private static ChatColor cUpgrade = C.RED;
	private static ChatColor cKillstreak = C.RED;

	public static String removeColor(String text)
	{
		return ChatColor.stripColor(text);
	}

	public static String ability(String ability)
	{
		return cAbility + ability + cBody;
	}

	public static String command(String command)
	{
		return cCommand + command + cBody;
	}

	public static String format(String head, String body)
	{
		return ChatColor.GRAY + "[" + cHead + head + ChatColor.GRAY + "]: " + ChatColor.translateAlternateColorCodes('&', body);
	}

	public static String gadget(String gadget)
	{
		return cGadget + gadget + cBody;
	}

	public static String killstreak(String killstreak)
	{
		return cKillstreak + "" + ChatColor.BOLD + killstreak + cBody;
	}

	public static String kit(String kit)
	{
		return cKit + kit + cBody;
	}

	public static String link(String link)
	{
		return cLink + link + cBody;
	}

	public static String player(String name)
	{
		return cPlayer + name + cBody;
	}

	public static String rank(String rank)
	{
		return cRank + rank + cReset + cBody;
	}

	public static String seperator()
	{
		return cDivider + "" + ChatColor.STRIKETHROUGH + "========================================";
	}

	public static String time(String time)
	{
		return cTime + time + cBody;
	}

	public static String tool(String tool)
	{
		return cTool + tool + cBody;
	}

	public static String upgrade(String upgrade)
	{
		return cUpgrade + upgrade + cBody;
	}
}
