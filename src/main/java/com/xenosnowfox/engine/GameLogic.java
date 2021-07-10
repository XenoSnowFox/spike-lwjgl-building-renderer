package com.xenosnowfox.engine;

import com.xenosnowfox.engine.display.Window;

public interface GameLogic {

	void init() throws Exception;

	void input();

	void update(float interval);

	void render();

	void cleanUp();
}
