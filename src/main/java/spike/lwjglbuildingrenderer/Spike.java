package spike.lwjglbuildingrenderer;

import com.xenosnowfox.engine.GameLogic;
import com.xenosnowfox.engine.GameLoop;
import com.xenosnowfox.engine.display.Monitor;
import com.xenosnowfox.engine.display.MonitorFactory;
import com.xenosnowfox.engine.display.Window;
import com.xenosnowfox.engine.graphics.Texture;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

import java.util.Properties;

/**
 * Main Spike Entrypoint.
 */
public class Spike implements GameLogic {

	/**
	 * Application entry point.
	 *
	 * @param args Commandline arguments.
	 */
	public static void main(String... args) {
		try {
			System.out.println("Starting Spike.");
			final Spike spike = new Spike();
			final GameLoop gameLoop = new GameLoop(spike.window, spike);
			gameLoop.run();
		} catch (Exception excp) {
			excp.printStackTrace();
			System.exit(-1);
		}
	}

	private final Properties spikeProperties;

	private final Window window;

	private Texture texture;

	public Spike() throws Exception {
		System.out.println("Loading properties file.");
		this.spikeProperties = Utils.loadProperties("spike.properties");
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
//		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
//		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
//		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
//		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
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


		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		GL46.glEnableClientState(GL46.GL_VERTEX_ARRAY);
	}

	@Override
	public void init() throws Exception {


		// load a texture
		final String textureFileName = spikeProperties.getProperty("textures.directory") + "bricks.png";
		System.out.println("Loading texture: " + textureFileName);
		this.texture = new Texture(textureFileName);
	}

	@Override
	public void input() {

	}

	@Override
	public void update(final float interval) {

	}

	@Override
	public void render() {

		// Set the clear color
		GL46.glClearColor(43f / 255f, 43f / 255f, 43f / 255f, 0f); // BG color
		GL46.glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color
		GL46.glEnable(GL46.GL_DEPTH_TEST);


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

	@Override
	public void cleanUp() {

		GL46.glDisableClientState(GL46.GL_VERTEX_ARRAY);
		this.texture.destroy();
	}

}
