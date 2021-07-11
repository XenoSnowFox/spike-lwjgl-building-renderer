package com.xenosnowfox.engine;

public interface GameLogic {

	void init() throws Exception;

	void input();

	void update(float interval);

	void render();

	void cleanUp();
}
