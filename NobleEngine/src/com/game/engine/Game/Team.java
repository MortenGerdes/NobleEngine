package com.game.engine.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Team
{
	private Game host;
	private String name;
	private ChatColor chatColor;
	private Color color;
	private byte displayColor;
	private List<Player> players = new ArrayList<>();

	public Team(Game game, String teamName, ChatColor chatColor, Color color, byte teamDisplayColor)
	{
		host = game;
		name = teamName;
		this.chatColor = chatColor;
		this.color = color;
		displayColor = teamDisplayColor;
	}

	public void Empty()
	{
		players.clear();
	}

	public boolean hasPlayer(Player player)
	{
		if (players.contains(player))
		{
			return true;
		}
		else
			return false;
	}

	public ItemStack TeamColorWool()
	{
		ItemStack item = new ItemStack(Material.WOOL, 1, GetDisplayColor());
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(GetName());
		im.setLore(Arrays.asList("Set prefered team to: ", GetName()));
		item.setItemMeta(im);
		return item;
	}
	
	public ItemStack numberedTeamColorWool()
	{
		ItemStack item = new ItemStack(Material.WOOL, GetThePlayers().size(), GetDisplayColor());
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(GetName());
		im.setLore(Arrays.asList("Set prefered team to: ", GetName()));
		item.setItemMeta(im);
		return item;

	}

	public void Join(Player player)
	{
		if (!players.contains(player))
		{
			players.add(player);
			player.sendMessage(chatColor + "" + ChatColor.BOLD + "You have joined the " + name + ".");
		}
	}

	public void Leave(Player player)
	{
		if (players.contains(player))
		{
			players.remove(player);
		}
	}

	public Game GetHost()
	{
		return host;
	}

	public String GetName()
	{
		return name;
	}

	public ChatColor GetColor()
	{
		return chatColor;
	}
	
	public Color GetLeatherColor()
	{
		return color;
	}

	public byte GetDisplayColor()
	{
		return displayColor;
	}

	public List<Player> GetThePlayers()
	{
		return players;
	}
}
