package com.jme3.rendering.pipeline.jme3.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;

public class StatesHandlerPass extends PipelinePass<StatesHandlerPass> {
    private final AppStateManager stateManager;

    public StatesHandlerPass(Application app){
        this.stateManager=new AppStateManager(app);
    }

    public AppStateManager getStateManager(){
        return stateManager;
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
        stateManager.cleanup();
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
        stateManager.update(tpf);
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