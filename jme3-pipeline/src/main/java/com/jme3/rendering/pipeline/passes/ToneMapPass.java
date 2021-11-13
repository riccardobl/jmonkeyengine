package com.jme3.rendering.pipeline.passes;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.jme3.context.Jme3ContextCreator;
import com.jme3.rendering.pipeline.params.primitives.MutableNumber;
import com.jme3.rendering.pipeline.renderer.RenderOutput;
import com.jme3.rendering.pipeline.renderer.generic.RenderPass;
import com.jme3.shader.VarType;
import com.jme3.system.Timer;
import com.jme3.texture.Texture;

/**
 * ToneMap
 * @author Riccardo Balbo
 */
public class ToneMapPass extends Effect{
    public enum ToneMapMode{
        FILMIC,
        HABLE_FILMIC,
        LOTTES_2016
    }    

    private RenderPass<? extends RenderPass> renderer;
    private int inputI=0;
    private int outputI=0;
    private static class PassIn{
        private static int exposure=100;
        private static int scene=0;
    }
    
    public ToneMapPass(Jme3ContextCreator contextFactory,FrameBufferFactory fbFactory,AssetManager assetManager){
        super(fbFactory);
        Material mat=new Material(assetManager,"Pipeline/ToneMap/ToneMap.j3md");
        renderer=contextFactory.newSurfaceRenderPass(mat, fbFactory);
        getEffectPipeline().add(renderer);
      }
    


      public ToneMapPass inColor(Texture texture,Object exposure){
        if(inputI>=100)throw new RuntimeException("Too many input colors");
        useInput(PassIn.scene+inputI,texture);
        useInput(PassIn.exposure+inputI,exposure);
        inputI++; 
        return this;
    }


    public ToneMapPass outColor(Texture outScene){
        useOutput(RenderOutput.Color+outputI,outScene);       
        outputI++;
        return this;
    }
    

    public ToneMapPass mode(ToneMapMode v){
        useInput("Mode",v.ordinal());
        return this;
    }


    @Override
    public void beforeIO(Pipeline pipeline){
        renderer.resetOutColors();
        renderer.resetOutDepth();
        for(int i=0;;i++) {
            if(
                renderer.clearParam("Scene"+i)==null
                ||(renderer.clearParam("Exposure"+i)==null&&renderer.clearParam("ExposureTexture"+i)==null)
            )break; 
        } 
    }

    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {  
        if(key instanceof Number){
            int keyi=((Number)key).intValue();
            if(keyi>=PassIn.exposure){
                if(value instanceof Texture){
                    renderer.useParam(VarType.Texture2D,"ExposureTexture"+(keyi-PassIn.exposure),value);
                }else if(value instanceof Number){
                    renderer.useParam(VarType.Texture2D,"Exposure"+(keyi-PassIn.exposure),value);
                }else{
                    throw new RuntimeException("Unsupported type "+value.getClass());
                }
            }else if(keyi>=PassIn.scene){
                renderer.useParam(VarType.Texture2D,"Scene"+keyi,value);
            }
        }
        
    }

    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {
        if(key instanceof Number){
            int keyi=((Number)key).intValue();
            if(keyi>=RenderOutput.Color){
                renderer.outColor(keyi-RenderOutput.Color,(Texture)value);
            }
        }
    }

    @Override
    protected void preAttach(Pipeline pipeline) {
        
    }

    @Override
    protected void postAttach(Pipeline pipeline) {
        
    }

    @Override
    protected void preDetach(Pipeline pipeline) {
        
    }

    @Override
    protected void postDetach(Pipeline pipeline) {
        
    }

    @Override
    protected void afterIO(Pipeline pipeline) {
        
    }

    @Override
    protected void beforeRun(Pipeline pipeline, float tpf) {
        
    }

    @Override
    protected void afterRun(Pipeline pipeline, float tpf) {
        
    }


   
}