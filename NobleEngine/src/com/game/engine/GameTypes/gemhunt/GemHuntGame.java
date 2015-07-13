package com.game.engine.GameTypes.gemhunt;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;

import com.game.engine.Game.Kit;
import com.game.engine.Game.Team;
import com.game.engine.Game.GameManagement.GameExtender;
import com.game.engine.Game.GameManagement.GameWorld;
import com.game.engine.GameTypes.gemhunt.Kits.Barbarian;
import com.game.engine.GameTypes.gemhunt.Kits.Leaper;
import com.game.engine.GameTypes.gemhunt.world.gemhuntWorld;
import com.game.engine.GameTypes.gemhunt.world.gemhuntWorld2;

public class GemHuntGame extends GameExtender
{
	private static List<GameWorld> worlds = Arrays.asList((GameWorld)new gemhuntWorld("GemHunt", "test"), (GameWorld)new gemhuntWorld2("GemHunt", "test2"));
	
	public GemHuntGame()
	{
		super("GemHunt", "gemhunt", new String[]
		{ "Collect the enemies treasure", "Most gems win!" }, new Leaper(), new gemhuntWorld("GemHunt", "test"), 16, 6,worlds, new GemHuntEvents());
		
	}

	@Override
	public void loadGame()
	{
		addTeam(new Team(getGame(), "Blue Team", ChatColor.AQUA, Color.BLUE, (byte) 11));
		addTeam(new Team(getGame(), "Red Team", ChatColor.RED, Color.RED, (byte) 14));
		addKit(new Leaper());
		addKit(new Barbarian());
		
		
//		CustomNPCHandler.getInstance().removeAllEntitiesfromArrayList();
//		SparkEngine.Debug("Adding entites to the world");
//		World world = ((CraftWorld) SparkEngine.getCurrentGame().GetLobby().getWorld()).getHandle();
//		customZombie zombie = CustomNPCHandler.getInstance().returnHandledZombie("&aGuardian", new Location(SparkEngine.getCurrentGame().GetLobby().getWorld(), -22.5, 5, -33.5), (world));
//		world.addEntity(zombie);
	}
}