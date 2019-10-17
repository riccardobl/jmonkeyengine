package com.jme3.scene.occlusion.logic;

import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.occlusion.OcclusionLogic;


public class SimpleGeometriesExclusionLogic implements OcclusionLogic {
    private static final int unshadedHashCode = "Unshaded".hashCode();
    @Override
    public Geometry getOcclusionGeometry(RenderManager rm, Geometry geom) {
        return (geom.getMaterial().getMaterialDef().getName().hashCode() == unshadedHashCode
                && geom.getMesh().getVertexCount() < 500 // todo: we need to check the current lod level, not the main
                                                         // mesh
        ) ? null : geom;
    }

}