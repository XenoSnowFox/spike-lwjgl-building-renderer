package spike.lwjglbuildingrenderer;

import com.xenosnowfox.engine.display.Monitor;
import com.xenosnowfox.engine.display.MonitorFactory;
import com.xenosnowfox.engine.display.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

import java.util.Properties;

/**
 * Main Spike Entrypoint.
 */
public class Spike implements Runnable {

	private final Window window;

	/**
	 * Application entry point.
	 *
	 * @param args Commandline arguments.
	 */
	public static void main(String... args) {
		System.out.println("Starting Spike.");
		Spike spike = new Spike();
		spike.run();
	}

	public Spike() {
		System.out.println("Loading properties file.");
		final Properties spikeProperties = Utils.loadProperties("spike.properties");
		System.out.println(spikeProperties);

		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!GLFW.glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// get a reference to the primary monitor
		final Monitor monitor = MonitorFactory.getPrimaryMonitor();

		// configure GLFW
		GLFW.glfwDefaultWindowHints(); // optional, the current window hints are already the default
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // the window will stay hidden after creation
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // the window will be resizable
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);

		// create a window
		this.window = new Window(
				spikeProperties.getProperty("window.title", "Spike")
				, Integer.parseInt(spikeProperties.getProperty("window.width", "640"))
				, Integer.parseInt(spikeProperties.getProperty("window.height", "480"))
				, true
		);

		// center the window on the monitor
		this.window.centerOnMonitor(monitor);
		this.window.setAsCurrentRenderingContext();
		this.window.show();
		this.window.focus();
		this.window.requestAttention();
	}

	@Override
	public void run() {
		System.out.println("Starting game loop.");

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		GL46.glEnableClientState(GL46.GL_VERTEX_ARRAY);

		// Set the clear color
		GL46.glClearColor(43f / 255f, 43f / 255f, 43f / 255f, 0f); // BG color
		GL46.glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color

		// main game loop
		while (!this.window.shouldClose()) {

			// Poll for window events. The key callback above will only be invoked during this call.
			GLFW.glfwPollEvents();

			// update scene

			// start new render pass
			GL46.glLoadIdentity();
			GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			// render scene

			// swap the buffers
			this.window.swapBuffers();
		}

		GL46.glDisableClientState(GL46.GL_VERTEX_ARRAY);
	}
}
