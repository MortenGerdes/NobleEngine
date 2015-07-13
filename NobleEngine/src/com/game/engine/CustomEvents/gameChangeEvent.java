package com.game.engine.CustomEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.game.engine.Game.GameManagement.GameExtender;

public class gameChangeEvent extends Event
{
	private static final HandlerList _handlers = new HandlerList();
	private GameExtender _game;
	
	public gameChangeEvent(GameExtender game)
	{
		this._game = game;
	}
	
	@Override
	public HandlerList getHandlers()
	{
		return _handlers;
	}
	
	public GameExtender getGameExtender()
	{
		return _game;
	}
	
	public static HandlerList getHandlerList()
	{
		return _handlers;
	}
}
