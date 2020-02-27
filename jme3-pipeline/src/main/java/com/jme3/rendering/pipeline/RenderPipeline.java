package com.jme3.rendering.pipeline;

import java.util.ArrayList;
import java.util.Collection;

import com.jme3.texture.FrameBuffer;

/**
 * RenderPipeline
 */
public class RenderPipeline {
    private ArrayList<PipelinePass> passes=new ArrayList<PipelinePass>();


    
    
    public void add(PipelinePass pass){
        pass.preAttach();
        passes.add(pass);
        pass.postAttach();
        recomputeIds();
    }

    public void add(int at,PipelinePass pass){
        pass.preAttach();
        passes.add(at, pass);
        pass.postAttach();
        recomputeIds();
    }

    public void set(int at,PipelinePass pass){
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

    public void run(float tpf){
        for(PipelinePass p:passes){
            p.preRun(tpf);        
        }        
        for(PipelinePass p:passes){
            p.run(tpf);        
        }
        for(PipelinePass p:passes){
            p.postRun(tpf);
        }        
    }

}