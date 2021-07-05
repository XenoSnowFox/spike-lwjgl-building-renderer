package spike.lwjglbuildingrenderer.lwjgl;

import org.lwjgl.glfw.GLFWVidMode;

/**
 * Video Mode.
 */
public class VideoMode {

	private final int width;

	private final int height;

	private final int redBits;

	private final int blueBits;

	private final int greenBits;

	private final int refreshRate;

	public VideoMode(GLFWVidMode withGLFWVidMode) {
		this.width = withGLFWVidMode.width();
		this.height = withGLFWVidMode.height();
		this.redBits = withGLFWVidMode.redBits();
		this.blueBits = withGLFWVidMode.blueBits();
		this.greenBits = withGLFWVidMode.greenBits();
		this.refreshRate = withGLFWVidMode.refreshRate();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getRedBits() {
		return redBits;
	}

	public int getBlueBits() {
		return blueBits;
	}

	public int getGreenBits() {
		return greenBits;
	}

	public int getRefreshRate() {
		return refreshRate;
	}

}
