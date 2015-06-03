package com.game.engine.Timer;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.Game.Game;

public class StartedGameFreeze
{
	public int FreezeTime;
	int Task1;

	public StartedGameFreeze()
	{
		this.FreezeTime = 10;
	}

	public StartedGameFreeze(int FreezeTime)
	{
		this.FreezeTime = FreezeTime;
	}

	public Runnable GameFreeze()
	{
		Task1 = GameEngine.GetPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(GameEngine.GetPlugin(), new Runnable()
		{
			public void run()
			{
				if (FreezeTime != 0)
				{
					for (Player player : Bukkit.getOnlinePlayers())
					{
						player.sendMessage(Chat.format("SparkEngine", "The game will start in " + FreezeTime));
						if (FreezeTime > 3)
							player.playSound(player.getLocation(), Sound.CAT_MEOW, 1.0f, 1.0f);
						else
							player.playSound(player.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
					}
				}

				if (FreezeTime <= 0)
				{
					Game.UnFreezeAllPlayersAtStart();
					for (Player player : Bukkit.getOnlinePlayers())
					{
						player.sendMessage(Chat.format("SparkEngine", "The Game has started!"));
					}
					GameEngine.GetPlugin().getServer().getScheduler().cancelTask(Task1);
				}
				FreezeTime--;
			}
		}, 20, 20);
		return null;
	}
}
