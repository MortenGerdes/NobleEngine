package com.game.engine.CustomEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.game.engine.GameEngine;
import com.game.engine.Game.GameState;

public final class gameStateChange extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private GameState state;
	private boolean cancelled;

	public gameStateChange(GameState state)
	{
		this.state = state;
	}
	
	public GameState getFromState()
	{
		return GameEngine.getCurrentGame().getState();
	}
	
	public GameState getToState()
	{
		return state;
	}
	
	public boolean isCancelled()
	{
		return cancelled;
	}
	
	public void setCancelled(boolean cancel)
	{
		cancelled = cancel;
	}
	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList()
	{
		return handlers;
	}
	
}
