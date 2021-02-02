package com.jme3.rendering.pipeline.logic;

import com.jme3.renderer.Camera;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class CullPass extends PipelinePass {
    protected final Spatial scene;
    private final Camera cam;

    public CullPass(Camera cam,Spatial scene){
        
        this.scene=scene;
        this.cam=cam;
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

    protected void checkCull(Camera cam,Spatial sp,boolean parentState){
        System.out.println(" "+sp.checkCulling(cam));
        CullState cullState=sp.getState(CullState.class, CullState::new);
        CullState camState=cam.getState(CullState.class, CullState::new);
        if(!cullState.isStateUpdateNeeded()&&camState.getStateId()==cullState.cameraStateId) return;
        cullState.culled=parentState||!sp.checkCulling(cam);
        camState.clearStateUpdateNeeded();
        cullState.clearStateUpdateNeeded();
        cullState.cameraStateId=camState.getStateId();
        if(sp instanceof Node){
            int cps = cam.getPlaneState();
            Node n=(Node)sp;
            for(Spatial child:n.getChildren()){
                cam.setPlaneState(cps);
                checkCull(cam,child,cullState.culled);
            }
        }
    }

    @Override
    protected void onRun(Pipeline pipeline, float tpf) {
        checkCull(cam,scene,false);
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
