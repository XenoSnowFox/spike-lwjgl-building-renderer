package com.xenosnowfox.engine.graphics;

import com.xenosnowfox.engine.components.Transformation3f;
import org.joml.Vector3f;

public class Camera implements Transformation3f {

    private final Vector3f position;
    
    private final Vector3f rotation;

    private final Vector3f scale;
    
    public Camera() {
        position = new Vector3f();
        rotation = new Vector3f();
        scale = new Vector3f();
    }
    
    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
        this.scale = new Vector3f();
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    @Override
    public void setPosition(final Vector3f withVector) {
        position.x = withVector.x;
        position.y = withVector.y;
        position.z = withVector.z;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    @Override
    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if ( offsetY != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.z)) * -1.0f * offsetY;
            position.y += (float)Math.cos(Math.toRadians(rotation.z)) * offsetY;
        }
        if ( offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.z - 90)) * -1.0f * offsetX;
            position.y += (float)Math.cos(Math.toRadians(rotation.z - 90)) * offsetX;
        }
        position.z += offsetZ;
    }

    @Override
    public Vector3f getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    @Override
    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }

    @Override
    public Vector3f getScale() {
        return this.scale;
    }

    @Override
    public void setScale(final float x, final float y, final float z) {
        scale.x = x;
        scale.y = y;
        scale.z = z;
    }

    @Override
    public void moveScale(final float offsetX, final float offsetY, final float offsetZ) {
        scale.x += offsetX;
        scale.y += offsetY;
        scale.z += offsetZ;
    }
}