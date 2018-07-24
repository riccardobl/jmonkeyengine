package com.jme3.scene.instancing;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

/**
 * Instanceable
 */
public abstract class InstanceableGeometry extends Geometry {
	public abstract int getActualNumInstances();

	public abstract VertexBuffer[] getAllInstanceData();
	
    public InstanceableGeometry() {
        super();
    }


    public InstanceableGeometry(String name) {
        super(name);
    }

    public InstanceableGeometry(String name, Mesh mesh) {
        super(name,mesh);
    }

}