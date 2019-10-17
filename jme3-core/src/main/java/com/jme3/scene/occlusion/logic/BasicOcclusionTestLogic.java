package com.jme3.scene.occlusion.logic;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.occlusion.OcclusionLogic;
import com.jme3.scene.shape.Box;


public class BasicOcclusionTestLogic implements OcclusionLogic {
    protected int oldLod = -1;
    protected Box boundingBox = new Box(1f, 1f, 1f);
    protected Geometry testGeom = new Geometry("OcclusionGeom", boundingBox);
    protected boolean useLod=false;

    public BasicOcclusionTestLogic(){

    }

    public BasicOcclusionTestLogic(boolean useLodWhenAvailable){
        useLod=useLodWhenAvailable;
    }

    @Override
    public Geometry getOcclusionGeometry(RenderManager rm,Geometry geom) {
        if (useLod) {
            oldLod = -1;
            oldLod = testGeom.getLodLevel();
            testGeom.setMesh(geom.getMesh());
            testGeom.setLodLevel(geom.getMesh().getNumLodLevels() - 1);
            testGeom.setLocalTransform(geom.getWorldTransform());
        } else {
            testGeom.setMesh(boundingBox);
            BoundingBox bbox = (BoundingBox) geom.getWorldBound();
            testGeom.setLocalScale(bbox.getExtent(testGeom.getLocalScale()));
            testGeom.setLocalTranslation(bbox.getCenter(testGeom.getLocalTranslation()));
            testGeom.setLocalRotation(Quaternion.IDENTITY);
        }
        testGeom.setMaterial(geom.getMaterial());
        testGeom.updateGeometricState();
        return testGeom;
    }

    @Override
    public void cleanup(RenderManager rm) {
        if (useLod) testGeom.setLodLevel(oldLod);        
        testGeom.setMaterial(null);
        testGeom.setMesh(boundingBox);
    }

}