package com.game.engine.GameTypes.sillyslap.Abilities;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.game.engine.Ability.Ability;
import com.game.engine.Chat.Chat;
import com.game.engine.Game.Kit;

public class LeapAbility extends Ability
{

	public LeapAbility(String abilityName, int cooldown, Kit kit, ItemStack itemstack)
	{
		super(abilityName, cooldown, kit, itemstack);
	}

	@Override
	public void doAbility(Player player, ItemStack item)
	{
		player.setVelocity(player.getLocation().getDirection().multiply(1.3D));
		player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 2.0F, 0.9F);
		player.sendMessage(Chat.format("Ability", "You used leap!"));
	}
}
