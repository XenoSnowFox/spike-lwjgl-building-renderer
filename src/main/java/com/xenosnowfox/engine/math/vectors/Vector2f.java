package com.xenosnowfox.engine.math.vectors;

/**
 * A vector composed of 2 float values.
 */
public class Vector2f {

	/**
	 * X value.
	 */
	private float x;

	/**
	 * Y value.
	 */
	private float y;

	/**
	 * Instantiates a new instance with an initial zero value.
	 */
	public Vector2f() {
		this(0f, 0f);
	}

	/**
	 * Instantiates a new instance.
	 *
	 * @param withX
	 * 		X value
	 * @param withY
	 * 		Y value
	 */
	public Vector2f(final float withX, final float withY) {
		this.x = withX;
		this.y = withY;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String toString() {
		return "Vector2f{x=" + this.x + ", y=" + this.y + "}";
	}

	/**
	 * Copy constructor.
	 *
	 * @param other
	 * 		instance to copy date from
	 */
	public Vector2f(final Vector2f other) {
		this(other.getX(), other.getY());
	}

	/**
	 * Returns the X portion of the vector.
	 *
	 * @return X value
	 */
	public float getX() {
		return this.x;
	}

	/**
	 * Returns the Y portion of the vector.
	 *
	 * @return Y value
	 */
	public float getY() {
		return this.y;
	}

	/**
	 * Defines the X portion of the vector.
	 *
	 * @param withX
	 * 		new X value
	 */
	public void setX(final float withX) {
		this.x = withX;
	}

	/**
	 * Defines the Y portion of the vector.
	 *
	 * @param withY
	 * 		new Y value
	 */
	public void setY(final float withY) {
		this.y = withY;
	}

}
