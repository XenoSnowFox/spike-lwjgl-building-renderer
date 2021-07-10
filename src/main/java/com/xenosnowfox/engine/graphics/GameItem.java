package com.xenosnowfox.engine.graphics;

import com.xenosnowfox.engine.components.Transformation3f;
import org.joml.Vector3f;

public class GameItem implements Transformation3f {

    private final Mesh mesh;
    
    private final Vector3f position;
    
    private final Vector3f scale;

    private final Vector3f rotation;

    public GameItem(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3f();
        scale = new Vector3f(1.0f, 1.0f, 1.0f);
        rotation = new Vector3f();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }
    
    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public void setPosition(final Vector3f withVector) {
        this.position.x = withVector.x;
        this.position.y = withVector.y;
        this.position.z = withVector.z;
    }

    @Override
    public void movePosition(final float offsetX, final float offsetY, final float offsetZ) {
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
    public void moveRotation(final float offsetX, final float offsetY, final float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
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