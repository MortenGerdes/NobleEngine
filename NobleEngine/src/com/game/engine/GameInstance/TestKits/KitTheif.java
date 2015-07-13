package com.game.engine.GameInstance.TestKits;

import org.bukkit.Material;

import com.game.engine.Game.Kit;

public class KitTheif extends Kit
{
	public KitTheif()
	{
		super("Theif Kit", new String[]
		{
			"Definitely not an theif."
		}, Material.GOLD_HOE);
	}

	@Override
	public void loadAbilities()
	{
		// TODO Auto-generated method stub
		
	}
}
