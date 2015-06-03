package com.game.engine.GameTypes.gemhunt;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;

import com.game.engine.Game.GameExtender;
import com.game.engine.Game.GameWorld;
import com.game.engine.Game.Kit;
import com.game.engine.Game.Team;
import com.game.engine.GameTypes.gemhunt.world.gemhuntWorld;
import com.game.engine.GameTypes.gemhunt.world.gemhuntWorld2;

public class GemHuntGame extends GameExtender
{
	private static String gameName = "GemHunt";
	private Team blueTeam = new Team(getGame(), "Blue Team", ChatColor.AQUA, Color.BLUE, (byte) 11);
	private Team redTeam = new Team(getGame(), "Red Team", ChatColor.RED, Color.RED, (byte) 14);
	private static Kit defaultkit = new Leaper();
	private static Kit Barbarian = new Barbarian();
	
	private static GameWorld world = new gemhuntWorld(gameName, "test");
	private static GameWorld world2 = new gemhuntWorld2(gameName, "test2");
	private static List<GameWorld> worlds = Arrays.asList(world, world2);
	
	public GemHuntGame()
	{
		super(gameName, "gemhunt", new String[]
		{ "Collect the enemies treasure", "Most gems win!" }, defaultkit, world, 16, 6,worlds, new GemHuntEvents());
		
		loadGame();
	}

	@Override
	public void loadGame()
	{
		addTeam(blueTeam);
		addTeam(redTeam);
		addKit(defaultkit);
		addKit(Barbarian);
		
		
//		CustomNPCHandler.getInstance().removeAllEntitiesfromArrayList();
//		SparkEngine.Debug("Adding entites to the world");
//		World world = ((CraftWorld) SparkEngine.getCurrentGame().GetLobby().getWorld()).getHandle();
//		customZombie zombie = CustomNPCHandler.getInstance().returnHandledZombie("&aGuardian", new Location(SparkEngine.getCurrentGame().GetLobby().getWorld(), -22.5, 5, -33.5), (world));
//		world.addEntity(zombie);
	}
}