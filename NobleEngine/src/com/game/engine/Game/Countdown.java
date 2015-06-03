package com.game.engine.Game;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.game.engine.GameEngine;
import com.game.engine.Util.WorldUtil;

public class Countdown extends BukkitRunnable
{
	private Game host;
	private int duration;

	public Countdown(Game host, int duration)
	{
		this.host = host;
		this.duration = duration;
	}
	
	public void start(){
		this.run();
	}

	@Override
	public void run()
	{
		duration--;

		for (Player player : host.GetPlayers().keySet())
		{
			player.setLevel(duration);
		}

		if (duration <= 10)
		{
			for (Player player : host.GetPlayers().keySet())
			{
				player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5F, 1.5F);
			}
		}
		
		if(duration == 9){
			if(GameManager.getCurrentGameExtender() == null)
				Bukkit.broadcastMessage("The extender is null!");
			if(GameManager.getCurrentGameExtender().getHost() == null)
				Bukkit.broadcastMessage("The host is null!");
			
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					GameManager.getCurrentGameExtender().getHost().load();
				}
			}.runTaskLater(GameEngine.GetPlugin(), 0); // For the purpose of multithreading
			
		}
		
		if(duration == 5)
		{
			GameEngine.getCurrentGame().GetHost().setSpawnPointsFromHostWorld();
			
			Game.replaceSpawn(GameManager.getCurrentGameExtender().getSpawns());
		}

		if (duration == 0)
		{
			host.Start();
			this.cancel();
		}
		
		if(duration < 0)
		{
			GameEngine.Debug("Cancelled start countdown");
			this.cancel();
			Thread.interrupted();
		}
	}
	
	public void countDownCancel()
	{
		duration = -1;
	}
}