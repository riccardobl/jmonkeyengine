package com.jme3.rendering.pipeline.jme3.controls;

import java.util.Collection;

import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.jme3.context.Jme3ContextCreator;
import com.jme3.scene.control.Control;

public class LegacyControlExecutorPass extends PipelinePass{
    private final Collection<Control> controls;
    private final Jme3ContextCreator contextFactory;
    public LegacyControlExecutorPass(Jme3ContextCreator contextFactory,Collection<Control> controls){
        this.controls=controls;
        this.contextFactory=contextFactory;
    }
    @Override
    protected void preAttach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void postAttach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void preDetach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void postDetach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeIO(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void afterIO(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeRun(Pipeline pipeline, float tpf) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void afterRun(Pipeline pipeline, float tpf) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onRun(Pipeline pipeline, float tpf) {
        for(Control c: this.controls){
            c.update(tpf);
        }
        // RenderManager renderManager=contextFactory.getContext().getRenderManager();
        // for(Control c: this.controls){            
        //     c.render(renderManager,);
        // }
    }

    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {
        // TODO Auto-generated method stub

    }
    
}
