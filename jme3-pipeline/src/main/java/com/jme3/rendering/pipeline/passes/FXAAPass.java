package com.jme3.rendering.pipeline.passes;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.texture.Texture;

/**
 * FXAA Antialiasing
 * @author Riccardo Balbo
 */
public class FXAAPass extends MaterialPass{
    private Vector2f resInverse=new Vector2f();
    

    public FXAAPass(RenderManager renderManager,AssetManager assetManager,FrameBufferFactory fbFactory){
        super(renderManager,assetManager,fbFactory,"Pipeline/FXAA/FXAA.j3md");
        useInput("ResolutionInverse", resInverse);
    }
    
    public FXAAPass inColor(Texture inScene){
        useInput("Scene",inScene);
        return this;
    }

    public FXAAPass subPixelShift(Float v){
        useInput("SubPixelShift",v==null?0.25f:v);
        return this;
    }

    public FXAAPass spanMax(Float v){
        useInput("SpanMax",v==null?8f:v);
        return this;
    }

    public FXAAPass reduceMul(Float v){
        useInput("ReduceMul",v==null?0.123f:v);
        return this;
    }

    public FXAAPass outColor(Texture outScene){
        useOutput(RenderPass.RENDER_OUT_COLOR,outScene);
        return this;
    }

   
    @Override
    protected void onOutput(Pipeline pipeline,Object key,Object value){
        super.onOutput(pipeline,key, value);
        if(key instanceof Number&&((Number)key).intValue()==RenderPass.RENDER_OUT_COLOR){
            Texture tx=(Texture) value;
            int width=tx.getImage().getWidth();
            int height=tx.getImage().getHeight();
            resInverse.set(1f / width, 1f / height);
        }
    }

}