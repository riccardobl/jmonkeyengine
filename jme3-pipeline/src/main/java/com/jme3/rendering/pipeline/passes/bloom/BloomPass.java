package com.jme3.rendering.pipeline.passes.bloom;

import java.util.ArrayList;

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
public class BloomPass extends Effect{
    private RenderPass<? extends RenderPass> renderer;
    private int inputI=0;
    private int outI=0;
    private static class PassIn{
        private static int scene=0;
    }

    public BloomPass(Jme3ContextCreator contextFactory,FrameBufferFactory fbFactory,AssetManager assetManager){
        super(fbFactory);
        Material mat=new Material(assetManager,"Pipeline/FastBloom/FastBloom.j3md");
        renderer=contextFactory.newSurfaceRenderPass(mat, fbFactory);
        getEffectPipeline().add(renderer);
    }

    public BloomPass brightPoint(MutableNumber<Float> brightPoint) {
        useInput("BrightPoint", brightPoint);
        return this;
    }

    public BloomPass direction(Vector2f dir) {
        useInput("BlurDirection", dir);
        return this;
    }

    public BloomPass extract(MutableBoolean v) {
        useInput("Extract", v);
        return this;
    }

    public BloomPass inColor(Texture texture){
        useInput(PassIn.scene+inputI,texture);
        inputI++; 
        return this;
    }

 
    public BloomPass outColor(Texture outScene){
        useOutput(RenderOutput.Color+outI,outScene);       
        outI++;
        return this;
    }
    
    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {
        if (key instanceof Number) {
            int keyi = ((Number) key).intValue();
            if (keyi >= PassIn.scene) {
                if (value instanceof Texture) {
                    SmartTexture stx = SmartTexture.from(value);

                    stx.minFilter(MinFilter.BilinearNoMipMaps);
                    stx.magFilter(MagFilter.Bilinear);
                    stx.wrapAxis(WrapMode.EdgeClamp, WrapMode.EdgeClamp, WrapMode.EdgeClamp);

                    value = stx.get(pipeline, this);
                    
                    renderer.useParam(VarType.Texture2D,"Scene",value);
                }
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
    protected void beforeIO(Pipeline pipeline) {
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

    // @Override
    // protected void onOutput(Pipeline pipeline, Object key, Object value) {
    //     if (key instanceof Number) {
    //         int keyi = ((Number) key).intValue();
    //         if (keyi >= PassIn.scene) {
    //             if (value instanceof Texture) {
    //                 SmartTexture stx = SmartTexture.from(value);

    //                 stx.minFilter(MinFilter.BilinearNoMipMaps);
    //                 stx.magFilter(MagFilter.Bilinear);
    //                 stx.wrapAxis(WrapMode.EdgeClamp, WrapMode.EdgeClamp, WrapMode.EdgeClamp);

    //                 value = stx.get(pipeline, this);
                    
    //                 renderer.useParam(VarType.Texture2D,"Scene",value);
    //             }
    //         }
    //     }

    // }
    // @Override
    // protected Object onMatParamOutput(Pipeline pipeline, int skey, Object value) {
    //     if (value instanceof Texture) {
    //         SmartTexture stx = SmartTexture.from(value);

    //         stx.minFilter(MinFilter.BilinearNoMipMaps);
    //         stx.magFilter(MagFilter.Bilinear);
    //         stx.wrapAxis(WrapMode.EdgeClamp, WrapMode.EdgeClamp, WrapMode.EdgeClamp);

    //         value = stx.get(pipeline, this);

    //         // if (value instanceof Texture2D) {
    //         //     Texture2D tx = (Texture2D) value;
    //         //     assert tx.getImage()!=null;
    //         //     outSize.set(tx.getImage().getWidth(), tx.getImage().getHeight());
    //         // }
    //         return value;
    //     }
    //     return super.onMatParamOutput(pipeline, skey, value);
    // }
}