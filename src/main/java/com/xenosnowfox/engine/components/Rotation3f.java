package com.xenosnowfox.engine.components;

import org.joml.Vector3f;

public interface Rotation3f {

	Vector3f getRotation();

	void setRotation(float x, float y, float z);

	void moveRotation(float offsetX, float offsetY, float offsetZ);
}
