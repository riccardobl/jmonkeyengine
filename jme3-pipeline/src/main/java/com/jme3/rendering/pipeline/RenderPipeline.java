package com.jme3.rendering.pipeline;

import java.util.ArrayList;
import java.util.Collection;

import com.jme3.texture.FrameBuffer;

/**
 * RenderPipeline
 */
public class RenderPipeline {
    private ArrayList<PipelinePass> passes=new ArrayList<PipelinePass>();


    public int size(){
        return passes.size();
    }

    public PipelinePass get(int i){
        return passes.get(i);
    }
    
    public void add(PipelinePass pass){
        pass.setPipeline(this);
        pass.preAttach();
        passes.add(pass);
        pass.postAttach();
        recomputeIds();
    }

    public void add(int at,PipelinePass pass){
        pass.setPipeline(this);
        pass.preAttach();
        passes.add(at, pass);
        pass.postAttach();
        recomputeIds();
    }

    public void set(int at,PipelinePass pass){
        pass.setPipeline(this);
        pass.preAttach();
        passes.set(at, pass);
        pass.postAttach();
        recomputeIds();
    }


    void recomputeIds(){
        int i=0;
        for(PipelinePass pass:passes)pass.setId(i++);        
    }
    
    public void remove(PipelinePass pass){
        pass.preDetach();
        pass.cleanup();
        passes.remove(pass);
        pass.postDetach();
        recomputeIds();
    }   

        
    public void remove(int at){
        PipelinePass pass=passes.get(at);
        pass.preDetach();
        pass.cleanup();
        passes.remove(at);
        pass.postDetach();
        recomputeIds();
    }   

    public void initialize(){
        for(PipelinePass p:passes){
            p.initialize(this);        
        }     
    }

    public void cleanup(){
        for(PipelinePass p:passes){
            p.cleanup();        
        }     
    }

    public Collection<PipelinePass> getPasses(){
        return passes;
    }


}