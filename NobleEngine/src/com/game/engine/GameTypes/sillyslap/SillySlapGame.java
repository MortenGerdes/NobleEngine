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

public class SillySlapGame extends GameExtender
{
	private static List<GameWorld> _worlds = Arrays.asList((GameWorld)new sillySlapWorld2("SillySlap", "JungleVibes"), (GameWorld)new sillySlapWorld1("SillySlap", "Hell"));
	
	public SillySlapGame()
	{
		super("SillySlap", "sillyslap", new String[]
		{ "Hit players off the map", "Avoid the crazzy game end!" }, new SillySlapDefaultKit(), new sillySlapWorld1("SillySlap", "Hell"), 12, 5, _worlds, new SillySlapEvents(), SillySlapScoreManager.getInstance());
	}

	@Override
	public void loadGame()
	{
		addTeam(new Team(getGame(), "Player team", ChatColor.YELLOW, Color.YELLOW, (byte) 4));
		addKit(new SillySlapDefaultKit());
		
		setFreezingOnStart(false);
	}
}