package com.jme3.scene.occlusion.logic;

import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.occlusion.OcclusionLogic;

/**
 * UseOcclusionTechnique
 */
public class UseOcclusionTechnique implements OcclusionLogic {
    protected String oldTechnique;
    @Override
    public Geometry getOcclusionGeometry(RenderManager rm, Geometry g) {
        oldTechnique=rm.getForcedTechnique();
        rm.setForcedTechnique("OcclusionTest");
        return g;
    }

    @Override
    public void cleanup(RenderManager rm) {
        rm.setForcedTechnique(oldTechnique);
        oldTechnique=null;
    }
    
}