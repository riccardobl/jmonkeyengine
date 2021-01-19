package com.jme3.rendering.pipeline.passes;

import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.scene.Spatial;
import com.jme3.system.Timer;

/**
 * UpdateGeometryPass
 */
public class UpdateTimerPass extends PipelinePass {

    private Timer timer;

    public UpdateTimerPass(Timer timer){
        this.timer=timer;
    }

    
    @Override
    protected void onRun(Pipeline pipeline, float tpf) {
        timer.update();
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