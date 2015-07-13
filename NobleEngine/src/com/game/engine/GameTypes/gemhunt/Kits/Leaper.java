package com.game.engine.GameTypes.gemhunt.Kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.game.engine.Game.Kit;
import com.game.engine.Util.ItemStackBuilder;

public class Leaper extends Kit
{

	public Leaper()
    {
	    super("Leaper", new String[] {"Can leap with his axe"}, Material.IRON_AXE);
	    
	    addSpecialItem(new ItemStackBuilder(Material.IRON_AXE, 1, "Leaper axe", ChatColor.GREEN).buildItem(), 1);
	    addSpecialItem(new ItemStackBuilder(Material.STONE_SWORD, 1, "Elf Sword", ChatColor.GREEN).buildItem(), 0);
	    addSpecialItem(new ItemStackBuilder(Material.BOW, 1, "Elf Bow", ChatColor.GREEN).buildItem(), 2);
	    addSpecialItem(new ItemStackBuilder(Material.ARROW, 16, "Elf Arrow", ChatColor.GREEN).buildItem(), 7);
		
	    setArmor(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS), true);
    }

	@Override
	public void loadAbilities()
	{
		// TODO Auto-generated method stub
		
	}

}
