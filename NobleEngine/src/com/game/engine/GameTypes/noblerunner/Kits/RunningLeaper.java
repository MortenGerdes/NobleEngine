package com.game.engine.GameTypes.noblerunner.Kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.game.engine.Game.Kit;
import com.game.engine.GameTypes.sillyslap.Abilities.LeapAbility;
import com.game.engine.Util.ItemStackBuilder;

public class RunningLeaper extends Kit
{
	private ItemStack _ironAxe;
	
	public RunningLeaper()
	{
		super("RunningLeaper", new String[] {"Can leap by right-clicking", "with his axe"}, Material.IRON_AXE);
	
		_ironAxe = new ItemStackBuilder(Material.IRON_AXE, 1, "Leaper axe", new String[] {"A noble companion","in a world which falls apart"}, ChatColor.GREEN).buildItem();
		addItem(_ironAxe);
	}

	@Override
	public void loadAbilities()
	{
		new LeapAbility("Leap of Faith", 10, this, _ironAxe);
	}
}
