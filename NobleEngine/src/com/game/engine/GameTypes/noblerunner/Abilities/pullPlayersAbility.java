package com.game.engine.GameTypes.noblerunner.Abilities;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.game.engine.GameEngine;
import com.game.engine.Ability.Ability;
import com.game.engine.Ability.AbilityType;
import com.game.engine.Chat.Chat;
import com.game.engine.Game.Kit;
import com.game.engine.Util.ItemStackBuilder;
import com.game.engine.Util.LocationsUtil;

public class pullPlayersAbility extends Ability
{

	public pullPlayersAbility(String abilityName, int cooldown, Kit kit, ItemStack itemstack, AbilityType type)
	{
		super(abilityName, cooldown, kit, itemstack);
	}

	@Override
	public void doAbility(Player player, ItemStack item)
	{
		Vector dropsite = player.getLocation().getDirection().normalize().multiply(1.2);
		Item diamondBlock = player.getWorld().dropItem(player.getLocation().add(0, 1.5, 0), new ItemStackBuilder(Material.DIAMOND_BLOCK, "Diamond vortex", ChatColor.GREEN).buildItem());
		diamondBlock.setVelocity(dropsite);
		
		pullPlayersTowardsItem(diamondBlock);
		
		
	}
	
	private void pullPlayersTowardsItem(final Item item)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if(item.isOnGround() && !item.isDead())
				{
					List<Entity> nearbyEntities = item.getNearbyEntities(6, 10, 6);
					
					for(Entity entity: nearbyEntities)
					{
						if(entity instanceof Player)
						{
							Player player = (Player)entity;
							
							LocationsUtil.pullTo(player, item.getLocation().add(0,1.5,0), true);
							player.sendMessage(Chat.format("Ability Effect", "You have been pulled by a &cVortex!"));
						}
					}
					item.getWorld().strikeLightningEffect(item.getLocation());
					item.remove();
					this.cancel();
				}
			}
		}.runTaskTimer(GameEngine.GetPlugin(), 20*2, 20);
	}
	
	
	
}
