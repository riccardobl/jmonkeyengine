package com.jme3.rendering.pipeline.passes.bloom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.jme3.context.Jme3ContextCreator;
import com.jme3.rendering.pipeline.params.primitives.MutableBoolean;
import com.jme3.rendering.pipeline.params.primitives.MutableNumber;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture;
import com.jme3.rendering.pipeline.passes.Effect;
import com.jme3.rendering.pipeline.renderer.RenderOutput;
import com.jme3.rendering.pipeline.renderer.generic.RenderPass;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;

/**
 * BloomPass
 */
public class BloomMergePass extends Effect{
    private RenderPass<? extends RenderPass> renderer;
    private int inputI=0;
    private int layerI=0;
    private int outI=0;
    
    private static class PassIn{
        private static int scene=0;
        private static int layer=100;
        private static int intensity=200;
        
    }

    public BloomMergePass(Jme3ContextCreator contextFactory,FrameBufferFactory fbFactory,AssetManager assetManager){
        super(fbFactory);
        Material mat=new Material(assetManager, "Pipeline/FastBloom/BloomMerger.j3md");
        renderer=contextFactory.newSurfaceRenderPass(mat, fbFactory);
        getEffectPipeline().add(renderer);
    // protected BloomMergePass(RenderManager renderManager, FrameBufferFactory fbFactory, AssetManager assetManager) {
        // super(renderManager, fbFactory, null, assetManager, "Pipeline/FastBloom/BloomMerger.j3md");
    }

    public BloomMergePass inColor(Texture texture){
        if(inputI>=100)throw new RuntimeException("Too many inputs");
        useInput(PassIn.scene+inputI,texture);
        inputI++; 
        return this;
    }

    // public BloomMergePass inColors(Texture2D... colors) {
    //     for(int i=0;;i++)  if(useInput("Scene"+i,null)==null)break; //reset
    //     for(int i=0;i<colors.length;i++) useInput("Scene" + (i), colors[i]);        
    //     return this;
    // }


    // public BloomMergePass strength(MutableNumber<Float> ... strength) {
    //     for(int i=0;i<strength.length;i++){
    //         useInput("Intensity"+i, strength[i]);
    //     }
    //     return this;
    // }
    public BloomMergePass inBloomLayer(Texture2D layer,float intensity) {
        if(layerI>=100)throw new RuntimeException("Too many bloom layers");
        useInput(PassIn.layer+layerI, layer);
        useInput(PassIn.intensity+layerI, intensity);
        layerI++;
        return this;
    }
    // public BloomMergePass inBloomLayers(List<Texture2D>... layers) {
    //     for(int i=0;i<layers.length;i++){
    //         List<Texture2D> ll=layers[i];
    //         for(int j=0;j<ll.size();j++){
    //             Texture2D layer=ll.get(j);
    //             useInput("BloomLayer"+i+"_"+j, layer);
    //         }
    //     }
    //     return this;
    // }


    public BloomMergePass outColor(Texture outScene){
        useOutput(RenderOutput.Color+outI,outScene);       
        outI++;
        return this;
    }
    

    @Override
    public void beforeIO(Pipeline pipeline){
        renderer.resetOutColors();
        renderer.resetOutDepth();
        for(int i=0;;i++)  {
            if(
                renderer.clearParam("Intensity"+i)==null
                ||renderer.clearParam("BloomLayer"+i)==null
            )break; //reset
        }
        for(int i=0;;i++)  {
            if(
                renderer.clearParam("Scene"+i)==null
            )break; //reset
        }
    }


    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {  
        if(key instanceof Number){
            int keyi=((Number)key).intValue();
            if(keyi>=PassIn.intensity){
                renderer.useParam(VarType.Float,"Intensity"+(keyi-PassIn.intensity),((Number)value).floatValue());
            }else if(keyi>=PassIn.layer){
                renderer.useParam(VarType.Texture2D,"BloomLayer"+(keyi-PassIn.layer),value);
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
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void postAttach(Pipeline pipeline) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void preDetach(Pipeline pipeline) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void postDetach(Pipeline pipeline) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void afterIO(Pipeline pipeline) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void beforeRun(Pipeline pipeline, float tpf) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void afterRun(Pipeline pipeline, float tpf) {
        // TODO Auto-generated method stub
        
    }

}