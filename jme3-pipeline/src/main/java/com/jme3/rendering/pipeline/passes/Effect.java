package com.jme3.rendering.pipeline.passes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.PipelinePointerFactory;
import com.jme3.rendering.pipeline.PipelinePointerResolver;
import com.jme3.rendering.pipeline.PipelineRunner;
import com.jme3.rendering.pipeline.params.primitives.MutableBoolean;
import com.jme3.rendering.pipeline.params.primitives.MutableNumber;
import com.jme3.rendering.pipeline.params.smartobj.SmartObject;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture;
import com.jme3.system.Timer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;


/**
 * An effect is a group of passes, a layer.
 */
public abstract class Effect extends PipelinePass {

    private  static final java.util.logging.Logger logger =  java.util.logging.Logger.getLogger( Effect.class.getName());


    // private final static Object DEFAULT=new Object();

    private final PipelinePointerFactory localPointers;
    private final  Pipeline localPipeline;
    private final  PipelineRunner localRunner;
    private final  FrameBufferFactory localFbFactory;
    

    // private Map<Object,List<Object>> effectOutputs=new HashMap<Object,List<Object>>(); // key, pass (if pass==DEFAULT, last out)
    // private Map<Object,List<Object>> effectInputs=new HashMap<Object,List<Object>>(); // key, pass (if pass==DEFAULT, all in)



    protected Effect(Pipeline pipeline,PipelineRunner runner,FrameBufferFactory fbFactory,PipelinePointerFactory pointerRes){
        localPointers=pointerRes==null?new PipelinePointerFactory():pointerRes;
        localRunner=runner==null?new PipelineRunner():runner;
        localFbFactory=fbFactory==null?new FrameBufferFactory():fbFactory;
        localPipeline=pipeline==null?new Pipeline(localPointers):pipeline;        
        localRunner.addPipeline(localPipeline);

    }

    protected Effect(){
        this(null,null,null,null);
    }

    protected Effect(FrameBufferFactory fbFactory){
        this(null,null,fbFactory,null);
    }

    protected Pipeline getEffectPipeline(){
        return localPipeline;
    }

    protected PipelinePointerFactory getEffectPointerFactory(){
        return localPointers;
    }

    protected FrameBufferFactory getEffectFrameBufferFactory(){
        return localFbFactory;
    }

    
    @Override
    protected void onRun(Pipeline pipeline, float tpf) {
        localRunner.run(tpf);
    }


   
    // private void unmarkEffectValueForPass(Map<Object,List<Object>> map,Object key,Object pass){
    //         List<Object> passesRefs=map.get(key);
    //         if(passesRefs!=null){
    //             passesRefs.remove(pass);
    //             if(passesRefs.size()==0){
    //                 map.remove(key);
    //             }
    //         }
       
    // }


    // private void markEffectValueForPass(Map<Object,List<Object>> map,Object key,Object pass){
  
    //         List<Object> passesRefs=map.get(key);
    //         if(passesRefs==null){
    //             map.put(key,passesRefs=new ArrayList<Object>());
    //         }
    //         passesRefs.add(pass);
        
    // }

    // /**
    //  * Input to every pass
    //  * @param key
    //  */

    //  @Override
    // public Object useInput(Object key,Object value){
    //     if(value==null){
    //         unmarkEffectValueForPass(effectInputs,key,DEFAULT);
    //         useInputOnPass(DEFAULT,key,value);
    //     }else{
    //         markEffectValueForPass(effectInputs,key,DEFAULT);
    //     } 
    //     return super.useInput(key, value);
    // }

    // /**
    //  * Input to id
    //  * @param id
    //  * @param key
    //  */
    
    // public Object useInput(int id,Object key,Object value){
    //     if(id<0)id=0;
    //     if(value==null){
    //         unmarkEffectValueForPass(effectInputs,key,id);
    //         useInputOnPass(id,key,value);
    //     }else{
    //         markEffectValueForPass(effectInputs,key,id);
    //     }    
    //     return super.useInput(key, value);
    // }

