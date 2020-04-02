package com.jme3.rendering.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.rendering.pipeline.params.primitives.MutablePrimitive;
import com.jme3.rendering.pipeline.params.smartobj.SmartObject;

/**
 * A self contained pass of the pipeline. 
 * 
 * @author Riccardo Balbo
 */
public abstract class PipelinePass<T extends PipelinePass> {
    private  static final java.util.logging.Logger logger =  java.util.logging.Logger.getLogger( PipelinePass.class.getName());
    private int id;
    private String name;
    private Map<Object,Object> inputs=new HashMap<Object,Object>();
    private Map<Object,Object> outputs=new HashMap<Object,Object>();

    private Map<Object,Object> defInputs=new HashMap<Object,Object>();
    private Map<Object,Object> defOutputs=new HashMap<Object,Object>();

    private final Map<Object,Object> tmpInputs=new HashMap<Object,Object>();
    private final Map<Object,Object> tmpOutputs=new HashMap<Object,Object>();

    void setId(final int id) {
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
    protected abstract void preAttach(Pipeline pipeline) ;

    /**
     * Called after the pass is attached to the pipeline
     */
    protected abstract void postAttach(Pipeline pipeline) ;

    /**
     * Called before the pass is detached from the pipeline
     */
    protected abstract  void preDetach(Pipeline pipeline);
    

    /**
     * Called after the pass is detached from the pipeline
     */
    protected abstract void postDetach(Pipeline pipeline) ;

    /**
     * Pass an input to the pass.
     * Note: inputs should not be manipulated directly, since they could be actually transparent pointers. 
     * All the inputs will be processed and passed to the onInput method on every frame. From there they can be used freely..
     * @param key An object that defines the name of this input
     * @param in the value
     */
    public Object useInput(final Object key, final Object in) {
     
        if (logger.isLoggable(java.util.logging.Level.FINEST))
            logger.log(java.util.logging.Level.FINEST, " Use input  {0}={1} for {2}", new Object[] { key, in, this });

        Object ret;
        if(in == null){
            ret=inputs.remove(key);
        }else{
            ret= inputs.put(key,in);
        }

        if(settingIOFor!=null){
            setIO(settingIOFor, key, in, false,false);
        }
        return ret;
      
    }

    public Object useDefaultInput(String key, Object in) {
        if (logger.isLoggable(java.util.logging.Level.FINEST))
            logger.log(java.util.logging.Level.FINEST, " Use  default input  {0}={1} for {2}",
                    new Object[] { key, in, this });
                    
        if (in == null) {
            return defInputs.remove(key);
        } else {
            return defInputs.put(key, in);
        }
    }

    public Object useDefaultOutput(String key, Object out) {
        if (logger.isLoggable(java.util.logging.Level.FINEST))
            logger.log(java.util.logging.Level.FINEST, " Use  default output  {0}={1} for {2}",
                    new Object[] { key, out, this });
        if (out == null) {
            return defOutputs.remove(key);
        } else {
            return defOutputs.put(key, out);
        }
    }

    public Object getInput(Object key) {
        return inputs.get(key);
    }

    public Object getDefaultInput(Object key) {
        return defInputs.get(key);
    }

    public Object getDefaultOutput(Object key) {
        return defOutputs.get(key);
    }

     /**
    * Define an output for the pass
     * Note: outputs should not be manipulated directly, since they could be actually transparent pointers. 
     * All the outputs will be processed and passed to the onOutput method on every frame. From there they can be used freely.
     * @param key An object that defines the name of this output
     * @param out the output object
     */
    public Object useOutput(final Object key, final Object out) {

        if (logger.isLoggable(java.util.logging.Level.FINEST))
            logger.log(java.util.logging.Level.FINEST, " Use output  {0}={1} for {2}", new Object[] { key, out, this });

        Object ret;
        if(out == null){
            ret= outputs.remove(key);
        }else{
            ret= outputs.put(key,out);
        }
        if(settingIOFor!=null){
            setIO(settingIOFor, key, out, true,false);
        }
        return ret;
    }

    // public void clearInputs(){
    //     inputs.clear();
    // }

    // public void clearOutputs(){
    //     outputs.clear();
    // }

    Object updatedInputKeys[]=new Object[0];
    int updatedInputKeysSize=0;
    Object updatedOutputKeys[]=new Object[0];
    int updatedOutputKeysSize=0;
    Pipeline         settingIOFor=null;
    
    protected void setIO(final Pipeline pipeline) {
        // final Map<Object,Object>  oinputs=inputs;
        // final Map<Object,Object>  ooutputs=outputs;

        beforeIO(pipeline);
        settingIOFor=pipeline;
        // long iterations=0;
        // while(true){
          

        //     updatedInputKeys=inputs.keySet().toArray(updatedInputKeys);
        //     updatedInputKeysSize=inputs.size();
            
        //     updatedOutputKeys=outputs.keySet().toArray(updatedOutputKeys);
        //     updatedOutputKeysSize=outputs.size();
   


        //     if(updatedInputKeysSize==0&&updatedOutputKeysSize==0)break;

        //     iterations++;
        //     assert iterations<100:getClass()+" Too many iterations "+Arrays.deepToString(updatedInputKeys)+" "+Arrays.deepToString(updatedOutputKeys);

        //     if(inputs==tmpInputs)  oinputs.putAll(tmpInputs);           
        //     if(outputs==tmpOutputs)  ooutputs.putAll(tmpOutputs);
            
        //     tmpInputs.clear();
        //     tmpOutputs.clear();

        //     inputs=tmpInputs;
        //     outputs=tmpOutputs;        

        //     for(int i=0;i<updatedInputKeysSize;i++){
        //         final Object key=updatedInputKeys[i];
        //         Object value=oinputs.get(key);
        //         final SmartObject svalue=SmartObject.from(value);
        //         value=svalue.get(pipeline,this);
        //         onInput(pipeline,key,value);            
        //     }
        //     for(int i=0;i<updatedOutputKeysSize;i++){
        //         final Object key=updatedOutputKeys[i];
        //         Object value=ooutputs.get(key);
        //         final SmartObject svalue=SmartObject.from(value);
        //         value=svalue.get(pipeline,this);
        //         onOutput(pipeline,key,value);            
        //     }

        // }
        updatedInputKeys=inputs.keySet().toArray(updatedInputKeys);
        updatedInputKeysSize=inputs.size();
        
        updatedOutputKeys=outputs.keySet().toArray(updatedOutputKeys);
        updatedOutputKeysSize=outputs.size();


        for(int i=0;i<updatedInputKeysSize;i++){
            final Object key=updatedInputKeys[i];
            Object value=inputs.get(key);
            setIO(pipeline,key,value,false,false);  
        }

        for(int i=0;i<updatedOutputKeysSize;i++){
            final Object key=updatedOutputKeys[i];
            Object value=outputs.get(key);
            setIO(pipeline,key,value,true,false);  
        }

    
        updatedInputKeys=defInputs.keySet().toArray(updatedInputKeys);
        updatedInputKeysSize=defInputs.size();
        
        updatedOutputKeys=defOutputs.keySet().toArray(updatedOutputKeys);
        updatedOutputKeysSize=defOutputs.size();

        for(int i=0;i<updatedInputKeysSize;i++){
            Object key=updatedInputKeys[i];
            Object value=defInputs.get(key);
            setIO(pipeline,key,value,false,true);  
        }

        for(int i=0;i<updatedOutputKeysSize;i++){
            Object key=updatedOutputKeys[i];
            Object value=defOutputs.get(key);
            setIO(pipeline,key,value,true,true);  
        }

        afterIO(pipeline);
        settingIOFor=null;


 
    
    
        // inputs=oinputs;
        // outputs=ooutputs;        



    }

    /**
     * Called before inputs and ouputs are processed in every frame
     */
    protected abstract void beforeIO(Pipeline pipeline);


    /**
     * Called after inputs and ouputs  are processed in every frame
     */
    protected abstract void afterIO(Pipeline pipeline);


    protected abstract void beforeRun(Pipeline pipeline,float tpf);
    protected abstract void afterRun(Pipeline pipeline,float tpf);
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


    private float speed=1f;

    public T speed(final float v){
        this.speed=v;
        return (T)this;
    }

    protected float getSpeed(){
        return speed;
    }


    protected void proxyInputOutput(Object in, Object out) {
        if (out instanceof MutablePrimitive) {
            MutablePrimitive mvalue = (MutablePrimitive) out;
            mvalue.setValue(in);
        } else {
            if (out instanceof Vector2f) {
                Vector2f mvalue = (Vector2f) out;
                mvalue.set((Vector2f) in);
            } else if (out instanceof Vector3f) {
                Vector3f mvalue = (Vector3f) out;
                mvalue.set((Vector3f) in);
            } else if (out instanceof Vector4f) {
                Vector4f mvalue = (Vector4f) out;
                mvalue.set((Vector4f) in);
            } else if (out instanceof Matrix3f) {
                Matrix3f mvalue = (Matrix3f) out;
                mvalue.set((Matrix3f) in);
            } else if (out instanceof Matrix4f) {
                Matrix4f mvalue = (Matrix4f) out;
                mvalue.set((Matrix4f) in);
            }
        }

    }

    protected void proxyInputOutputs() {
        for (Entry<Object, Object> e : outputs.entrySet()) {
            Object in = inputs.get(e.getKey());
            Object out = e.getValue();
            if (in != null) {
                proxyInputOutput(in, out);
            }
        }
    }


    protected void setIO(Pipeline pipeline,Object key,Object value,boolean out,boolean def){
        assert value!=null:key+" is  null";
        if(def&&inputs.containsKey(key))return;
        final SmartObject svalue=SmartObject.from(value);
        value=svalue.get(pipeline,this);
        if(out)     onOutput(pipeline,key,value);            
        else   onInput(pipeline,key,value);           
    }

    public final void run(final Pipeline pipeline,float tpf){
        tpf*=speed;
        // setIO(pipeline);


        // beforeIO(pipeline);

          

        setIO(pipeline);

        // afterIO(pipeline);


        // inputs=oinputs;
        // outputs=ooutputs;        

        beforeRun(pipeline,tpf);
        onRun(pipeline,tpf);
        proxyInputOutputs();
        afterRun(pipeline,tpf);

    
    }


    public String getName(){
        return this.name==null?this.getClass().getSimpleName():this.name;
    }

    public T  name(final String name){
        this.name=name;
        return (T)this;
    }


    private final String mapToString(final Map... maps){
        StringBuilder sb = new StringBuilder();
        for(final Map<Object,Object> m:maps){            
            for( Entry<Object, Object> e : m.entrySet()) {
                sb.append(e.getKey()).append("=");
                Object v=e.getValue() ;
                if(v instanceof Map){
                    sb.append("{\n");
                    sb.append(mapToString((Map)v));
                    sb.append("}\n");
                }else if(v.getClass().isArray()){
                    sb.append(Arrays.deepToString((Object[])v));
                }else{
                    sb.append(v.toString());
                }
                sb.append("\n       ");

            }
        }
        return sb.toString();
    }

    @Override
    public String toString(){
        return getName()+"@"+hashCode()+" [\n"+
        "    inputs:    [\n       "+mapToString(inputs,tmpInputs)+" \n    ]\n"+
        "    outputs: [\n       "+mapToString(outputs,tmpOutputs)+"\n    ] \n]"

        
        ;
    }




}