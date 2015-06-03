package com.game.engine.GameTypes.sillyslap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import com.game.engine.Game.Kit;
import com.game.engine.Util.EnchantedItem;
import com.game.engine.Util.ItemStackBuilder;

public class DefaultKit extends Kit
{
	
	public DefaultKit()
	{
		super("Default", new String[]{"Basic cookie skined item!"}, Material.COOKIE);
		
		for(int i = 0; i <= 7; i++)
			 AddItem(new ItemStackBuilder(Material.COOKIE, 1, "NOM NOM NOM", new String[]{"A strong cookie"}, ChatColor.GREEN, new EnchantedItem(Enchantment.KNOCKBACK, 4)).buildItem());
	}

}