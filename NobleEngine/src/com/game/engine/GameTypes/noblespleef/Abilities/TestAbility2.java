package com.game.engine.GameTypes.noblespleef.Abilities;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.game.engine.GameEngine;
import com.game.engine.Ability.Ability;
import com.game.engine.Ability.AbilityType;
import com.game.engine.Game.Kit;
import com.game.engine.Util.FireworkExplosion;
import com.game.engine.Util.ItemStackBuilder;

public class TestAbility2 extends Ability
{
	private Player[] _players = Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
	private Player _user;

	public TestAbility2(String abilityName, int cooldown, Kit kit, ItemStack itemstack, AbilityType type)
	{
		super(abilityName, cooldown, kit, itemstack, type);
	}

	@Override
	public void doAbility(final Player player, ItemStack item)
	{
		Arrow arrow = this.getArrow();
		Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
		this._user = player;
		
		FireworkEffect.Builder build = FireworkEffect.builder();
		FireworkEffect effects = build.trail(false).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.GREEN).flicker(false).build();
		FireworkExplosion.spawn(arrow.getLocation().add(0,1,0), effects, players);

		final Item _goldBlock = arrow.getWorld().dropItem(arrow.getLocation().add(0, 1, 0), new ItemStackBuilder(Material.GOLD_BLOCK, player.getName() + "'S Gold", ChatColor.GREEN).buildItem());
		_goldBlock.setVelocity(new Vector(0,5,0).normalize());
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				fireArrowsFromItem(_goldBlock, 10);
			}
		}.runTaskLater(GameEngine.GetPlugin(), 20);
	}
	
	private void fireArrowsFromItem(Item item, int radius)
	{
		FireworkEffect.Builder build = FireworkEffect.builder();
		FireworkEffect effects = build.trail(false).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).flicker(false).build();
		FireworkExplosion.spawn(item.getLocation().add(0,1,0), effects, _players);

		List<Entity> nearEntities = item.getNearbyEntities(radius, 15, radius);
		
		for(Entity entity: nearEntities)
		{
			if(entity instanceof Player)
			{
				Player nearPlayer = (Player) entity;
				Arrow arrow;
				
				if(GameEngine.getCurrentGame().getSpectators().contains(nearPlayer) || nearPlayer.getName() == _user.getName())
				{
					continue;
				}
				
				Vector from = item.getLocation().toVector();
				Vector to = nearPlayer.getLocation().add(0,3,0).toVector();
				
				Vector velocity = to.subtract(from);
				velocity.normalize().multiply(1.5);
				
				arrow = (Arrow) item.getLocation().getWorld().spawnEntity(item.getLocation(), EntityType.ARROW);
				arrow.setVelocity(velocity);
			}
		}
		item.remove();
	}
	
}
