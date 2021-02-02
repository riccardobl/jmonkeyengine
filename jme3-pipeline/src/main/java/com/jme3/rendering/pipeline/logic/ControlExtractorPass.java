package com.jme3.rendering.pipeline.logic;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.util.functional.Function;

/**
 * RunGameLogicPass
 */
public class ControlExtractorPass extends PipelinePass {

    protected Spatial scene;
    protected Function<Boolean,Control> filter;
    protected Collection<Control> queue;
    protected boolean needUpdate=true;

  

    public ControlExtractorPass(Node inScene,Collection< Control> outQueue,Function<Boolean,  Control>  filter){
        this.scene=inScene;
        this.queue=outQueue;
        this.filter=filter;
    }


    
    protected void rebuildQueues(Spatial sp,Collection< Control> queue,Function< Boolean, Control> filter){
        for(int i=0;i<sp.getNumControls();i++){
            Control c=sp.getControl(i);
            if(filter.eval(c)) {
                queue.add(c);
            }
        }
        if(sp instanceof Node){
            Node n=(Node)sp;
            for(Spatial child:n.getChildren()){
                rebuildQueues(child,queue,filter);
            }
        }
    }

    @Override
    protected void onRun(Pipeline pipeline, float tpf) {
        if(scene==null||queue==null)return;        
        LogicQueueState queueState=scene.getState(this,  LogicQueueState::new);
        if(queueState.isStateUpdateNeeded()||needUpdate){
            System.out.println("rebuild");
            queue.clear();
            rebuildQueues(scene,queue,filter);
            queueState.clearStateUpdateNeeded();
            needUpdate=false;
        }
        // queueState.queue.forEach(c->c.update(tpf));
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
        // TODO Auto-generated method stub

    }

    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {
        // TODO Auto-generated method stub

    }




    
}