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
	private Material material;
	private String displayName;
	private String[] lore;
	private ChatColor color;
	private int amount;
	private List<EnchantedItem> enchantments = new ArrayList<>();

	public ItemStackBuilder(Material material, String name, ChatColor displayColor)
	{
		this.material = material;
		this.displayName = name;
		this.color = displayColor;
	}

	public ItemStackBuilder(Material material, int amount, String name, ChatColor displayColor)
	{
		this.material = material;
		this.displayName = name;
		this.color = displayColor;
		this.amount = amount;
	}

	public ItemStackBuilder(Material material, int amount, String name, String[] lore, ChatColor displayColor)
	{
		this.material = material;
		this.amount = amount;
		this.color = displayColor;
		this.displayName = name;
		this.lore = lore;
	}
	
	public ItemStackBuilder(Material material, int amount, String name, String[] lore, ChatColor displayColor, EnchantedItem... items)
	{
		this.material = material;
		this.amount = amount;
		this.color = displayColor;
		this.displayName = name;
		this.lore = lore;
		
		for(EnchantedItem e : items)
			this.enchantments.add(e);
	}

	public ItemStack buildItem()
	{
		ItemStack item = new ItemStack(material, amount);
		ItemMeta im = item.getItemMeta();
		if (displayName != null)
		{
			im.setDisplayName(color + displayName);
		}
		if (lore != null)
		{
			im.setLore(Arrays.asList(lore));
		}

		item.setItemMeta(im);
		
		if(!this.enchantments.isEmpty())
		{
			for(EnchantedItem e : this.enchantments)
			{
				item.addUnsafeEnchantment(e.getEnchantment(), e.getLevel());
			}
		}

		return item;
	}
}
