package com.jme3.rendering.pipeline.logic;

import java.util.Collection;

import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.control.LogicControl;
import com.jme3.util.functional.Function;
import com.jme3.util.functional.VoidBiFunction;
import com.jme3.util.functional.VoidFunction;

/**
 * RunGameLogicPass
 */
public class LogicExecutorPass extends PipelinePass {


    private final Collection<Control> queue;

    public LogicExecutorPass(Collection<Control>  queue){
        this.queue=queue;
    }



    @Override
    protected void onRun(Pipeline pipeline, float tpf) {
        for(Control c:queue){
            if(c instanceof LogicControl){
                ((LogicControl)c).onLogicUpdate(c.getSpatial(), tpf);
            }
        }
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
        // TODO Auto-generated method stub

    }

 

    
}