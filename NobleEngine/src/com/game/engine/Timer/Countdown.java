package com.game.engine.Timer;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.Game.GameManagement.Game;
import com.game.engine.Game.GameManagement.GameManager;
import com.game.engine.Util.WorldUtil;

public class Countdown extends BukkitRunnable
{
	private Game _host;
	private int _duration;

	public Countdown(Game host, int duration)
	{
		this._host = host;
		this._duration = duration;
	}
	
	public void start(){
		this.run();
	}

	@Override
	public void run()
	{
		_duration--;

		for (Player player : _host.getPlayers().keySet())
		{
			player.setLevel(_duration);
		}

		if (_duration <= 10)
		{
			for (Player player : _host.getPlayers().keySet())
			{
				player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5F, 1.5F);
			}
		}
		
		if(_duration == 9){
			if(GameManager.getCurrentGameExtender().getHost() == null)
				Chat.devMessage("The host is null!");
			
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					Bukkit.broadcastMessage(Chat.format(GameEngine.getCurrentGame().getName(), "The game &a" + GameEngine.getCurrentGame().getName() + "&e will start shortly on the map &a" + GameEngine.getCurrentGame().getHost().getName()));
					GameManager.getCurrentGameExtender().getHost().load();
				}
			}.runTaskLater(GameEngine.GetPlugin(), 0); // For the purpose of multithreading
			
		}
		
		if(_duration == 5)
		{
			GameEngine.getCurrentGame().getHost().setSpawnPointsFromHostWorld();
			
			Game.replaceSpawn(GameManager.getCurrentGameExtender().getSpawns());
		}

		if (_duration == 0)
		{
			_host.start();
			this.cancel();
		}
		
		if(_duration < 0)
		{
			GameEngine.Debug("Cancelled start countdown");
			this.cancel();
			Thread.interrupted();
		}
	}
	
	public void countDownCancel()
	{
		_duration = -1;
	}
}