package com.jme3.rendering.pipeline.passes;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.params.primitives.MutableNumber;
import com.jme3.system.Timer;
import com.jme3.texture.Texture;

/**
 * ToneMap
 * @author Riccardo Balbo
 */
public class ToneMapPass extends MaterialPass<FXAAPass>{
    public enum ToneMapMode{
        FILMIC,
        HABLE_FILMIC,
        LOTTES_2016
    }    

    public ToneMapPass(RenderManager renderManager, FrameBufferFactory fbFactory,AssetManager assetManager){
        super(renderManager,fbFactory,null,assetManager,"Pipeline/ToneMap/ToneMap.j3md");
    }
    
    public ToneMapPass inColors(Texture... inScene){
        for(int i=0;;i++)  if(useInput("Scene"+i,null)==null)break; //reset
        for(int i=0;i<inScene.length;i++)   useInput("Scene"+i,inScene[i]);        
        return this;
    }

    public ToneMapPass outColors(Texture... outScene){
        for(int i=0;;i++)  if(useOutput(RenderPass.RENDER_OUT_COLOR+i,null)==null)break; //reset
        for(int i=0;i<outScene.length;i++)useOutput(RenderPass.RENDER_OUT_COLOR+i,outScene[i]);
        return this;
    }

    public ToneMapPass exposure(Object... v){
        for(int i=0;;i++)  if(useInput("Exposure"+i,null)==null)break; //reset
        for(int i=0;;i++)  if(useInput("ExposureTexture"+i,null)==null)break; //reset
        for(int i=0;i<v.length;i++)  {
            if(v[i] instanceof Texture){
                useInput("ExposureTexture"+i,v[i]);
            }else{
                useInput("Exposure"+i,v[i]);
            }
        }
        return this;
    }

    public ToneMapPass mode(ToneMapMode v){
        useInput("Mode",v.ordinal());
        return this;
    }




   
}