package com.game.engine.GameInstance;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import com.game.engine.Game.Team;
import com.game.engine.Game.GameManagement.Game;
import com.game.engine.GameInstance.TestKits.KitElf;
import com.game.engine.GameInstance.TestKits.KitTheif;
import com.game.engine.GameTypes.gemhunt.world.gemhuntWorld;
import com.game.engine.World.HubWorld;

public class GameIns extends Game
{
	private Team _blueTeam = new Team(this, "Blue Team", ChatColor.AQUA, Color.AQUA, (byte) 11);
	private Team _redTeam = new Team(this, "Red Team", ChatColor.RED, Color.RED, (byte) 14);

	/*
	 * The entire purpose of this class is to create the instance of the actual game class
	 * When that's done, I actually replace the information in the game class instance since
	 * that's way more efficient then creating an entire new object.
	 * 
	 * I replace the information with the "GameExtender" class so go look there :P
	 */
	
	public GameIns()
	{
		super("GemHunt", new String[]
		{
			"Just a simple game."
		}, new KitElf(), new HubWorld(), new gemhuntWorld("Gemhunt", "gemhunt"), 20, 16);

		addKit(new KitTheif());

		addTeam(_blueTeam);
		addTeam(_redTeam);

	}
}
