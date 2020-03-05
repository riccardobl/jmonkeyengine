package com.jme3.rendering.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.renderer.RenderManager;

/**
 * Runs the pipeline
 * @author Riccardo Balbo
 */
public class PipelineRunner {

    protected List<Pipeline> pipelines=new ArrayList<Pipeline>();
    protected float speed;
    protected BiConsumer<Pipeline,Float> runnerAction;
    

    public void run(float tpf){
        for(Pipeline p : pipelines){
            runnerAction.accept(p,tpf);
        }
    }
    
    public PipelineRunner(){
        setRunner(null);
    }

    public void addPipeline(Pipeline p){
        pipelines.add(p);
    }

    public void removePipeline(Pipeline p){
        pipelines.remove(p);
    }

    public void setSpeed(float speed){
        this.speed=speed;
    }

    public void setRunner(BiConsumer<Pipeline,Float> action){
        if(action==null){
            action=(pipeline,tpf)->{
                for(PipelinePass p:pipeline.getPasses()){
                    
                    p.run(pipeline,tpf);
                }
            };
        }
        this.runnerAction=action;
    }


    
}