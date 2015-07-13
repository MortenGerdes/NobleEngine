package com.game.engine.Game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;

public class SpectatorManager
{
	// Util class
	
	public static void addSpectator(Player player)
	{
		player.setGameMode(GameMode.CREATIVE);
		makeInvisible(player);
		player.sendMessage(Chat.format("Spectate", "You are now hidden from other players."));
		GameEngine.getCurrentGame().getSpectators().add(player);
	}

	public static void removeSpectator(Player player)
	{
		player.setGameMode(GameMode.SURVIVAL);
		makeVisible(player);
		player.sendMessage(Chat.format("spectate", "You are now visible to other players."));
		GameEngine.getCurrentGame().getSpectators().remove(player);
	}
	
	public static void timedSpectator(final Player player, int seconds)
	{
		addSpectator(player);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(GameEngine.GetPlugin(), new Runnable()
		{

			@Override
			public void run()
			{
				removeSpectator(player);
			}
			
		}, 20 * seconds);
	}

	private static void makeVisible(Player player)
	{
		for (Player players : Bukkit.getOnlinePlayers())
		{
			players.showPlayer(player);
		}
		removeActiveEffects(player);
	}

	private static void makeInvisible(Player player)
	{
		for (Player players : Bukkit.getOnlinePlayers())
		{
			players.hidePlayer(player);
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
	}
	
	public static void makeVisibleAndCreative(Player player)
	{
		for (Player players : Bukkit.getOnlinePlayers())
		{
			players.showPlayer(player);
		}
		removeActiveEffects(player);
		player.setGameMode(GameMode.SURVIVAL);
	}

	public static void makeInvisibleAndCreative(Player player)
	{
		for (Player players : Bukkit.getOnlinePlayers())
		{
			players.hidePlayer(player);
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
		player.setGameMode(GameMode.CREATIVE);
	}


	private static void removeActiveEffects(Player player)
	{
		for (PotionEffect effect : player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}
	}

}
