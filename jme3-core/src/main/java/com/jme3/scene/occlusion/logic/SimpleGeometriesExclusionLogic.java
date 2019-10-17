package com.jme3.scene.occlusion.logic;

import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.occlusion.OcclusionLogic;


public class SimpleGeometriesExclusionLogic implements OcclusionLogic {

    private static final int matUnshaded = "Unshaded".hashCode();
    private static final int matSky = "Sky Plane".hashCode();
    private static final int matBillboard = "Billboard".hashCode();
    private static final int matFakeLighting = "FakeLighting".hashCode();
    private static final int matPointSprite = "Point Sprite".hashCode();
    
    @Override
    public Geometry getOcclusionGeometry(RenderManager rm, Geometry geom) {
        int mat=geom.getMaterial().getMaterialDef().getName().hashCode();        
        
        if( mat==matSky||mat==matBillboard||mat==matFakeLighting||mat==matPointSprite)return null;

        if(mat == matUnshaded){
            int triangles=geom.getMesh().getTriangleCount(geom.getMesh().getNumLodLevels()-1);
            if(triangles<500)return null;
        }
   
        return geom;
    }

}