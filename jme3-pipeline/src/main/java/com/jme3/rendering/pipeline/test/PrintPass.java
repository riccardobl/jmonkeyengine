package com.jme3.rendering.pipeline.test;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.RenderPipeline;
import com.jme3.rendering.pipeline.params.PipelineParam;
import com.jme3.rendering.pipeline.params.PipelineParamPointer;

/**
 * PrintPass
 */
public class PrintPass extends PipelinePass{
    PipelineParam value;
    String desc;

    public PrintPass value(PipelineParamPointer val){
        value=val.get(this);
        return this;
    }

    public PrintPass desc(String d){
        desc=d;
        return this;
    }


    @Override
    public void run(float tpf) {
        System.out.println(desc+" " +value.getValue());

    }

}