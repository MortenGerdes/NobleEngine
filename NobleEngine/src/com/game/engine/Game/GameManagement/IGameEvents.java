package com.game.engine.Game.GameManagement;

public interface IGameEvents {

	public void register();
	
	public void unregister();
	
	public void setHostWorld(GameWorld w);
}
