package com.game.engine.GameTypes.noblespleef;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;

import com.game.engine.Game.Kit;
import com.game.engine.Game.Team;
import com.game.engine.Game.GameManagement.GameExtender;
import com.game.engine.Game.GameManagement.GameWorld;
import com.game.engine.GameTypes.noblespleef.Kits.ArcherSpleefer;
import com.game.engine.GameTypes.noblespleef.Kits.NormalSpleefer;
import com.game.engine.GameTypes.noblespleef.Worlds.NobleSpleefWorld1;

public class NobleSpleefGame extends GameExtender
{
	private static List<GameWorld> _worlds = Arrays.asList((GameWorld)new NobleSpleefWorld1("NobleSpleef", "Morten1"));
	
	public NobleSpleefGame()
	{
		super("NobleSpleef", "noblespleef", new String[]
		{"Destroy blocks under players","Be the last man standing!","Avoid the meteorshower!!!"}, new NormalSpleefer(), new NobleSpleefWorld1("NobleSpleef", "Morten1"), 16, 2, _worlds, new NobleSpleefEvents());
	}

	@Override
	public void loadGame()
	{
		addTeam(new Team(getGame(), "Player", ChatColor.YELLOW, Color.YELLOW, (byte) 4));
		addKit(new NormalSpleefer());
		addKit(new ArcherSpleefer());
		
		setFreezingOnStart(false);
	}
}
