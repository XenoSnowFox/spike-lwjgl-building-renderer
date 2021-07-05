package spike.lwjglbuildingrenderer.lwjgl;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Objects;
import java.util.function.BiConsumer;

import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Help class for managing a window.
 */
public class Window {

	/**
	 * Handle to the window.
	 */
	private long handle;

	/**
	 * Current width of the window.
	 */
	private int width;

	/**
	 * Current height of the window.
	 */
	private int height;

	/**
	 * Window title.
	 */
	private String title;

	/**
	 * Instantiates a new window.
	 *
	 * @param withTitle
	 * @param withWidth
	 * @param withHeight
	 */
	public Window(final String withTitle, final int withWidth, final int withHeight) {
		this.title = withTitle;
		this.width = withWidth;
		this.height = withHeight;

		// create the window handle.
		this.handle = GLFW.glfwCreateWindow(this.getWidth(), this.getHeight(), this.getTitle(), 0L, 0L);
		if (this.handle == 0L) {
			throw new RuntimeException("Unable to create window.");
		}
	}

	/**
	 * Returns the handle to the window.
	 *
	 * @return Window handle.
	 */
	public long getHandle() {
		return this.handle;
	}

	/**
	 * Returns the window's current width.
	 *
	 * @return width of the window.
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Returns the window's current height.
	 *
	 * @return height of the window.
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns the title set on the window.
	 *
	 * @return Window title.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Makes the window visible if it was previously hidden. If the window is already visible or is in full screen mode,
	 * this function does nothing.
	 *
	 * <p>This function must only be called from the main thread.</p>
	 */
	public void show() {
		GLFW.glfwShowWindow(this.getHandle());
	}

	/**
	 * Hides the window, if it was previously visible. If the window is already hidden or is in full screen mode, this
	 * function does nothing.
	 *
	 * <p>This function must only be called from the main thread.</p>
	 */
	public void hide() {
		GLFW.glfwHideWindow(this.getHandle());
	}

	/**
	 * Brings the specified window to front and sets input focus. The window should already be visible and not iconified.
	 *
	 * <p>By default, both windowed and full screen mode windows are focused when initially created. Set the {@link GLFW#GLFW_FOCUSED FOCUSED} hint to disable this behavior.</p>
	 *
	 * <p>Also by default, windowed mode windows are focused when shown with {@link GLFW#glfwShowWindow ShowWindow}. Set the {@link GLFW#GLFW_FOCUS_ON_SHOW FOCUS_ON_SHOW} window hint to disable this behavior.</p>
	 *
	 * <p><b>Do not use this function</b> to steal focus from other applications unless you are certain that is what the user wants. Focus stealing can be extremely disruptive.</p>
	 *
	 * <p>For a less disruptive way of getting the user's attention, see {@link GLFW#glfwRequestWindowAttention RequestWindowAttention}.</p>
	 *
	 */
	public void focus() {
		GLFW.glfwFocusWindow(this.getHandle());
	}

	public void requestAttention() {
		GLFW.glfwRequestWindowAttention(this.getHandle());
	}

	public void getSize(final BiConsumer<Integer, Integer> withConsumer) {
		int width;
		int height;
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*
			glfwGetWindowSize(this.getHandle(), pWidth, pHeight);
			width = pWidth.get(0);
			height = pHeight.get(0);
		}
		Objects.requireNonNull(withConsumer).accept(width, height);
	}

	public void centerOnMonitor(final Monitor withMonitor) {
		this.getSize((width, height) -> {
			VideoMode videoMode = Objects.requireNonNull(withMonitor).getVideoMode();

			// Center the window
			glfwSetWindowPos(
					this.getHandle(),
					(videoMode.getWidth() - width) / 2,
					(videoMode.getHeight() - height) / 2
			);
		});
	}

	/**
	 * Returns whether the window should be closed.
	 *
	 * @return {@code true} if the window should be closed.
	 */
	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(this.getHandle());
	}

	public void swapBuffers() {
		GLFW.glfwSwapBuffers(this.getHandle());
	}

	/**
	 * Defines the number of screen updates to wait before swapping buffers.
	 * @param withInterval
	 */
	public void swapInterval(final int withInterval) {
		GLFW.glfwSwapInterval(withInterval);
	}

	public void setAsCurrentRenderingContext() {
		GLFW.glfwMakeContextCurrent(this.getHandle());
	}

	public void freeCallbacks() {
		Callbacks.glfwFreeCallbacks(this.getHandle());
	}

	public void destroy() {
		GLFW.glfwDestroyWindow(this.getHandle());
	}

}
