package com.jme3.rendering.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.renderer.RenderManager;

/**
 * PipelineRunner
 */
public class PipelineRunner {

    protected List<RenderPipeline> pipelines=new ArrayList<RenderPipeline>();
    protected float speed;
    protected BiConsumer<RenderPipeline,Float> runnerAction;
    
    public PipelineRunner(){
        setRunner(null);
    }

    public void addPipeline(RenderPipeline p){
        pipelines.add(p);
    }

    public void removePipeline(RenderPipeline p){
        pipelines.remove(p);
    }

    public void setSpeed(float speed){
        this.speed=speed;
    }

    public void setRunner(BiConsumer<RenderPipeline,Float> action){
        if(action==null){
            action=(pipeline,tpf)->{
                for(PipelinePass p:pipeline.getPasses()){
                    p.run(tpf);
                }
            };
        }
        this.runnerAction=action;
    }

    public void run(float tpf){
        for(RenderPipeline p : pipelines){
            runnerAction.accept(p,tpf);
        }
    }
    
}