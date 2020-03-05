package com.jme3.rendering.pipeline.integration;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.PipelineRunner;

/**
 * A pipeline runner that is also an appstate. For quick integration in default application.
 * @author Riccardo Balbo
 */
public class PipelineRunnerAppState extends PipelineRunner implements AppState{

    static final Logger log=Logger.getLogger(BaseAppState.class.getName());

    private Application app;
    private boolean initialized;
    private boolean enabled=true;
    private String id;

    public PipelineRunnerAppState(){
    }

    public PipelineRunnerAppState(String id){
        this.id=id;
    }

    protected void initialize(Application app) {

    }

    protected void cleanup(Application app) {

    }

    protected void onEnable() {

    }

    protected void onDisable() {

    }

    @Override
    public final void initialize(AppStateManager stateManager, Application app) {
        log.log(Level.FINEST,"initialize():{0}",this);

        this.app=app;
        initialized=true;
        initialize(app);
        if(isEnabled()){
            log.log(Level.FINEST,"onEnable():{0}",this);
            onEnable();
        }
    }

    @Override
    public final boolean isInitialized() {
        return initialized;
    }

    protected void setId(String id) {
        this.id=id;
    }

    @Override
    public String getId() {
        return id;
    }

    public final Application getApplication() {
        return app;
    }

    public final AppStateManager getStateManager() {
        return app.getStateManager();
    }

    @Override
    public final void setEnabled(boolean enabled) {
        if(this.enabled == enabled) return;
        this.enabled=enabled;
        if(!isInitialized()) return;
        if(enabled){
            log.log(Level.FINEST,"onEnable():{0}",this);
            onEnable();
        }else{
            log.log(Level.FINEST,"onDisable():{0}",this);
            onDisable();
        }
    }

    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void render(RenderManager rm) {
        float tpf=getApplication().getTimer().getTimePerFrame();
        run(tpf);
    }

    @Override
    public void postRender() {
    }

    @Override
    public final void cleanup() {
        log.log(Level.FINEST,"cleanup():{0}",this);

        if(isEnabled()){
            log.log(Level.FINEST,"onDisable():{0}",this);
            onDisable();
        }
        cleanup(app);
        initialized=false;
    }

}