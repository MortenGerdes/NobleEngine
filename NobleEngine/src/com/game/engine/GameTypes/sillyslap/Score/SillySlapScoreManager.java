package com.game.engine.GameTypes.sillyslap.Score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.CustomEvents.gameStateChange;
import com.game.engine.Game.GameManagement.IGameEvents;
import com.game.engine.Game.GameManagement.GameManager;
import com.game.engine.Game.GameManagement.GameState;
import com.game.engine.Game.GameManagement.GameWorld;

public class SillySlapScoreManager implements IGameEvents, Listener
{
	private int timeBeforeKill = 15;
	private HashMap<String, ArrayList<String>> _lastHitInformation = new HashMap<String, ArrayList<String>>();
	public Map<String, Integer> score = new HashMap<>();
	
	private static SillySlapScoreManager instance = null;

	private SillySlapScoreManager()
	{
		// For the purpose of singleton
	}

	public static SillySlapScoreManager getInstance()
	{
		if (instance == null)
		{
			instance = new SillySlapScoreManager();
		}
		return instance;
	}

	@EventHandler
	public void resetLists(gameStateChange event)
	{
		if (event.getToState() == GameState.STARTED)
		{
			_lastHitInformation.clear();
			score.clear();
			for (Player player : GameEngine.getCurrentGame().getPlayers().keySet())
			{
				score.put(player.getName(), 0);
			}
		}
	}

	@EventHandler
	public void onPlayerHitPlayer(EntityDamageByEntityEvent event)
	{
		if (GameEngine.getCurrentGame().getState() != GameState.STARTED)
		{
			return;
		}
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player)
		{
			Player hitted = (Player) event.getEntity();
			Player damager = (Player) event.getDamager();
			final String hittedName = hitted.getName();
			final String damagerName = damager.getName();
			
			if(GameManager.getCurrentGameExtender().getName() == "SillySlapTeam")
			{
				if(GameEngine.getCurrentGame().getTeam(hitted).equals(GameEngine.getCurrentGame().getTeam(damager)))
				{
					event.setCancelled(true);
					return;
				}
			}
			
			if (_lastHitInformation.containsKey(hitted.getName()))
			{
				_lastHitInformation.get(hittedName).add(damagerName);
			}
			else
			{
				_lastHitInformation.put(hittedName, new ArrayList<String>());
				_lastHitInformation.get(hittedName).add(damagerName);
			}
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					_lastHitInformation.get(hittedName).remove(damagerName);
				}
			}.runTaskLater(GameEngine.GetPlugin(), 20 * timeBeforeKill);
		}
	}

	public boolean hasKiller(String player)
	{
		if (_lastHitInformation.containsKey(player))
		{
			if (_lastHitInformation.get(player).isEmpty())
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		return false;
	}

	public String getKiller(String player)
	{
		if (_lastHitInformation.containsKey(player))
		{
			if (!_lastHitInformation.get(player).isEmpty())
			{
				int sizeOfArray = _lastHitInformation.get(player).size();
				return _lastHitInformation.get(player).get(sizeOfArray - 1);
			}
			else
			{
				return null;
			}
		}
		return null;
	}

	public void creditKiller(Player died)
	{
		Player playerToCredit;
		if (_lastHitInformation.containsKey(died.getName()))
		{
			if (!_lastHitInformation.get(died.getName()).isEmpty())
			{
				int sizeOfArray = _lastHitInformation.get(died.getName()).size();
				String nameOfLastKiller = _lastHitInformation.get(died.getName()).get(sizeOfArray - 1);
				score.put(nameOfLastKiller, score.get(nameOfLastKiller) + 1);
				playerToCredit = Bukkit.getServer().getPlayer(nameOfLastKiller);
				playerToCredit.sendMessage(Chat.format("Score", "You have killed player &c" + died.getName() + "&e. Score increased"));
				died.sendMessage(Chat.format("Score", "You have been killed by player &c" + playerToCredit.getName()));
				playerToCredit.playSound(playerToCredit.getLocation(), Sound.LEVEL_UP, 2.0f, 2.0f);
				_lastHitInformation.get(died.getName()).clear();
			}
			else
			{
				died.sendMessage(Chat.format("SillySlap", "You died without having someone to kill you..."));
			}
		}
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private LinkedHashMap<String, Integer> sortHashMap()
	{
		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		Object[] sorted = score.entrySet().toArray();
		Arrays.sort(sorted, new Comparator()
		{
			public int compare(Object object1, Object object2)
			{
				return ((Map.Entry<String, Integer>) object2).getValue().compareTo(((Map.Entry<String, Integer>) object1).getValue());
			}
		});
		for (Object entry : sorted)
		{
			String key = ((Map.Entry<String, Integer>) entry).getKey();
			int integer = ((Map.Entry<String, Integer>) entry).getValue();
			sortedMap.put(key, integer);
		}
		
		return sortedMap;
	}

	public Map<String, Integer> getTopPlayers(int amount)
	{
		LinkedHashMap<String, Integer> values = sortHashMap();
		HashMap<String, Integer> toReturn = new HashMap<>();
		ArrayList<String> keys = new ArrayList<String>(values.keySet());
		
		for(String testing: values.keySet())
		{
			if(!keys.contains(testing))
			{
				keys.add(testing);
			}
		}
		for (int i = 0; i < amount; i++)
		{
			try
			{
				
				String key = keys.get(i);
				int ScoreNumber = values.get(key);
				toReturn.put(key, ScoreNumber);
			}
			catch(IndexOutOfBoundsException e)
			{
				break;
			}
		}
		return toReturn;
	}

	public String getWinner()
	{
		for (String playerNames : getTopPlayers(1).keySet())
		{
			return playerNames;
		}
		return null;
	}
	
	public String getPlayerOnPlace2()
	{
		int place = 1;
		
		for (String playerNames : getTopPlayers(2).keySet())
		{
			if(place == 1)
			{
				place++;
				continue;
			}
			place = 1;
			return playerNames;
		}
		return null;
	}
	
	public String getPlayerOnPlace3()
	{
		int place = 1;
		
		for (String playerNames : getTopPlayers(3).keySet())
		{
			if(place != 3)
			{
				place++;
				continue;
			}
			place = 1;
			return playerNames;
		}
		return null;
	}


	public int getScore(String player)
	{
		if (score.get(player) != null)
		{
			return score.get(player);
		}
		else
		{
			return 0;
		}
	}

	@Override
	public void register()
	{
		Bukkit.getPluginManager().registerEvents(this, GameEngine.GetPlugin());
	}

	@Override
	public void unregister()
	{
		HandlerList.unregisterAll(this);
	}

	@Override
	public void setHostWorld(GameWorld w)
	{
	}
}