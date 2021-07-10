package com.xenosnowfox.engine.components;

import org.joml.Vector3f;

public interface Scale3f {

	Vector3f getScale();

	void setScale(float x, float y, float z);

	void moveScale(float offsetX, float offsetY, float offsetZ);
}
