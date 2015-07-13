package com.game.engine.Ability;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.Game.Kit;
import com.game.engine.Game.GameManagement.GameState;

public abstract class Ability
{
	private String _name;
	private int _cooldown;
	private int _varCooldown = 0;
	private Kit _kit;
	private ItemStack _item;
	private AbilityType _abilityType;
	private Arrow arrow = null;
	
	
	public Ability(String abilityName, int cooldown, Kit kit, ItemStack itemstack, AbilityType type)
	{
		this._name = abilityName;
		this._cooldown = cooldown;
		this._kit = kit;
		this._item = itemstack;
		this._abilityType = type;
		AbilityManager.getInstance().makeAbilityAvailable(this);
	}

	public Ability(String abilityName, int cooldown, Kit kit, ItemStack itemstack)
	{
		this._name = abilityName;
		this._cooldown = cooldown;
		this._kit = kit;
		this._item = itemstack;
		this._abilityType = AbilityType.RIGHT_CLICK;
		AbilityManager.getInstance().makeAbilityAvailable(this);
	}

	public abstract void doAbility(Player player, ItemStack item);
	
	public void startCooldown()
	{
		_varCooldown = _cooldown;
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if(_varCooldown != 0)
				{
					_varCooldown--;
				}
				else
				{
					this.cancel();
				}
			}
		}.runTaskTimer(GameEngine.GetPlugin(), 0, 20);
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getMaxCooldown()
	{
		return _cooldown;
	}

	public int getCurrentCooldown()
	{
		return _varCooldown;
	}

	public Kit getKit()
	{
		return _kit;
	}

	public ItemStack getItem()
	{
		return _item;
	}
	
	public AbilityType getAbilityType()
	{
		return _abilityType;
	}

	public Arrow getArrow()
	{
		return arrow;
	}

	public void setArrow(Arrow arrow)
	{
		this.arrow = arrow;
	}
	
	
}
