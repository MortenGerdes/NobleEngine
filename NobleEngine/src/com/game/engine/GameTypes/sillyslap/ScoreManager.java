package com.game.engine.GameTypes.sillyslap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import noble.craft.core.NobleCore;
import noble.craft.core.scheduler.DelayedTask;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.game.engine.GameEngine;

public class ScoreManager implements Listener{

	
	public Map<String,String> LAST_HITS;
	public Map<String, DelayedTask> REMOVE_TASK;
	
	public Map<String, Integer> SCORES;
	
	public ScoreManager()
	{
		LAST_HITS = new HashMap<>();
		REMOVE_TASK = new HashMap<>();
		SCORES = new HashMap<>();
	}
	
	public void init()
	{
		for(Player p : GameEngine.getCurrentGame().GetPlayers().keySet())
		{
			SCORES.put(p.getName(), 0);
		}
	}
	
	@EventHandler
	public void onPlayerHitPlayer(EntityDamageByEntityEvent e)
	{
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player)
		{
			final Player hit = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			
			e.setDamage(0);
			LAST_HITS.put(hit.getName(), damager.getName());
			
			if(REMOVE_TASK.containsKey(hit.getName()))
			{
				REMOVE_TASK.get(hit.getName()).cancel();
				REMOVE_TASK.remove(hit.getName());
			}
			
			DelayedTask task = new DelayedTask()
			{
				@Override
				public void run()
				{
					if(LAST_HITS.containsKey(hit.getName()))
						LAST_HITS.remove(hit.getName());
					
					if(REMOVE_TASK.containsKey(hit.getName()))
						REMOVE_TASK.remove(this);
				}
			};
			
			REMOVE_TASK.put(hit.getName(), task);
			NobleCore.getScheduler().scheduleDelayedTask(task, 20*10);
		}
	}
	
	public boolean hasKiller(String player)
	{
		return LAST_HITS.containsKey(player);
	}
	
	public String getKiller(String player)
	{
		return LAST_HITS.get(player);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LinkedHashMap<String, Integer> sortHashMap()
	{
		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		
		Object[] sorted = SCORES.entrySet().toArray();
		Arrays.sort(sorted, new Comparator(){
			public int compare(Object object1, Object object2)
			{
				return((Map.Entry<String, Integer>) object2).getValue().compareTo(((Map.Entry<String, Integer>) object1).getValue());
			}
		});
		
		for(Object entry: sorted)
		{
			String key = ((Map.Entry<String, Integer>) entry).getKey();
			int integer = ((Map.Entry<String, Integer>) entry).getValue();
			
			sortedMap.put(key, integer);
		}
		return sortedMap; // See if you can use this Lew. This is the best I can do out from memory and I really have no idea if it works
	}
}