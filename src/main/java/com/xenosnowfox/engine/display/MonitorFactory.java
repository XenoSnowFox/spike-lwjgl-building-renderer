package com.xenosnowfox.engine.display;

import org.lwjgl.glfw.GLFW;

/**
 * Represents a factory capable of producing instances of supported monitors.
 */
public class MonitorFactory {

	/**
	 * Returns an instance on the user's primary display monitor.
	 *
	 * @return Primary display monitor.
	 */
	public static Monitor getPrimaryMonitor() {
		return new Monitor(GLFW.glfwGetPrimaryMonitor());
	}

	/**
	 * Hidden constructor.
	 */
	private MonitorFactory() {
	}
}
