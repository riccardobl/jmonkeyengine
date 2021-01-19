package com.jme3.rendering.pipeline.passes;

import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.scene.Spatial;

/**
 * UpdateGeometryPass
 */
public class UpdateGeometryPass extends PipelinePass {

    private Spatial rootSpatial;

    public UpdateGeometryPass(Spatial rootSpatial){
        this.rootSpatial=rootSpatial;
    }

    
    @Override
    protected void onRun(Pipeline pipeline, float tpf) {
        rootSpatial.updateGeometricState();
    }


    @Override
    protected void preAttach(Pipeline pipeline) {

    }

    @Override
    protected void postAttach(Pipeline pipeline) {

    }

    @Override
    protected void preDetach(Pipeline pipeline) {

    }

    @Override
    protected void postDetach(Pipeline pipeline) {

    }

    @Override
    protected void beforeIO(Pipeline pipeline) {

    }

    @Override
    protected void afterIO(Pipeline pipeline) {

    }

    @Override
    protected void beforeRun(Pipeline pipeline, float tpf) {

    }

    @Override
    protected void afterRun(Pipeline pipeline, float tpf) {

    }

   
    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {

    }

    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {

    }

    
}