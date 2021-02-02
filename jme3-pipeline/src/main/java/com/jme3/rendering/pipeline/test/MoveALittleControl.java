package com.jme3.rendering.pipeline.test;

import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.LogicControl;
import com.jme3.scene.control.SpatialControl;

public class MoveALittleControl extends SpatialControl implements LogicControl{

    private float time=0;

    @Override
    public void onLogicUpdate(Spatial spatial,float tpf) {
        time+=tpf*10;
        spatial.getLocalTransform().getTranslation().addLocal(FastMath.sin(time),FastMath.sin(time),0);

        getSpatial().setLocalTransform(
            spatial.getLocalTransform()
        );
       
    }


    
}
