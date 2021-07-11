package spike.lwjglbuildingrenderer;

import com.xenosnowfox.engine.graphics.GameItem;
import com.xenosnowfox.engine.graphics.Material;
import com.xenosnowfox.engine.graphics.Mesh;
import com.xenosnowfox.engine.graphics.MouseInput;
import com.xenosnowfox.engine.graphics.OBJLoader;
import com.xenosnowfox.engine.graphics.PointLight;
import com.xenosnowfox.engine.graphics.Texture;
import com.xenosnowfox.engine.graphics.Transformation;
import com.xenosnowfox.lwjglengine.GameLogic;
import com.xenosnowfox.lwjglengine.GameLoop;
import com.xenosnowfox.lwjglengine.Viewport;
import com.xenosnowfox.lwjglengine.display.Monitor;
import com.xenosnowfox.lwjglengine.display.Window;
import com.xenosnowfox.lwjglengine.projection.Camera;
import com.xenosnowfox.lwjglengine.shader.ShaderProgram;
import com.xenosnowfox.lwjglengine.shader.ShaderType;
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
public class Spike extends GameLoop implements GameLogic {

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
			new Spike()
					.run();
		} catch (Exception excp) {
			excp.printStackTrace();
			System.exit(-1);
		}
	}

	private final Properties spikeProperties;

	private final Window window;

	private final Camera camera;

	private final Viewport viewport;

	private final MouseInput mouseInput;

	private Texture texture;

	private Material material;

	private Mesh mesh;

	private GameItem[] gameItems;

	private ShaderProgram shaderProgram;

	private float specularPower = 5f;

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

		// create a new window
		this.window = Window.builder()
				.title(spikeProperties.getProperty("window.title", "Spike"))
				.width(Integer.parseInt(spikeProperties.getProperty("window.width", "640")))
				.height(Integer.parseInt(spikeProperties.getProperty("window.height", "480")))
				.visible(false)
				.resizable(true)
				.centerCursor(true)
				.build();

		// center the window on the monitor
		this.window.centerOnMonitor(monitor);
		this.window.setAsCurrentRenderingContext();
		this.window.show();
		this.window.focus();
		this.window.requestAttention();

		this.camera = new Camera();
		this.camera.position(0f, 1.65f, 1f);

		this.mouseInput = new MouseInput();

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		GL46.glEnableClientState(GL46.GL_VERTEX_ARRAY);

		// create a viewport
		this.viewport = new Viewport(this.window, this.camera, this);
		this.addViewport(this.viewport);
	}

	@Override
	public void initialize() throws Exception {

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

		GameItem gameItem2 = new GameItem(mesh);
		gameItem2.setScale(1f, 1f, 1f);
		gameItem2.setPosition(1f, 0, 0);
		gameItem2.setRotation(0f, 90f, 0f);

		GameItem gameItem3 = new GameItem(mesh);
		gameItem3.setScale(1f, 1f, 1f);
		gameItem3.setPosition(0.5f, 0, 0);

		gameItems = new GameItem[]{gameItem, gameItem2, gameItem3};

		// load shader program
		System.out.println("Loading shader program.");
		this.shaderProgram = ShaderProgram.builder()
				// shaders
				.shader(builder -> {
					try {
						builder.type(ShaderType.VERTEX).fromResourceFile("/shaders/vertex.vs");
					} catch (Exception exception) {
						throw new RuntimeException(exception);
					}
				})
				.shader(builder -> {
					try {
						builder.type(ShaderType.FRAGMENT).fromResourceFile("/shaders/fragment.fs");
					} catch (Exception exception) {
						throw new RuntimeException(exception);
					}
				})
				// Create uniforms for modelView and projection matrices and texture
				.uniform("projectionMatrix")
				.uniform("modelViewMatrix")
				.uniform("texture_sampler")
				// Create uniform for material
				.uniform("material.ambient")
				.uniform("material.diffuse")
				.uniform("material.specular")
				.uniform("material.hasTexture")
				.uniform("material.reflectance")
				.uniform("specularPower")
				.uniform("ambientLight")
				// Create lighting related uniforms
				.uniform("pointLight.colour")
				.uniform("pointLight.position")
				.uniform("pointLight.intensity")
				.uniform("pointLight.att.constant")
				.uniform("pointLight.att.linear")
				.uniform("pointLight.att.exponent")
				// build
				.build();

		// ambient light source
		ambientLight = new Vector3f(1f, 1f, 1f);

		Vector3f lightColour = new Vector3f(1, 1, 1);
		Vector3f lightPosition = new Vector3f(0, 0, -2);
		float lightIntensity = 1.0f;
		pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
		PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
		pointLight.setAttenuation(att);
	}

	@Override
	public void input() {

		if (this.window.shouldClose()) {
			this.stop();
			return;
		}
		this.mouseInput.input(this.window);

		List<String> suffixParts = new ArrayList<>();
		camera.position(vector3f -> {
			suffixParts.add("X:" + vector3f.x);
			suffixParts.add("Y:" + vector3f.y);
			suffixParts.add("Z:" + vector3f.z);
		});
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
		Vector2f rotVec = mouseInput.getDisplVec();
		camera.position(position -> {
			final float offsetX = cameraInc.x * CAMERA_POS_STEP;
			final float offsetY = cameraInc.y * CAMERA_POS_STEP;
			final float offsetZ = cameraInc.z * CAMERA_POS_STEP;
			final float rotateX = rotVec.x * MOUSE_SENSITIVITY;
			final float rotateY = rotVec.y * MOUSE_SENSITIVITY;

			camera.rotation(rotation -> {
				rotation.x += rotateX;
				rotation.y += rotateY;

				if ( offsetZ != 0 ) {
					position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
					position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
				}
				if ( offsetX != 0) {
					position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
					position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
				}
				position.y += offsetY;
			});
		});

	}

	@Override
	public void render() {

		// Set the clear color
		GL46.glClearColor(250f / 255f, 43f / 255f, 43f / 255f, 1f); // BG color
		GL46.glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color
		GL46.glEnable(GL46.GL_DEPTH_TEST);
		GL46.glEnable(GL46.GL_CULL_FACE);

		// start new render pass
		GL46.glLoadIdentity();
		GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		GL46.glCullFace(GL46.GL_BACK);
		GL46.glFrontFace(GL46.GL_CW);

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
		shaderProgram.uniform("projectionMatrix", projectionMatrix);

		// Update view Matrix
		Matrix4f viewMatrix = transformation.getViewMatrix(this.camera);

		// Update Light Uniforms
		shaderProgram.uniform("ambientLight", ambientLight);
		shaderProgram.uniform("specularPower", specularPower);

		// Get a copy of the light object and transform its position to view coordinates
		PointLight currPointLight = new PointLight(pointLight);
		Vector3f lightPos = currPointLight.getPosition();
		Vector4f aux = new Vector4f(lightPos, 1f);
		aux.mul(viewMatrix);
		lightPos.x = aux.x;
		lightPos.y = aux.y;
		lightPos.z = aux.z;
		shaderProgram.uniform("pointLight.colour", pointLight.getColor());
		shaderProgram.uniform("pointLight.position", pointLight.getPosition());
		shaderProgram.uniform("pointLight.intensity", pointLight.getIntensity());
		PointLight.Attenuation att = pointLight.getAttenuation();
		shaderProgram.uniform("pointLight.att.constant", att.getConstant());
		shaderProgram.uniform("pointLight.att.linear", att.getLinear());
		shaderProgram.uniform("pointLight.att.exponent", att.getExponent());

		shaderProgram.uniform("texture_sampler", 0);

		// Render each gameItem
		for (GameItem gameItem : gameItems) {
			Mesh mesh = gameItem.getMesh();
			// Set model view matrix for this item
			Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
			shaderProgram.uniform("modelViewMatrix", modelViewMatrix);

			// Render the mesh for this game item
			shaderProgram.uniform("material.ambient", material.getAmbientColour());
			shaderProgram.uniform("material.diffuse", material.getDiffuseColour());
			shaderProgram.uniform("material.specular", material.getSpecularColour());
			shaderProgram.uniform("material.hasTexture", material.isTextured() ? 1 : 0);
			shaderProgram.uniform("material.reflectance", material.getReflectance());
			mesh.render();
		}

		shaderProgram.unbind();
	}

	@Override
	public void postRender() {
		// swap the buffers
		this.window.swapBuffers();
		this.mouseInput.centerOnWindow(this.window);
	}

	@Override
	public void destroy() {
		GL46.glDisableClientState(GL46.GL_VERTEX_ARRAY);
		this.shaderProgram.destroy();
		this.mesh.cleanUp();
		this.texture.destroy();
	}

}
