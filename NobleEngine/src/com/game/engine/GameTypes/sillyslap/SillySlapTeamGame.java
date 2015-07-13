package com.game.engine.GameTypes.sillyslap;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;

import com.game.engine.Game.Kit;
import com.game.engine.Game.Team;
import com.game.engine.Game.GameManagement.GameExtender;
import com.game.engine.Game.GameManagement.GameWorld;
import com.game.engine.GameTypes.sillyslap.Kits.SillySlapDefaultKit;
import com.game.engine.GameTypes.sillyslap.Score.SillySlapScoreManager;
import com.game.engine.GameTypes.sillyslap.Worlds.sillySlapWorld1;
import com.game.engine.GameTypes.sillyslap.Worlds.sillySlapWorld2;

public class SillySlapTeamGame extends GameExtender
{
	private static List<GameWorld> worlds = Arrays.asList((GameWorld)new sillySlapWorld2("SillySlapTeam", "JungleVibes"), (GameWorld)new sillySlapWorld1("SillySlapTeam", "Hell"));
	
	public SillySlapTeamGame()
	{
		super("SillySlapTeam", "sillyslapteam", new String[]
		{ "Hit players off the map", "Avoid the crazzy game end!" }, new SillySlapDefaultKit(), new sillySlapWorld1("SillySlapTeam", "Hell"), 30, 6, worlds, new SillySlapEvents(), SillySlapScoreManager.getInstance());
	}

	@Override
	public void loadGame()
	{
		addTeam(new Team(getGame(), "Team Chicken Nugget", ChatColor.LIGHT_PURPLE, Color.FUCHSIA, (byte) 6));
		addTeam(new Team(getGame(), "Team Fractis", ChatColor.RED, Color.RED, (byte) 14));
		addTeam(new Team(getGame(), "Team Memory Leak", ChatColor.GREEN, Color.GREEN, (byte) 5));
		addKit(new SillySlapDefaultKit());
		
		setForcableTeams(false);
		setFreezingOnStart(false);
	}
}
