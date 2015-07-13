package com.game.engine.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.Util.ItemStackBuilder;
import com.game.engine.Util.UtilPlayer;

public abstract class Kit
{
	private boolean _isAllLeather = false;
	private boolean _kitHasArmor = false;
	private String _name;
	private String[] _description;
	private Material _displayItem;
	private ItemStack _helmet;
	private ItemStack _chest;
	private ItemStack _leggins;
	private ItemStack _boots;
	private List<ItemStack> _contents = new ArrayList<>();
	private HashMap<ItemStack, Integer> _specificContent = new HashMap<ItemStack, Integer>();

	public Kit(String kitName, String[] kitDescription, Material displayItem)
	{
		_name = kitName;
		_description = kitDescription;
		this._displayItem = displayItem;
		GameEngine.Debug("Creating " + _name + " kit.");
	}
	
	public abstract void loadAbilities();

	public void setArmor(ItemStack helmet, ItemStack chest, ItemStack leggins, ItemStack boots, boolean isAllLeather)
	{
		this._helmet = helmet;
		this._chest = chest;
		this._leggins = leggins;
		this._boots = boots;
		this._isAllLeather = isAllLeather;
		this._kitHasArmor = true;
	}

	public void addItem(ItemStack itemStack)
	{
		_contents.add(itemStack);
	}

	public void addSpecialItem(ItemStack item, int slot)
	{
		_specificContent.put(item, slot);
	}

	public void removeItem(ItemStack itemStack)
	{
		if (_contents.contains(itemStack)) _contents.remove(itemStack);
	}

	public void equip(Player player)
	{
		UtilPlayer.reset(player);
		player.sendMessage(Chat.format("Kit", "You have equipped the &a" + _name + "&f."));
		for (ItemStack item : _specificContent.keySet())
		{
			player.getInventory().setItem(_specificContent.get(item), item);
		}
		for (ItemStack itemStack : _contents)
		{
			player.getInventory().addItem(itemStack);
		}
		if (_kitHasArmor = true)
		{
			if (_isAllLeather = true)
			{
				try
				{
					LeatherArmorMeta im1 = (LeatherArmorMeta) _helmet.getItemMeta();
					im1.setColor(GameEngine.getCurrentGame().getTeam(player).GetLeatherColor());
					_helmet.setItemMeta(im1);
					LeatherArmorMeta im2 = (LeatherArmorMeta) _chest.getItemMeta();
					im2.setColor(GameEngine.getCurrentGame().getTeam(player).GetLeatherColor());
					_chest.setItemMeta(im2);
					LeatherArmorMeta im3 = (LeatherArmorMeta) _leggins.getItemMeta();
					im3.setColor(GameEngine.getCurrentGame().getTeam(player).GetLeatherColor());
					_leggins.setItemMeta(im3);
					LeatherArmorMeta im4 = (LeatherArmorMeta) _boots.getItemMeta();
					im4.setColor(GameEngine.getCurrentGame().getTeam(player).GetLeatherColor());
					_boots.setItemMeta(im4);
				}
				catch (NullPointerException e)
				{
					GameEngine.Debug("Caught kit item meta exception. Are you sure you added armor for " + getName() + "?");
				}
			}
			try
			{
				player.getInventory().setHelmet(_helmet);
				player.getInventory().setChestplate(_chest);
				player.getInventory().setLeggings(_leggins);
				player.getInventory().setBoots(_boots);
			}
			catch (NullPointerException e)
			{
				GameEngine.Debug("Caught kit set armor exception");
			}
		}
	}

	public String getName()
	{
		return _name;
	}

	public String[] getDescription()
	{
		return _description;
	}

	public List<ItemStack> getContents()
	{
		return _contents;
	}

	public ItemStack getDisplayItem()
	{
		return new ItemStackBuilder(_displayItem, 1, _name, _description, ChatColor.GREEN).buildItem();
	}
}
