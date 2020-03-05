package com.jme3.rendering.pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jme3.rendering.pipeline.params.smartobj.SmartObject;

/**
 * A self contained pass of the pipeline. 
 * 
 * @author Riccardo Balbo
 */
public abstract class PipelinePass{
    private int id;
    private Map<Object,Object> inputs=new HashMap<Object,Object>();
    private Map<Object,Object> outputs=new HashMap<Object,Object>();

    void setId(int id) {
        this.id=id;
    }

    /**
     * Get id of the current pass inside the pipeline
     */
    public int getId() {
        return this.id;
    }

    /**
     * Called before the pass is attached to the pipeline
     */
    protected void preAttach(Pipeline pipeline) {

    }

    /**
     * Called after the pass is attached to the pipeline
     */
    protected void postAttach(Pipeline pipeline) {

    }

    /**
     * Called before the pass is detached from the pipeline
     */
    protected void preDetach(Pipeline pipeline) {

    }

    /**
     * Called after the pass is detached from the pipeline
     */
    protected void postDetach(Pipeline pipeline) {

    }


    /**
     * Pass an input to the pass.
     * Note: inputs should not be manipulated directly, since they could be actually transparent pointers. 
     * All the inputs will be processed and passed to the onInput method on every frame. From there they can be used freely..
     * @param key An object that defines the name of this input
     * @param in the value
     */
    protected void useInput(Object key, Object in) {
        if(in == null){
            inputs.remove(key);
        }else{
            inputs.put(key,in);
        }
    }


     /**
    * Define an output for the pass
     * Note: outputs should not be manipulated directly, since they could be actually transparent pointers. 
     * All the outputs will be processed and passed to the onOutput method on every frame. From there they can be used freely.
     * @param key An object that defines the name of this output
     * @param out the output object
     */
    protected void useOutput(Object key, Object out) {
        if(out == null){
            outputs.remove(key);
        }else{
            outputs.put(key,out);
        }
    }

    protected void setInOut(Pipeline pipeline) {
        for(Entry<Object,Object> e:inputs.entrySet()){
            Object key=e.getKey();
            Object value=e.getValue();
            SmartObject svalue=SmartObject.from(value);
            value=svalue.get(pipeline,this);
            onInput(pipeline,key,value);            
        }
        for(Entry<Object,Object> e:outputs.entrySet()){
            Object key=e.getKey();
            Object value=e.getValue();
            SmartObject svalue=SmartObject.from(value);
            value=svalue.get(pipeline,this);
            onOutput(pipeline,key,value);            
        }
    }

    /**
     * Called before inputs and ouputs are processed in every frame
     */
    protected void beforeInOutSet(Pipeline pipeline){

    }


    /**
     * Called after inputs and ouputs  are processed in every frame
     */
    protected void afterInOutSet(Pipeline pipeline){

    }

    protected abstract void onRun(Pipeline pipeline,float tpf);


    /**
     * Called for each input, after the input is processed
     * @param pipeline The pipeline
     * @param key The name of the input
     * @param value The value of the input
     */
    protected abstract void onInput(Pipeline pipeline,Object key,Object value);


    

    /**
     * Called for each output, after the output is processed
     * @param pipeline The pipeline
     * @param key The name of the output
     * @param value The value of the output
     */
    protected abstract void onOutput(Pipeline pipeline,Object key,Object value);

    public final void run(Pipeline pipeline,float tpf){
        beforeInOutSet(pipeline);
        setInOut(pipeline);
        afterInOutSet(pipeline);
        onRun(pipeline,tpf);
    }

}