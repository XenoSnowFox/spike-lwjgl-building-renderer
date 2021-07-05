package spike.lwjglbuildingrenderer.lwjgl;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Represents a screen/monitor.
 *
 * See: https://www.glfw.org/docs/latest/monitor_guide.html
 */
public class Monitor {

	public static Monitor primary() {
		return new Monitor(GLFW.glfwGetPrimaryMonitor());
	}

	/**
	 * The registered handle for this monitor.
	 */
	private final long handle;

	/**
	 * Instantiates a new instance representation of a monitor with the given handle.
	 *
	 * @param withHandle monitor Handle.
	 */
	private Monitor(long withHandle) {
		this.handle = withHandle;
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
	 * @param withCallback
	 * 		Callback to return the scale values.
	 */
	public void getContentScale(BiConsumer<Float, Float> withCallback) {
		float scaleX = 1.0f;
		float scaleY = 1.0f;

		if (Platform.get() != Platform.MACOSX) {
			try (MemoryStack s = MemoryStack.stackPush()) {
				FloatBuffer px = s.mallocFloat(1);
				FloatBuffer py = s.mallocFloat(1);

				GLFW.glfwGetMonitorContentScale(this.handle, px, py);

				scaleX = px.get(0);
				scaleY = py.get(0);
			}
		}

		Objects.requireNonNull(withCallback)
				.accept(scaleX, scaleY);
	}

	/**
	 * Returns a human-readable name, encoded as UTF-8, of the specified monitor. The name typically reflects the make
	 * and model of the monitor and is not guaranteed to be unique among the connected monitors.
	 *
	 * <p>The returned string is allocated and freed by GLFW. You should not free it yourself. It is valid until the
	 * specified monitor is disconnected or the library is terminated.</p>
	 *
	 * <p>This function must only be called from the main thread.</p>
	 *
	 * @return the UTF-8 encoded name of the monitor, or {@code NULL} if an error occurred
	 */
	public String getName() {
		return GLFW.glfwGetMonitorName(this.handle);
	}

	public VideoMode getVideoMode() {
		return new VideoMode(Objects.requireNonNull(GLFW.glfwGetVideoMode(this.handle)));
	}

	/**
	 * Returns a list of video modes, sorted initially by BitDepth and secondly by resolution area.
	 *
	 * @return Video Modes.
	 */
	public List<VideoMode> getVideoModes() {
		GLFWVidMode.Buffer buff = GLFW.glfwGetVideoModes(this.handle);
		if (buff == null) {
			return Collections.emptyList();
		}

		return buff.stream()
				.map(VideoMode::new)
				.collect(Collectors.toList());
	}

	public long getHandle() {
		return this.handle;
	}
}
