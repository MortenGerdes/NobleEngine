package com.game.engine.GameTypes.sillyslap;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;

import com.game.engine.Game.GameExtender;
import com.game.engine.Game.GameWorld;
import com.game.engine.Game.Kit;
import com.game.engine.Game.Team;
import com.game.engine.GameTypes.sillyslap.world.sillySlapWorld1;

public class SillySlapGame extends GameExtender{
	
	private static String gameName = "SillySlap";
	private Team players = new Team(getGame(), "Player team", ChatColor.YELLOW, Color.YELLOW, (byte) 4);
	private static Kit cookie = new DefaultKit();
	
	private static GameWorld Hell = new sillySlapWorld1(gameName, "Hell");
	
	private static List<GameWorld> worlds = Arrays.asList(Hell);

	public SillySlapGame() {
		super("SillySlap", "sillyslap", new String[]{"Hit players off the map", "Avoid the crazzy game end!"}, cookie, Hell, 12,
				5, worlds, new SillySlapEvents());
	}

	@Override
	public void loadGame() {
		addTeam(players);
		addKit(cookie);
	}
}