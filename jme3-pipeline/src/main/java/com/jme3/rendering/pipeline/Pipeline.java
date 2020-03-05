package com.jme3.rendering.pipeline;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A pipeline is a collection of PipelinePass that are run one after the other.
 * Every pass can access to the output of the previous passes and to a common storage.
 *
 *   @author  Riccardo Balbo
 */
public class Pipeline {
    private ArrayList<PipelinePass> passes=new ArrayList<PipelinePass>();

    /**
     *  Get the number of attached passes
     */
    public int size(){
        return passes.size();
    }

    /**
     * Get a pass from its id
     */
    public PipelinePass get(int i){
        return passes.get(i);
    }
    

    /** 
     * Add a pass at the end of the pipeline
     */
    public void add(PipelinePass pass){
        pass.preAttach(this);
        passes.add(pass);
        pass.postAttach(this);
        recomputeIds();
    }


    /**
     * Inserts the pass at the specified position of the pipeline. Subsequent elements are moved to the right
     *
     * @param at the position where the pass will be inserted
     * @param pass the pass to insert
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int at,PipelinePass pass){
        pass.preAttach(this);
        passes.add(at, pass);
        pass.postAttach(this);
        recomputeIds();
    }

    /**
     * Replace a pass at the specified position
     *
     * @param at the position of the pass that will be replaced
     * @param pass the pass to insert
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void set(int at,PipelinePass pass){
        pass.preAttach(this);
        passes.set(at, pass);
        pass.postAttach(this);
        recomputeIds();
    }


    /**
     * Remove a pass from the pipeline
     * @param pass Pass to remove
     */
    public void remove(PipelinePass pass){
        pass.preDetach(this);
        passes.remove(pass);
        pass.postDetach(this);
        recomputeIds();
    }   

        
    /**
     * Remove a pass from the pipeline
     * @param at index of the pass that must be removed inside the pipeline
     */
    public void remove(int at){
        PipelinePass pass=passes.get(at);
        pass.preDetach(this);
        passes.remove(at);
        pass.postDetach(this);
        recomputeIds();
    }   

    /**
     * Remove all passes and clear the pipeline
     */
    public void clear(){
        for(PipelinePass p:passes) remove(p);     
        passes.clear();        
    }

    /** 
     * Returns all passes
     * @return All the passes attached to this pipeline
     */
    public Collection<PipelinePass> getPasses(){
        return passes;
    }

    @Override
    protected void finalize() throws Throwable {
        clear();
        super.finalize();
    }

       protected void recomputeIds(){
        int i=0;
        for(PipelinePass pass:passes)pass.setId(i++);        
    }
    

}