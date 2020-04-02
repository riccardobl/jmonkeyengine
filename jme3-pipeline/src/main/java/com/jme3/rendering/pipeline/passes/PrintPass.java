package com.jme3.rendering.pipeline.passes;

import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.params.smartobj.SmartObject;

/**
 * PrintPass
 */
public class PrintPass extends PipelinePass {
    Object obj;

    public PipelinePass inObj(Object o){
        useInput("Object",o);
        return this;
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

        System.out.println(obj);

    }

    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {
        obj=value;
    }

    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {
        // TODO Auto-generated method stub

    }

    
}