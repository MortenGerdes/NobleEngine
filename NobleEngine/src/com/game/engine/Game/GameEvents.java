package com.game.engine.Game;

public interface GameEvents {

	public void register();
	
	public void unregister();
	
	public void setHostWorld(GameWorld w);
}
