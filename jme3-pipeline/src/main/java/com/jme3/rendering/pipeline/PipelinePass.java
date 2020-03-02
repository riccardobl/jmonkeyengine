package com.jme3.rendering.pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jme3.rendering.pipeline.params.texture.SmartObject;

public abstract class PipelinePass{
    protected int id;
    protected RenderPipeline pipeline;

    public void initialize(RenderPipeline pipeline) {
    }

    public RenderPipeline getPipeline(){
        assert pipeline!=null;
        return pipeline;
    }

    protected void setPipeline(RenderPipeline pipeline){
        this.pipeline=pipeline;

    }
    public void cleanup() {

    }

    public void setId(int id) {
        this.id=id;
    }

    public int getId() {
        return this.id;
    }

    public void preAttach() {

    }

    public void postAttach() {

    }

    public void preDetach() {

    }

    public void postDetach() {

    }

    Map<Object,Object> inputs=new HashMap<Object,Object>();
    Map<Object,Object> outputs=new HashMap<Object,Object>();

    protected void useInput(Object key, Object in) {
        if(in == null){
            inputs.remove(key);
        }else{
            inputs.put(key,in);
        }
    }

    protected void useOutput(Object key, Object out) {
        if(out == null){
            outputs.remove(key);
        }else{
            outputs.put(key,out);
        }
    }

    protected void setInOut() {
        for(Entry<Object,Object> e:inputs.entrySet()){
            Object key=e.getKey();
            Object value=e.getValue();
            SmartObject svalue=SmartObject.from(value);
            value=svalue.get(this);
            onInput(key,value);            
        }
        for(Entry<Object,Object> e:outputs.entrySet()){
            Object key=e.getKey();
            Object value=e.getValue();
            SmartObject svalue=SmartObject.from(value);
            value=svalue.get(this);
            System.out.println(this+ " Use output "+value);

            onOutput(key,value);            
        }
    }

 
    protected void beforeInOutSet(){

    }
    protected void afterInOutSet(){

    }

    public abstract void onRun(float tpf);
    public abstract void onInput(Object key,Object value);
    public abstract void onOutput(Object key,Object value);

    public final void run(float tpf){
        assert pipeline!=null;
        beforeInOutSet();
        setInOut();
        afterInOutSet();
        onRun(tpf);
    }

}