package com.jme3.rendering.pipeline.passes;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.params.primitives.MutableNumber;
import com.jme3.system.Timer;
import com.jme3.texture.Texture;

/**
 * FXAA Antialiasing
 * @author Riccardo Balbo
 */
public class FXAAPass extends MaterialPass<FXAAPass>{
    

    public FXAAPass(RenderManager renderManager, FrameBufferFactory fbFactory,AssetManager assetManager){
        super(renderManager,fbFactory,null,assetManager,"Pipeline/FXAA/FXAA.j3md");
    }
    
    public FXAAPass inColor(Texture... inScene){
        for(int i=0;;i++)  if(useInput("Scene"+i,null)==null)break; //reset
        for(int i=0;i<inScene.length;i++)   useInput("Scene"+i,inScene[i]);        
        return this;
    }

  

    public FXAAPass outColor(Texture... outScene){
        for(int i=0;;i++)  if(useOutput(RenderPass.RENDER_OUT_COLOR+i,null)==null)break; //reset
        for(int i=0;i<outScene.length;i++)useOutput(RenderPass.RENDER_OUT_COLOR+i,outScene[i]);
        return this;
    }

    public FXAAPass subPixelShift(MutableNumber<Float> v){
        useInput("SubPixelShift",v==null?0.25f:v);
        return this;
    }

    public FXAAPass spanMax(MutableNumber<Float> v){
        useInput("SpanMax",v==null?8f:v);
        return this;
    }

    public FXAAPass reduceMul(MutableNumber<Float> v){
        useInput("ReduceMul",v==null?0.123f:v);
        return this;
    }



   
}