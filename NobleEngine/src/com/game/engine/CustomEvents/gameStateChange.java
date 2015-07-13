package com.game.engine.CustomEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.game.engine.GameEngine;
import com.game.engine.Game.GameManagement.GameState;

public final class gameStateChange extends Event
{
	private boolean _cancelled;
	private static final HandlerList _handlers = new HandlerList();
	private GameState _state;

	public gameStateChange(GameState state)
	{
		this._state = state;
	}
	
	public GameState getFromState()
	{
		return GameEngine.getCurrentGame().getState();
	}
	
	public GameState getToState()
	{
		return _state;
	}
	
	public boolean isCancelled()
	{
		return _cancelled;
	}
	
	public void setCancelled(boolean cancel)
	{
		_cancelled = cancel;
	}
	@Override
	public HandlerList getHandlers()
	{
		return _handlers;
	}
	
	public static HandlerList getHandlerList()
	{
		return _handlers;
	}
	
}
