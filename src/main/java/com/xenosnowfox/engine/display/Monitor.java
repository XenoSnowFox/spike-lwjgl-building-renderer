package com.xenosnowfox.engine.display;

import com.xenosnowfox.engine.math.vectors.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import java.nio.FloatBuffer;
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

	/**
	 * Retrieves the content scale for the specified monitor.
	 *
	 * <p>This function retrieves the content scale for the specified monitor. The content scale is the ratio between
	 * the current DPI and the platform's default DPI. This is especially important for text and any UI elements. If the
	 * pixel dimensions of your UI scaled by this look appropriate on your machine then it should appear at a reasonable
	 * size on other machines regardless of their DPI and scaling settings. This relies on the system DPI and scaling
	 * settings being somewhat correct.</p>
	 *
	 * <p>The content scale may depend on both the monitor resolution and pixel density and on user settings. It may be
	 * very different from the raw DPI calculated from the physical size and current resolution.</p>
	 *
	 * <p>This function must only be called from the main thread.</p>
	 *
	 * @return Vector containing the content scale.
	 */
	public Vector2f getContentScale() {
		if (Platform.get() != Platform.MACOSX) {
			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				FloatBuffer px = memoryStack.mallocFloat(1);
				FloatBuffer py = memoryStack.mallocFloat(1);

				GLFW.glfwGetMonitorContentScale(this.getMonitorHandle(), px, py);

				return new Vector2f(px.get(0), py.get(0));
			}
		}
		return new Vector2f();
	}
}
