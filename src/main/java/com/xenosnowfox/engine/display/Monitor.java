package com.xenosnowfox.engine.display;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Representation of a monitor or display device.
 */
public class Monitor {

	/**
	 * Monitor Handle.
	 */
	private final long monitorHandle;

	/**
	 * Instantiates a new instance representation of a monitor with the given handle.
	 *
	 * @param withHandle
	 * 		monitor Handle.
	 */
	public Monitor(long withHandle) {
		this.monitorHandle = withHandle;
	}

	/**
	 * Returns the registered handle for the monitor.
	 *
	 * @return monitor handle
	 */
	public long getMonitorHandle() {
		return this.monitorHandle;
	}

	/**
	 * Returns a human-readable name, encoded as UTF-8, of the specified monitor. The name typically reflects the make
	 * and model of the monitor and is not guaranteed to be unique among the connected monitors.
	 *
	 * <p>This function must only be called from the main thread.</p>
	 *
	 * @return the UTF-8 encoded name of the monitor, or {@code NULL} if an error occurred
	 */
	public String getName() {
		return GLFW.glfwGetMonitorName(this.getMonitorHandle());
	}

	/**
	 * Returns the monitor's current video mode settings.
	 *
	 * @return video mode.
	 */
	public VideoMode getVideoMode() {
		return new VideoMode(Objects.requireNonNull(GLFW.glfwGetVideoMode(this.getMonitorHandle())));
	}

	/**
	 * Returns a collection of video modes this monitor can support.
	 *
	 * @return Collection of video modes.
	 */
	public Collection<VideoMode> getSupportedVideoModes() {
		GLFWVidMode.Buffer buffer = GLFW.glfwGetVideoModes(this.getMonitorHandle());
		if (buffer == null) {
			return null;
		}

		return buffer.stream()
				.map(VideoMode::new)
				.collect(Collectors.toList());
	}
}