    // /**
    //  * Input to pass
    //  */
    // public Object useInput(PipelinePass pass,Object key,Object value){
    //     if(value==null){
    //         unmarkEffectValueForPass(effectInputs,key,pass);
    //         useInputOnPass(pass,key,value);
    //     }else{
    //         markEffectValueForPass(effectInputs,key,pass);
    //     }    
    //     return super.useInput(key, value);

    // }

 
    // @Override
    // public Object useOutput(Object key,Object value){
    //     if(value==null){
    //         unmarkEffectValueForPass(effectOutputs,key,DEFAULT);
    //         useOutputOnPass(DEFAULT,key,value);            
    //     }else{
    //         markEffectValueForPass(effectOutputs,key,DEFAULT);
    //     } 
    //     return super.useOutput(key,value);
    // }

    // /**
    //  * Outout from id
    //  * @param id
    //  * @param key
    //  */

    // public Object useOutput(int id,Object key,Object value){
    //     if(id<0)id=0;
    //     if(value==null){
    //         unmarkEffectValueForPass(effectOutputs,key,id);
    //         useOutputOnPass(id,key,value);
    //     }else{
    //         markEffectValueForPass(effectOutputs,key,id);
    //     } 
    //     return super.useOutput(key,value);
    // }

    // /**
    //  * Output from pass
    //  */
  
    // public Object useOutput(PipelinePass pass,Object key,Object value){
    //     if(value==null){
    //         unmarkEffectValueForPass(effectOutputs,key,pass);
    //         useOutputOnPass(pass,key,value);
    //     }else{
    //         markEffectValueForPass(effectOutputs,key,pass);
    //     } 
    //     return super.useOutput(key,value);
    // }

    // @Override
    // public void clearInputs(){
    //    super.clearInputs();
    //    effectInputs.clear();
    // }

    // @Override
    // public void clearOutputs(){
    //   super.clearOutputs();
    //   effectOutputs.clear();
    // }


    // private void useInputOnPass(Object proxyTo,Object key,Object value){

    //     if(logger.isLoggable(java.util.logging.Level.  FINER  ))logger.log(java.util.logging.Level.FINER,
    //         "Set effect input {0}={1} on {2}",new Object[]{key,value,proxyTo}   
    //     );


    //     if(proxyTo instanceof Number){
    //         int in=((Number)proxyTo).intValue();
    //         if(in>=localPipeline.size())in=localPipeline.size()-1;
    //         localPipeline.get(in).useInput(key, value);
    //     }else if(proxyTo instanceof PipelinePass){
    //         PipelinePass pass=(PipelinePass)proxyTo;
    //         pass.useInput(key, value);            
    //     }else  {                
    //         for(PipelinePass pass:localPipeline.getPasses()){
    //             pass.useInput(key,value);
    //         }
    //     }
    // }

    // private void useOutputOnPass(Object proxyTo,Object key,Object value){
    
    //     if(logger.isLoggable(java.util.logging.Level.  FINER  ))logger.log(java.util.logging.Level.FINER,
    //         "Set effect output {0}={1} on {2}",new Object[]{key,value,proxyTo}   
    //     );

    //     if(proxyTo instanceof Number){
    //         int in=((Number)proxyTo).intValue();
    //         if(in>=localPipeline.size())in=localPipeline.size()-1;
    //         localPipeline.get(in).useOutput(key, value);
    //     }else if(proxyTo instanceof PipelinePass){
    //         PipelinePass pass=(PipelinePass)proxyTo;
    //         pass.useOutput(key, value);            
    //     }else  {                
    //         localPipeline.get(localPipeline.size()-1).useOutput(key,value);                
    //     }
    // }

    // @Override
    // protected void onInput(Pipeline pipeline, Object key, Object value) {
    //     List<Object> proxyTo=effectInputs.get(key);
    //     if(proxyTo!=null){
    //         for(Object p:proxyTo)useInputOnPass(p,key,value);
    //     }
    // }

    // @Override
    // protected void onOutput(Pipeline pipeline, Object key, Object value) {
    //     List<Object>  proxyTo=effectOutputs.get(key);
    //     if(proxyTo!=null){
    //         for(Object p:proxyTo)useOutputOnPass(p,key,value);
    //     }
    // }


  
}