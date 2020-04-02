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
    private  static final java.util.logging.Logger logger =  java.util.logging.Logger.getLogger( PipelineRunner.class.getName());
    protected List<Pipeline> pipelines=new ArrayList<Pipeline>();
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
        if(!pipelines.contains(p))pipelines.add(p);
    }

    public void removePipeline(Pipeline p){
        pipelines.remove(p);
    }

   

    public void setRunner(BiConsumer<Pipeline,Float> action){
        if(action==null){

            action=(pipeline,tpf)->{
                if(logger.isLoggable(java.util.logging.Level.  FINER  ))logger.log(java.util.logging.Level.FINER,
                    "Run pipeline {0} with runner {1}",new Object[]{pipeline,this}
                );

                for(PipelinePass p:pipeline.getPasses()){
                    if(logger.isLoggable(java.util.logging.Level.  FINER  ))logger.log(java.util.logging.Level.FINER,
                        "Run {0}",p
                    );
                    p.run(pipeline,tpf);
                }
            };
        }
        this.runnerAction=action;
    }


    
}