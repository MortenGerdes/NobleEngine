package com.game.engine.Util;

import org.bukkit.enchantments.Enchantment;

public class EnchantedItem {

	private Enchantment e;
	private int lev;
	
	public EnchantedItem(Enchantment enchant, int level)
	{
		this.e = enchant;
		this.lev = level;
	}
	
	public Enchantment getEnchantment()
	{
		return this.e;
	}
	
	public int getLevel()
	{
		return this.lev;
	}
}
