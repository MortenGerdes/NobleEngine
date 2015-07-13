package com.game.engine.GameTypes.noblerunner;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;

import com.game.engine.Game.Team;
import com.game.engine.Game.GameManagement.GameExtender;
import com.game.engine.Game.GameManagement.GameWorld;
import com.game.engine.GameTypes.noblerunner.Kits.RunningLeaper;
import com.game.engine.GameTypes.noblerunner.Kits.VortexRunner;
import com.game.engine.GameTypes.noblerunner.Worlds.NobleRunnerWorld1;
import com.game.engine.GameTypes.noblespleef.Worlds.NobleSpleefWorld1;

public class NobleRunnerGame extends GameExtender
{
	private static List<GameWorld> _worlds = Arrays.asList((GameWorld)new NobleRunnerWorld1("NobleRunner", "Morten2"));

	
	public NobleRunnerGame()
	{
		super("NobleRunner", "noblerunner", new String[] {"Run with the speed of sound!","Enjoy the trial","Don't fall down"}, new RunningLeaper(), new NobleRunnerWorld1("NobleRunner", "Morten2"), 16, 2, _worlds, new NobleRunnerEvents());
	}

	@Override
	public void loadGame()
	{
		addTeam(new Team(getGame(), "Player", ChatColor.YELLOW, Color.YELLOW, (byte) 4));
		addKit(new RunningLeaper());
		addKit(new VortexRunner());
		
		setFreezingOnStart(false);
	}
}
