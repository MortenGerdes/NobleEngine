package com.game.engine.Util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.BlockIterator;

public class MiscUtil
{
	public static Block getHitBlock(Projectile p)
	{
		Block hit = null;
		BlockIterator iterator = new BlockIterator(p.getLocation().getWorld(), p.getLocation().toVector(), p.getVelocity().normalize(), 0.0D, 4);
		while (iterator.hasNext())
		{
			hit = iterator.next();
			if (hit.getType() != Material.AIR) break;
		}
		return hit;
	}

	public static void changePlayerHunger(Player player, int amount)
	{
		for (int x = 0; x < Math.abs(amount); x++)
		{
			if (amount > 0 && (player.getFoodLevel() < 20))
			{
				player.setFoodLevel(player.getFoodLevel() + 1);
				
			}
			else if (amount < 0 && (player.getFoodLevel() > 0))
			{
				player.setFoodLevel(player.getFoodLevel() - 1);
			}
		}
	}
}
