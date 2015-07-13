package com.game.engine.GameTypes.sillyslap.Kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.game.engine.Game.Kit;
import com.game.engine.GameTypes.sillyslap.Abilities.LeapAbility;
import com.game.engine.Util.EnchantedItem;
import com.game.engine.Util.ItemStackBuilder;

public class SillySlapDefaultKit extends Kit
{
	private ItemStack _cookie;
	
	public SillySlapDefaultKit()
	{
		super("Default", new String[]{"Basic cookie skined item!", "Knows how to leap with a cookie"}, Material.COOKIE);

		_cookie = new ItemStackBuilder(Material.COOKIE, 1, "NOM NOM NOM", new String[]{"A strong cookie"}, ChatColor.GREEN, new EnchantedItem(Enchantment.KNOCKBACK, 6)).buildItem();
		addItem(_cookie);
		setArmor(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS), true);
		
	}

	@Override
	public void loadAbilities()
	{
		new LeapAbility("Slappy slap", 10, this, _cookie);
	}

}