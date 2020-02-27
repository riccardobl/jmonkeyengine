package com.jme3.rendering.pipeline.passes;

import java.util.Arrays;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.params.PipelineParam;
import com.jme3.rendering.pipeline.params.PipelineParamPointer;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;

/**
 * FXAAPass
 */
public class FXAAPass extends TexturePass{
    private Vector2f resInverse=new Vector2f();
    
    public FXAAPass(RenderManager renderManager,AssetManager assetManager,FrameBufferFactory fbFactory){
        super(renderManager,assetManager,fbFactory,"Pipeline/FXAA/FXAA.j3md");
    }
    
    public FXAAPass inScene(PipelineParamPointer<Texture> inScene){
        inParam("Scene",inScene);
        return this;
    }


    public FXAAPass outScene(PipelineParamPointer<Texture> inScene){
        outColors(Arrays.asList(inScene));
        return this;
    }


    @Override
    public void afterFrameBufferUpdate(FrameBuffer fb){
        int width=getFbFactory().getFrameBufferWidth(fb);
        int height=getFbFactory().getFrameBufferHeight(fb);
        resInverse.set(1f / width, 1f / height);
        inParam("ResolutionInverse", PipelineParam.from(resInverse));
    }
    
}