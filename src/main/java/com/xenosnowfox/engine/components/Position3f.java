package com.xenosnowfox.engine.components;

import org.joml.Vector3f;

public interface Position3f {

	Vector3f getPosition();

	void setPosition(Vector3f withVector);

	void setPosition(float withX, float withY, float withZ);

	void movePosition(float offsetX, float offsetY, float offsetZ);
}
