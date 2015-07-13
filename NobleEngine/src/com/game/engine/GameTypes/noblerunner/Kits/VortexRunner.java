package com.game.engine.GameTypes.noblerunner.Kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.game.engine.Ability.AbilityType;
import com.game.engine.Game.Kit;
import com.game.engine.GameTypes.noblerunner.Abilities.pullPlayersAbility;
import com.game.engine.Util.ItemStackBuilder;

public class VortexRunner extends Kit
{
	private ItemStack _stick;
	public VortexRunner()
	{
		super("Vortex Runner", new String[] {"Can launch a deadly orb","that pull all runners towards it", "but watch out because", "it pulls you too!"}, Material.ENDER_PEARL);
		
		_stick = new ItemStackBuilder(Material.STICK, 1, "Pull a Stick", new String[] {"It's not just a Stick","It has the POWER!"}, ChatColor.DARK_PURPLE).buildItem();
		addItem(_stick);
	}

	@Override
	public void loadAbilities()
	{
		new pullPlayersAbility("Vortex", 15, this, _stick, AbilityType.RIGHT_CLICK);
	}
}
