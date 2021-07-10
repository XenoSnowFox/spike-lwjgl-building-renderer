package spike.lwjglbuildingrenderer;

import com.xenosnowfox.engine.GameLogic;
import com.xenosnowfox.engine.GameLoop;
import com.xenosnowfox.engine.display.Monitor;
import com.xenosnowfox.engine.display.MonitorFactory;
import com.xenosnowfox.engine.display.Window;
import com.xenosnowfox.engine.graphics.GameItem;
import com.xenosnowfox.engine.graphics.Material;
import com.xenosnowfox.engine.graphics.Mesh;
import com.xenosnowfox.engine.graphics.OBJLoader;
import com.xenosnowfox.engine.graphics.Texture;
import org.joml.Matrix4f;
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

	private Material material;

	private Mesh mesh;

	private GameItem[] gameItems;

	/**
	 * Field of View in Radians
	 */
	private static final float FOV = (float) Math.toRadians(60.0f);

	private static final float Z_NEAR = 0.01f;

	private static final float Z_FAR = 1000.f;


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

		// load a material
		System.out.println("Creating Material");
		float reflectance = 1f;
		this.material = new Material(this.texture, reflectance);

		// load a mesh
		final String meshFileName = spikeProperties.getProperty("models.directory") + "arch.obj";
		System.out.println("Loading mesh: " + meshFileName);
		this.mesh = OBJLoader.loadMeshFile(meshFileName);
		this.mesh.setMaterial(this.material);

		// load mesh into a game object
		System.out.println("Converting mesh into game item.");
		GameItem gameItem = new GameItem(mesh);
		gameItem.setScale(0.5f);
		gameItem.setPosition(0, 0, 0);
		gameItems = new GameItem[]{gameItem};
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

		// Render each gameItem
		for (GameItem gameItem : gameItems) {
			Mesh mesh = gameItem.getMesh();
			// Set model view matrix for this item
			//Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
			//shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
			// Render the mesh for this game item
			//shaderProgram.setUniform("material", mesh.getMaterial());
			mesh.render();
		}

		// shaderProgram.unbind();

		// swap the buffers
		this.window.swapBuffers();
	}

	@Override
	public void cleanUp() {
		GL46.glDisableClientState(GL46.GL_VERTEX_ARRAY);
		this.mesh.cleanUp();
		this.texture.destroy();
	}

}
