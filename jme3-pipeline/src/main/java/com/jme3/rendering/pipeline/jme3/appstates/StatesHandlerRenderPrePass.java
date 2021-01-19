package com.jme3.rendering.pipeline.jme3.appstates;

import com.jme3.app.state.AppStateManager;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.jme3.context.Jme3ContextCreator;

public class StatesHandlerRenderPrePass extends PipelinePass<StatesHandlerPass> {
    private final AppStateManager stateManager;
    private final Jme3ContextCreator contextFactory;

    public StatesHandlerRenderPrePass(AppStateManager stateManager,Jme3ContextCreator contextFactory) {
        this.stateManager = stateManager;
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
        stateManager.render(contextFactory.getContext().getRenderManager());
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