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

import com.game.engine.Game.GameManagement.Game;

public class Team
{
	private byte _displayColor;
	private String _name;
	private ChatColor _chatColor;
	private Color _color;
	private Game _host;
	
	private List<Player> _players = new ArrayList<>();

	public Team(Game game, String teamName, ChatColor chatColor, Color color, byte teamDisplayColor)
	{
		this._host = game;
		this._name = teamName;
		this._chatColor = chatColor;
		this._color = color;
		this._displayColor = teamDisplayColor;
	}

	public void Empty()
	{
		_players.clear();
	}

	public boolean hasPlayer(Player player)
	{
		if (_players.contains(player))
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
		if (!_players.contains(player))
		{
			_players.add(player);
			player.sendMessage(_chatColor + "" + ChatColor.BOLD + "You have joined the " + _name + ".");
		}
	}

	public void Leave(Player player)
	{
		if (_players.contains(player))
		{
			_players.remove(player);
		}
	}

	public Game GetHost()
	{
		return _host;
	}

	public String GetName()
	{
		return _name;
	}

	public ChatColor GetColor()
	{
		return _chatColor;
	}
	
	public Color GetLeatherColor()
	{
		return _color;
	}

	public byte GetDisplayColor()
	{
		return _displayColor;
	}

	public List<Player> GetThePlayers()
	{
		return _players;
	}
}
