package org.paracite.glframework.core;


public abstract class Screen {
	protected final Game game;
	protected final Graphics graphics;

	public Screen(Game game) {
		this.game = game;
		this.graphics = game.graphics;
	}
	
	public abstract void update(float deltaTime);
	
	public abstract void present(float deltaTime);
	
	public abstract void pause();
	
	public abstract void resume();
	
	public abstract void dispose();
}
