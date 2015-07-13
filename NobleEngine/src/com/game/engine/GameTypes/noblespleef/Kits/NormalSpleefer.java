package com.game.engine.GameTypes.noblespleef.Kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.game.engine.Game.Kit;
import com.game.engine.GameTypes.noblespleef.Abilities.LightningCookie;
import com.game.engine.Util.EnchantedItem;
import com.game.engine.Util.ItemStackBuilder;

public class NormalSpleefer extends Kit
{
	private ItemStack _ironShovel;
	
	public NormalSpleefer()
	{
		super("NormalSpleefer", new String[]{"Can send out a deadly cookie", "to trick his enemies into death!"}, Material.IRON_SPADE);
		
		_ironShovel = new ItemStackBuilder(Material.IRON_SPADE, 1, "Basic Shovel", new String[] {"Removes blocks faster than", "Notch removing Herobrine"}, ChatColor.GREEN, new EnchantedItem(Enchantment.DURABILITY, 3)).buildItem();
		addItem(_ironShovel);
		
	}

	@Override
	public void loadAbilities()
	{
		new LightningCookie("Lightning Cookie", 10, this, _ironShovel);
	}
}
