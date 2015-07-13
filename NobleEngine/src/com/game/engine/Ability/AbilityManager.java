package com.game.engine.Ability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.org.ibex.nestedvm.UnixRuntime.DevFS;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.CustomEvents.gameStateChange;
import com.game.engine.Game.GameManagement.GameManager;
import com.game.engine.Game.GameManagement.GameState;
import com.game.engine.Util.MiscUtil;

public class AbilityManager implements Listener
{
	private static AbilityManager _instance = null;
	private ArrayList<Ability> _availableAbilities = new ArrayList<Ability>();
	private HashMap<String, ArrayList<Ability>> _activeAbilities = new HashMap<String, ArrayList<Ability>>();
	private HashMap<String, Ability> _preparedAbilities = new HashMap<String, Ability>();

	private AbilityManager()
	{
		//SingleTon
	}

	public static AbilityManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new AbilityManager();
		}
		return _instance;
	}

	public void addAbility(String playerName, Ability ability)
	{
		if (_activeAbilities.containsKey(playerName))
		{
			_activeAbilities.get(playerName).add(ability);
		}
		else
		{
			_activeAbilities.put(playerName, new ArrayList<Ability>());
			_activeAbilities.get(playerName).add(ability);
		}
	}

	/*
	 * In the method I need to avoid ConcurrentModificationException
	 */
	public void removeAbility(String playerName, Ability ability)
	{
		if (_activeAbilities.containsKey(playerName))
		{
			ArrayList<Ability> testing = new ArrayList<Ability>(_activeAbilities.get(playerName));
			for (Iterator<Ability> abilityIter = testing.iterator(); abilityIter.hasNext();)
			{
				Ability theAbility = abilityIter.next();
				if (theAbility.getName().equals(ability.getName()))
				{
					_activeAbilities.get(playerName).remove(theAbility);
					break;
				}
				abilityIter.remove();
			}
		}
	}

	public void makeAbilityAvailable(Ability ability)
	{
		for(Ability theAbility: getAvailableAbilities())
		{
			if(!theAbility.getName().equals(ability.getName()))
			{
				continue;
			}
			else
			{
			return;
			}
		}
		getAvailableAbilities().add(ability);
	}

	@EventHandler
	public void onGameStart(final gameStateChange event)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if (event.getToState() == GameState.STARTED)
				{
					
					for (Player players : GameEngine.getCurrentGame().getPlayers().keySet())
					{
						for (Ability ability : _availableAbilities)
						{
							addAbility(players.getName(), ability);
						}
					}
				}

			}
		}.runTaskLater(GameEngine.GetPlugin(), 1);
	}

	@EventHandler
	public void onGameEnd(gameStateChange event)
	{
		if (event.getToState() == GameState.ENDED)
		{
			_activeAbilities.clear();
		}
	}

	/*
	 * In the method I need to avoid ConcurrentModificationException aswell
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (GameEngine.getCurrentGame().getState() != GameState.STARTED) return;
		if(!GameManager.getCurrentGameExtender().validateSpectator(player)) return;
		if (item == null) return;
		if (player.getItemInHand() == null) return;
		if (!item.hasItemMeta()) return;
		ArrayList<Ability> testing = new ArrayList<Ability>(getAvailableAbilities());
		for (Iterator<Ability> abilityIter = testing.iterator(); abilityIter.hasNext();)
		{
			Ability ability = abilityIter.next();
			if (GameEngine.getCurrentGame().getPlayerKit(player).getName().equals(ability.getKit().getName()))
			{
				if (item.getItemMeta().getDisplayName().equals(ability.getItem().getItemMeta().getDisplayName()))
				{
					if (getActiveAbilities().containsKey(player.getName()))
					{
						for (Ability theAbility : getActiveAbilities().get(player.getName()))
						{
							if (theAbility.getName().equals(ability.getName()))
							{
								if (ability.getAbilityType() == AbilityType.RIGHT_CLICK
										&& (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
								{
									if (getActiveAbility(player, ability).getCurrentCooldown() != 0)
									{
										player.sendMessage(Chat.format("Ability", "You can use the ability &a" + ability.getName() + "&e in &a"
												+ getActiveAbility(player, ability).getCurrentCooldown() + " seconds!"));
										abilityIter.remove();
										return;
									}
									removeAbility(player.getName(), ability);
									break;
								}
								else if (ability.getAbilityType() == AbilityType.LEFT_CLICK_BOW
										&& (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK))
								{
									if (getActiveAbility(player, ability).getCurrentCooldown() != 0)
									{
										player.sendMessage(Chat.format("Ability", "You can use the ability &a" + ability.getName() + "&e in &a"
												+ getActiveAbility(player, ability).getCurrentCooldown() + " seconds!"));
										abilityIter.remove();
										return;
									}
									removeAbility(player.getName(), ability);
									if (_preparedAbilities.containsKey(player.getName()))
									{
										if (_preparedAbilities.get(player.getName()).getName().equals(ability.getName()))
										{
											player.sendMessage(Chat.format("Ability", "You have already prepared the ability &a" + ability.getName()));
											abilityIter.remove();
											return;
										}
									}
									else
									{
										_preparedAbilities.put(player.getName(), ability);
										player.sendMessage(Chat.format("Ability", "You have prepared the ability &a" + ability.getName()));
										abilityIter.remove();
										return;
									}
								}
								else
								{
									abilityIter.remove();
									return;
								}
							}
						}
					}
					if (ability.getAbilityType() != AbilityType.LEFT_CLICK_BOW)
					{
						Ability newInstance = getNewInstance(ability);
						addAbility(player.getName(), newInstance);
						newInstance.doAbility(player, item);
						newInstance.startCooldown();
						abilityIter.remove();
						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void onArrowHitBlock(ProjectileHitEvent event)
	{
		if (event.getEntity() instanceof Arrow)
		{
			Projectile proj = event.getEntity();
			Arrow arrow = (Arrow) proj;
			Player shooter = (Player) arrow.getShooter();
			if (shooter instanceof Player)
			{
				if (_preparedAbilities.containsKey(shooter.getName()))
				{
					Ability ability = _preparedAbilities.get(shooter.getName());
					ability.setArrow(arrow);
					Ability newInstance = getNewInstance(ability);
					newInstance.setArrow(arrow);
					addAbility(shooter.getName(), newInstance);
					newInstance.doAbility(shooter, shooter.getItemInHand());
					newInstance.startCooldown();
					arrow.remove();
					_preparedAbilities.remove(shooter.getName());
					return;
				}
			}
		}
	}

	public Ability getNewInstance(final Ability ability)
	{
		return new Ability(ability.getName(), ability.getMaxCooldown(), ability.getKit(), ability.getItem())
		{
			@Override
			public void doAbility(Player player, ItemStack item)
			{
				ability.doAbility(player, item);
			}
		};
	}

	public Ability getActiveAbility(Player player, Ability ability)
	{
		if (!_activeAbilities.get(player.getName()).isEmpty())
		{
			for (Ability theAbility : _activeAbilities.get(player.getName()))
			{
				if (theAbility.getName().equalsIgnoreCase(ability.getName()))
				{
					return theAbility;
				}
			}
		}
		Chat.devMessage("ABILITY NULL!");
		return null;
	}

	public void registerAvailableAbilities()
	{
		GameEngine.Register(this);
	}

	public void unRegisterAvailableAbilities()
	{
		HandlerList.unregisterAll(this);
	}

	public HashMap<String, ArrayList<Ability>> getActiveAbilities()
	{
		return _activeAbilities;
	}

	public ArrayList<Ability> getAvailableAbilities()
	{
		return _availableAbilities;
	}
}
