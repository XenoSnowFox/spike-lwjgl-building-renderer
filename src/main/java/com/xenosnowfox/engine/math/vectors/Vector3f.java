package com.xenosnowfox.engine.math.vectors;

/**
 * A vector composed of 3 floating point values (x, y and z).
 */
public class Vector3f {

	/**
	 * X value.
	 */
	public float x;

	/**
	 * Y value.
	 */
	public float y;

	/**
	 * Z value.
	 */
	public float z;

	/**
	 * Instantiates a new instance with an initial zero value.
	 */
	public Vector3f() {
		this(0f, 0f, 0f);
	}

	/**
	 * Instantiates a new instance.
	 *
	 * @param withX
	 * 		X value
	 * @param withY
	 * 		Y value
	 */
	public Vector3f(final float withX, final float withY, final float withZ) {
		this.x = withX;
		this.y = withY;
		this.z = withZ;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String toString() {
		return "Vector3f{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
	}

	/**
	 * Copy constructor.
	 *
	 * @param other
	 * 		instance to copy date from
	 */
	public Vector3f(final Vector3f other) {
		this(other.x, other.y, other.z);
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
	 * Returns the Z portion of the vector.
	 *
	 * @return Z value
	 */
	public float getZ() {
		return this.z;
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

	/**
	 * Defines the Z portion of the vector.
	 *
	 * @param withZ
	 * 		new Z value
	 */
	public void setZ(final float withZ) {
		this.z = withZ;
	}

}
