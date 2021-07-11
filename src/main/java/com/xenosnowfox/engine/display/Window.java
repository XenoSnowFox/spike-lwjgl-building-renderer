package com.xenosnowfox.engine.display;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Class for managing a game window.
 */
public class Window {

	/**
	 * Title of the window.
	 */
	private final String title;
	private String titleSuffix = "";

	/**
	 * Width, in pixels, of the window.
	 */
	private int width;

	/**
	 * Height, in pixels, of the window.
	 */
	private int height;

	/**
	 * Handle to the window.
	 */
	private final long windowHandle;

	/**
	 * Flag indicating if the window has been resized.
	 */
	private boolean resized;

	/**
	 * Flag indicating if vSync has been activated.
	 */
	private final boolean vSync;

	/**
	 * Instantiates a new window instance.
	 *
	 * @param withTitle
	 * 		Title of the window.
	 * @param withWidth
	 * 		Width, in pixels, of the window.
	 * @param withHeight
	 * 		Height, in pixels, of the window.
	 * @param withVSync
	 * 		Flag indicating if vSync has been activated.
	 */
	public Window(final String withTitle, final int withWidth, final int withHeight, final boolean withVSync) {
		this.title = withTitle;
		this.width = withWidth;
		this.height = withHeight;
		this.vSync = withVSync;
		this.resized = false;

		// Create the window
		windowHandle = GLFW.glfwCreateWindow(this.width, this.height, String.join("", this.title, this.titleSuffix), NULL, NULL);
		if (windowHandle == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Setup resize callback
		GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
			this.width = width;
			this.height = height;
			this.resized = true;
		});

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		GLFW.glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
				GLFW.glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			}
		});

		// enable v-sync
		if (this.isVSync()) {
			this.setAsCurrentRenderingContext();
			GLFW.glfwSwapInterval(1);
		}
	}

	/**
	 * Returns the handle to the window.
	 *
	 * @return Window handle.
	 */
	public long getWindowHandle() {
		return this.windowHandle;
	}

	/**
	 * Returns the window title.
	 *
	 * @return Title of the window.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Returns the width of the window.
	 *
	 * @return Window width.
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Returns the height of the window.
	 *
	 * @return Window height.
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns whether the window has been resized.
	 *
	 * @return {@code true} if the window was resized, {@code false} otherwise.
	 */
	public boolean isResized() {
		return this.resized;
	}

	public void setResized(final boolean isResized) {
		this.resized = isResized;
	}

	/**
	 * Returns whether VSync is on.
	 *
	 * @return {@code true} is vSync is enabled, {@code false} otherwise.
	 */
	public boolean isVSync() {
		return this.vSync;
	}

	/**
	 * Makes the window visible if it was previously hidden. If the window is already visible or is in full screen
	 * mode,
	 * this function does nothing.
	 *
	 * <p>This function must only be called from the main thread.</p>
	 */
	public void show() {
		GLFW.glfwShowWindow(this.getWindowHandle());
	}

	/**
	 * Hides the window, if it was previously visible. If the window is already hidden or is in full screen mode, this
	 * function does nothing.
	 *
	 * <p>This function must only be called from the main thread.</p>
	 */
	public void hide() {
		GLFW.glfwHideWindow(this.getWindowHandle());
	}

	/**
	 * Brings the specified window to front and sets input focus. The window should already be visible and not
	 * iconified.
	 *
	 * <p>By default, both windowed and full screen mode windows are focused when initially created. Set the {@link
	 * GLFW#GLFW_FOCUSED FOCUSED} hint to disable this behavior.</p>
	 *
	 * <p>Also by default, windowed mode windows are focused when shown with {@link GLFW#glfwShowWindow ShowWindow}.
	 * Set the {@link GLFW#GLFW_FOCUS_ON_SHOW FOCUS_ON_SHOW} window hint to disable this behavior.</p>
	 *
	 * <p><b>Do not use this function</b> to steal focus from other applications unless you are certain that is what
	 * the user wants. Focus stealing can be extremely disruptive.</p>
	 *
	 * <p>For a less disruptive way of getting the user's attention, see {@link GLFW#glfwRequestWindowAttention
	 * RequestWindowAttention}.</p>
	 */
	public void focus() {
		GLFW.glfwFocusWindow(this.getWindowHandle());
	}

	/**
	 * Requests user's attention on the window.
	 */
	public void requestAttention() {
		GLFW.glfwRequestWindowAttention(this.getWindowHandle());
	}

	/**
	 * Returns whether the window should be closed.
	 *
	 * @return {@code true} if the window should be closed.
	 */
	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(this.getWindowHandle());
	}

	/**
	 * Swaps this window's rendering buffers.
	 */
	public void swapBuffers() {
		GLFW.glfwSwapBuffers(this.getWindowHandle());
	}

	/**
	 * Sets this window as the current rendering context.
	 */
	public void setAsCurrentRenderingContext() {
		GLFW.glfwMakeContextCurrent(this.getWindowHandle());
	}

	/**
	 * Resets all callbacks for this window.
	 */
	public void freeCallbacks() {
		Callbacks.glfwFreeCallbacks(this.getWindowHandle());
	}

	/**
	 * Destroys this window and it's context.
	 */
	public void destroy() {
		GLFW.glfwDestroyWindow(this.getWindowHandle());
	}

	/**
	 * Attempts to center the window on the specified monitor.
	 *
	 * @param withMonitor monitor to center the window on.
	 */
	public void centerOnMonitor(final Monitor withMonitor) {
		VideoMode videoMode = Objects.requireNonNull(withMonitor)
				.getVideoMode();

		// Center the window
		GLFW.glfwSetWindowPos(
				this.getWindowHandle(),
				(videoMode.getWidth() - this.getWidth()) / 2,
				(videoMode.getHeight() - this.getHeight()) / 2
		);
	}

	/**
	 * Returns whether the given key is currently pressed.
	 *
	 * @param keyCode Key to check for.
	 * @return {@code true} if the key is currently pressed, or {@code false} otherwise.
	 */
	public boolean isKeyPressed(int keyCode) {
		return GLFW.glfwGetKey(windowHandle, keyCode) == GLFW.GLFW_PRESS;
	}

	public void setTitleSuffix(final String withSuffix) {
		this.titleSuffix = Objects.requireNonNull(withSuffix);
		GLFW.glfwSetWindowTitle(this.windowHandle, String.join(" ", this.title, this.titleSuffix));
	}
}
