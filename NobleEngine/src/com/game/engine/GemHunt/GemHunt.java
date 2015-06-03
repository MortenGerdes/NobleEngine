package com.game.engine.GemHunt;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import com.game.engine.Game.Game;
import com.game.engine.Game.Team;
import com.game.engine.GameTypes.gemhunt.world.gemhuntWorld;
import com.game.engine.World.HubWorld;

public class GemHunt extends Game
{
	private Team blueTeam = new Team(this, "Blue Team", ChatColor.AQUA, Color.AQUA, (byte) 11);
	private Team redTeam = new Team(this, "Red Team", ChatColor.RED, Color.RED, (byte) 14);

	public GemHunt()
	{
		super("GemHunt", new String[]
		{
			"Just a simple game."
		}, new KitElf(), new HubWorld(), new gemhuntWorld("Gemhunt", "gemhunt"), 20, 16);

		AddKit(new KitTheif());

		AddTeam(blueTeam);
		AddTeam(redTeam);

	}

	@Override
	public void onStopGame()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartGame()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onJoinGame(Player player)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onLeaveGame(Player player)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onWinGame(Player[] players)
	{
		// TODO Auto-generated method stub

	}

}
