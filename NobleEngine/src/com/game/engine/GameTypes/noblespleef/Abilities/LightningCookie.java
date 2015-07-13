package com.game.engine.GameTypes.noblespleef.Abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.game.engine.GameEngine;
import com.game.engine.Ability.Ability;
import com.game.engine.Game.Kit;
import com.game.engine.Util.ItemStackBuilder;

public class LightningCookie extends Ability
{

	public LightningCookie(String abilityName, int cooldown, Kit kit, ItemStack itemstack)
	{
		super(abilityName, cooldown, kit, itemstack);
	}

	@Override
	public void doAbility(Player player, ItemStack item)
	{
		Vector dropsite = player.getLocation().getDirection().normalize().multiply(1.2);
		Item cookie = player.getWorld().dropItem(player.getLocation().add(0, 1.5, 0), new ItemStackBuilder(Material.COOKIE, "Lightning Cookie", ChatColor.GREEN).buildItem());
		cookie.setVelocity(dropsite);
		
		strikeLightning(cookie);

	}
	
	private void strikeLightning(final Item item)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if(item.isOnGround() && !item.isDead())
				{
					item.getWorld().strikeLightningEffect(item.getLocation());
					item.getWorld().createExplosion(item.getLocation(), 5);
					item.remove();
					this.cancel();
				}
			}
		}.runTaskTimer(GameEngine.GetPlugin(), 20*2, 20);
	}
}
