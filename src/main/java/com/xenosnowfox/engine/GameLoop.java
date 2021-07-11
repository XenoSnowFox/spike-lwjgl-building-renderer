package com.xenosnowfox.engine;

import com.xenosnowfox.engine.graphics.Timer;
import com.xenosnowfox.lwjglengine.display.Window;

import java.util.Objects;

public class GameLoop implements GameLogic, Runnable {

	public static final int TARGET_FPS = 75;

	public static final int TARGET_UPS = 30;

	private final Window window;

	private final GameLogic gameLogic;

	private final Timer timer;

	public GameLoop(final Window withWindow, final GameLogic withGameLogic) {
		this.window = Objects.requireNonNull(withWindow);
		this.gameLogic = Objects.requireNonNull(withGameLogic);
		this.timer = new Timer();
	}

	@Override
	public void run() {
		try {
			System.out.println("GAME LOOP: init");
			this.init();

			System.out.println("GAME LOOP: run");
			float elapsedTime;
			float accumulator = 0f;
			float interval = 1f / TARGET_UPS;

			boolean running = true;
			while (running && !window.shouldClose()) {
				elapsedTime = timer.getElapsedTime();
				accumulator += elapsedTime;

				this.input();

				while (accumulator >= interval) {
					this.update(interval);
					accumulator -= interval;
				}

				this.render();

				if ( !window.isVSync() ) {
					sync();
				}
			}
		} catch (Exception excp) {
			excp.printStackTrace();
		} finally {
			System.out.println("GAME LOOP: destroy");
			this.cleanUp();
		}
	}

	private void sync() {
		float loopSlot = 1f / TARGET_FPS;
		double endTime = timer.getLastLoopTime() + loopSlot;
		while (timer.getTime() < endTime) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
			}
		}
	}
	@Override
	public void init() throws Exception {
		this.gameLogic.init();
	}

	@Override
	public void input() {
		this.gameLogic.input();
	}

	@Override
	public void update(final float interval) {
		this.gameLogic.update(interval);
	}

	@Override
	public void render() {
		this.gameLogic.render();
	}

	@Override
	public void cleanUp() {
		this.gameLogic.cleanUp();
	}
}
