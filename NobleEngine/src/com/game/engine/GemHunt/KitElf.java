package com.game.engine.GemHunt;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.game.engine.Game.Kit;

public class KitElf extends Kit
{
	public KitElf()
	{
		super("Elf Kit", new String[]
		{
			"A standard Elf, born to fight."
		}, Material.FEATHER);

		AddItem(new ItemStack(Material.STONE_SWORD));
		AddItem(new ItemStack(Material.BOW));
		AddItem(new ItemStack(Material.ARROW, 30));
		
		setArmor(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS), true);
	}
}
