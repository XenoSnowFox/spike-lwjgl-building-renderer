package spike.lwjglbuildingrenderer;

import com.xenosnowfox.engine.GameLogic;
import com.xenosnowfox.engine.GameLoop;
import com.xenosnowfox.engine.graphics.Camera;
import com.xenosnowfox.engine.graphics.GameItem;
import com.xenosnowfox.engine.graphics.Material;
import com.xenosnowfox.engine.graphics.Mesh;
import com.xenosnowfox.engine.graphics.MouseInput;
import com.xenosnowfox.engine.graphics.OBJLoader;
import com.xenosnowfox.engine.graphics.PointLight;
import com.xenosnowfox.engine.graphics.ShaderProgram;
import com.xenosnowfox.engine.graphics.Texture;
import com.xenosnowfox.engine.graphics.Transformation;
import com.xenosnowfox.lwjglengine.display.Monitor;
import com.xenosnowfox.lwjglengine.display.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Main Spike Entrypoint.
 */
public class Spike implements GameLogic {

	private final static int KEY_FORWARD = GLFW.GLFW_KEY_W;
	private final static int KEY_BACKWARD = GLFW.GLFW_KEY_S;
	private final static int KEY_LEFT = GLFW.GLFW_KEY_A;
	private final static int KEY_RIGHT = GLFW.GLFW_KEY_D;
	private final static int KEY_UP = GLFW.GLFW_KEY_Q;
	private final static int KEY_DOWN = GLFW.GLFW_KEY_E;

	private static final float CAMERA_POS_STEP = 0.05f;
	private static final float MOUSE_SENSITIVITY = 0.2f;

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

	private final Camera camera;

	private final MouseInput mouseInput;

	private Texture texture;

	private Material material;

	private Mesh mesh;

	private GameItem[] gameItems;

	private ShaderProgram shaderProgram;

	private float specularPower = 10f;

	/**
	 * Field of View in Radians
	 */
	private static final float FOV = (float) Math.toRadians(60.0f);

	private static final float Z_NEAR = 0.01f;

	private static final float Z_FAR = 1000.f;

	private final Transformation transformation = new Transformation();

	private float cameraOffset = 0f;

	private Vector3f ambientLight;

	private PointLight pointLight;

	private final Vector3f cameraInc = new Vector3f();


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
		final Monitor monitor = Monitor.factory()
				.getPrimaryMonitor();

		// configure GLFW
		GLFW.glfwDefaultWindowHints(); // optional, the current window hints are already the default
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // the window will stay hidden after creation
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // the window will be resizable
//		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
//		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
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

		this.camera = new Camera();
		this.camera.setPosition(0f, 1.85f, 3f);

		this.mouseInput = new MouseInput();

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

		// initialize mouse input
		this.mouseInput.init(this.window);
		this.mouseInput.centerOnWindow(this.window);

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
		gameItem.setScale(1f, 1f, 1f);
		gameItem.setPosition(1f, 0, 0);
		gameItems = new GameItem[]{gameItem};

		// load shader program
		System.out.println("Loading shader program.");
		shaderProgram = new ShaderProgram();
		shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
		shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
		shaderProgram.link();

		// Create uniforms for modelView and projection matrices and texture
		shaderProgram.createUniform("projectionMatrix");
		shaderProgram.createUniform("modelViewMatrix");
		shaderProgram.createUniform("texture_sampler");
		// Create uniform for material
		shaderProgram.createMaterialUniform("material");
		// Create lighting related uniforms
		shaderProgram.createUniform("specularPower");
		shaderProgram.createUniform("ambientLight");
		shaderProgram.createPointLightUniform("pointLight");

		// ambient light source
		ambientLight = new Vector3f(1f, 1f, 1f);

		Vector3f lightColour = new Vector3f(1, 1, 1);
		Vector3f lightPosition = new Vector3f(0, 0, 1);
		float lightIntensity = 1.0f;
		pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
		PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
		pointLight.setAttenuation(att);
	}

	@Override
	public void input() {

		this.mouseInput.input(this.window);

		List<String> suffixParts = new ArrayList<>();
		suffixParts.add("X:" + camera.getPosition().x);
		suffixParts.add("Y:" + camera.getPosition().y);
		suffixParts.add("Z:" + camera.getPosition().z);
		this.window.setTitle(spikeProperties.getProperty("window.title", "Spike") + " [" + String.join(", ", suffixParts) + "]");

		cameraInc.set(0f, 0f, 0f);

		if (window.isKeyPressed(KEY_FORWARD)) {
			cameraInc.z = -1;
		} else if (window.isKeyPressed(KEY_BACKWARD)) {
			cameraInc.z = 1;
		}
		if (window.isKeyPressed(KEY_LEFT)) {
			cameraInc.x = -1;
		} else if (window.isKeyPressed(KEY_RIGHT)) {
			cameraInc.x = 1;
		}
		if (window.isKeyPressed(KEY_UP)) {
			cameraInc.y = 1;
		} else if (window.isKeyPressed(KEY_DOWN)) {
			cameraInc.y = -1;
		}
	}

	@Override
	public void update(final float interval) {
		camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

		Vector2f rotVec = mouseInput.getDisplVec();
		camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
	}

	@Override
	public void render() {
		// this.gameItems[0].movePosition(0, 0, -this.cameraOffset);


		// Set the clear color
		GL46.glClearColor(43f / 255f, 43f / 255f, 43f / 255f, 0f); // BG color
		GL46.glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color
		GL46.glEnable(GL46.GL_DEPTH_TEST);
		// start new render pass
		GL46.glLoadIdentity();
		GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		// adjust the viewport
		if (window.isResized()) {
			GL46.glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResized(false);
		}
		// Poll for window events. The key callback above will only be invoked during this call.
		GLFW.glfwPollEvents();

		// update scene
		shaderProgram.bind();

		// Update projection Matrix
		Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);

		// Update view Matrix
		Matrix4f viewMatrix = transformation.getViewMatrix(this.camera);

		// Update Light Uniforms
		 shaderProgram.setUniform("ambientLight", ambientLight);
		 shaderProgram.setUniform("specularPower", specularPower);

		// Get a copy of the light object and transform its position to view coordinates
		PointLight currPointLight = new PointLight(pointLight);
		Vector3f lightPos = currPointLight.getPosition();
		Vector4f aux = new Vector4f(lightPos, 1);
		aux.mul(viewMatrix);
		lightPos.x = aux.x;
		lightPos.y = aux.y;
		lightPos.z = aux.z;
		shaderProgram.setUniform("pointLight", currPointLight);

		 shaderProgram.setUniform("texture_sampler", 0);

		// Render each gameItem
		for (GameItem gameItem : gameItems) {
			Mesh mesh = gameItem.getMesh();
			// Set model view matrix for this item
			Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
			shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
			// Render the mesh for this game item
			shaderProgram.setUniform("material", mesh.getMaterial());
			mesh.render();
		}

		 shaderProgram.unbind();

		// swap the buffers
		this.window.swapBuffers();

		this.mouseInput.centerOnWindow(this.window);
	}

	@Override
	public void cleanUp() {
		GL46.glDisableClientState(GL46.GL_VERTEX_ARRAY);
		this.shaderProgram.cleanup();
		this.mesh.cleanUp();
		this.texture.destroy();
	}

}
