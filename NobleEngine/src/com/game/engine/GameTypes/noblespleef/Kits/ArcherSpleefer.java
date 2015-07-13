package com.game.engine.GameTypes.noblespleef.Kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.game.engine.Ability.AbilityType;
import com.game.engine.Game.Kit;
import com.game.engine.GameTypes.noblespleef.Abilities.TestAbility2;
import com.game.engine.Util.EnchantedItem;
import com.game.engine.Util.ItemStackBuilder;

public class ArcherSpleefer extends Kit
{
	private ItemStack _bow;
	private ItemStack _arrow;
	
	public ArcherSpleefer()
	{
		super("ArcherSpleefer", new String[] {"Can prepare a deadly knockback shot","by &bleft-clicking&f with his bow", "Watch out though...", "The chance of hitting is low!"}, Material.BOW);
		
		_bow = new ItemStackBuilder(Material.BOW, 1, "Spleef Bow", new String[] {"A strong bow"}, ChatColor.GREEN,new EnchantedItem(Enchantment.ARROW_INFINITE, 1)).buildItem();
		_arrow = new ItemStackBuilder(Material.ARROW, "Le-Arrow", ChatColor.LIGHT_PURPLE).buildItem();
		
		addItem(_bow);
		addSpecialItem(_arrow, 7);
	}

	@Override
	public void loadAbilities()
	{
		new TestAbility2("Testing Strike", 15, this, _bow, AbilityType.LEFT_CLICK_BOW);
	}
	
}
