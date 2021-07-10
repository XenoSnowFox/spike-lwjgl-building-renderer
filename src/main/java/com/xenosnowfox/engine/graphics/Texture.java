package com.xenosnowfox.engine.graphics;

import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;



public class Texture {

	public static int loadFromFile(String withFileName) throws Exception {
		int width;
		int height;
		ByteBuffer buf;
		// Load Texture file
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);

			buf = STBImage.stbi_load(withFileName, w, h, channels, 4);
			if (buf == null) {
				throw new Exception("Image file [" + withFileName + "] not loaded: " + STBImage.stbi_failure_reason());
			}

			width = w.get();
			height = h.get();
		}

		// Create a new OpenGL texture
		int textureId = GL46.glGenTextures();
		// Bind the texture
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, textureId);

		// Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
		GL46.glPixelStorei(GL46.GL_UNPACK_ALIGNMENT, 1);

		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		// Upload the texture data
		GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGBA, width, height, 0,
				GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, buf);
		// Generate Mip Map
		GL46.glGenerateMipmap(GL46.GL_TEXTURE_2D);

		STBImage.stbi_image_free(buf);

		return textureId;
	}

	private final int id;

	private Texture(final int withId) {
		this.id = withId;
	}

	public Texture(final String withFileName) throws Exception {
		this(loadFromFile(withFileName));
	}

	public int getId() {
		return this.id;
	}

	public void bind() {
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, this.getId());
	}

	public void destroy() {
		GL46.glDeleteTextures(this.getId());
	}
}
