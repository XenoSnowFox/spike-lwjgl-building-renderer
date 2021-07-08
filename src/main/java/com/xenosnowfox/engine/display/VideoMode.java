package com.xenosnowfox.engine.display;

import org.lwjgl.glfw.GLFWVidMode;

import java.util.Objects;

/**
 * Represents the details of a video mode.
 */
public class VideoMode {

	/**
	 * UnderlyingGLFW Video mode.
	 */
	private final GLFWVidMode glfwVidMode;

	/**
	 * Instantiates a new instance.
	 *
	 * @param withGLFWVidMode
	 * 		GLFW Video Mode.
	 * @throws NullPointerException
	 * 		if provided with a null value
	 */
	public VideoMode(final GLFWVidMode withGLFWVidMode) {
		this.glfwVidMode = Objects.requireNonNull(withGLFWVidMode);
	}

	/**
	 * Returns the width of the video mode in pixels.
	 *
	 * @return width
	 */
	public int getWidth() {
		return this.glfwVidMode.width();
	}

	/**
	 * Returns the height of the video mode in pixels.
	 *
	 * @return height
	 */
	public int getHeight() {
		return this.glfwVidMode.height();
	}

	/**
	 * Returns the number of bits that make up the red colour channel.
	 *
	 * @return number of red bits
	 */
	public int getRedBits() {
		return this.glfwVidMode.redBits();
	}

	/**
	 * Returns the number of bits that make up the green colour channel.
	 *
	 * @return number of green bits.
	 */
	public int getGreenBits() {
		return this.glfwVidMode.greenBits();
	}

	/**
	 * Returns the number of bits that make up the blue colour channel.
	 *
	 * @return number of blue bits.
	 */
	public int getBlueBits() {
		return this.glfwVidMode.blueBits();
	}

	/**
	 * Refresh rate of the video mode in hertz.
	 *
	 * @return refresh rate.
	 */
	public int getRefreshRate() {
		return this.glfwVidMode.refreshRate();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String toString() {
		return "VideoMode{width=" + this.getWidth()
				+ ", height=" + this.getHeight()
				+ ", redBit=" + this.getRedBits()
				+ ", greenBits=" + this.getGreenBits()
				+ ", blueBits=" + this.getBlueBits()
				+ ", refreshRate=" + this.getRefreshRate()
				+ "}";
	}
}
