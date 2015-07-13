package com.game.engine.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackBuilder
{
	private Material _material;
	private String _displayName;
	private String[] _lore;
	private ChatColor _color;
	private int _amount;
	private List<EnchantedItem> _enchantments = new ArrayList<>();

	public ItemStackBuilder(Material material, String name, ChatColor displayColor)
	{
		this._material = material;
		this._displayName = name;
		this._color = displayColor;
		this._amount = 1;
	}

	public ItemStackBuilder(Material material, int amount, String name, ChatColor displayColor)
	{
		this._material = material;
		this._displayName = name;
		this._color = displayColor;
		this._amount = amount;
	}

	public ItemStackBuilder(Material material, int amount, String name, String[] lore, ChatColor displayColor)
	{
		this._material = material;
		this._amount = amount;
		this._color = displayColor;
		this._displayName = name;
		this._lore = lore;
	}
	
	public ItemStackBuilder(Material material, int amount, String name, String[] lore, ChatColor displayColor, EnchantedItem... items)
	{
		this._material = material;
		this._amount = amount;
		this._color = displayColor;
		this._displayName = name;
		this._lore = lore;
		
		for(EnchantedItem e : items)
			this._enchantments.add(e);
	}

	public ItemStack buildItem()
	{
		ItemStack item = new ItemStack(_material, _amount);
		ItemMeta im = item.getItemMeta();
		if (_displayName != null)
		{
			im.setDisplayName(_color + _displayName);
		}
		if (_lore != null)
		{
			im.setLore(Arrays.asList(_lore));
		}

		item.setItemMeta(im);
		
		if(!this._enchantments.isEmpty())
		{
			for(EnchantedItem e : this._enchantments)
			{
				item.addUnsafeEnchantment(e.getEnchantment(), e.getLevel());
			}
		}

		return item;
	}
}
