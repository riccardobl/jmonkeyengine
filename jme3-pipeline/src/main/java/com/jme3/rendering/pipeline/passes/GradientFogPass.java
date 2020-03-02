package com.jme3.rendering.pipeline.passes;

import java.util.Arrays;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.texture.Texture;

/**
 * GradientFogPass
 */
public class GradientFogPass  extends TexturePass{
    private final Vector2f frustumNearFar=new Vector2f();
    public static enum PassIn{
        CAMERA
    }
    
    public GradientFogPass(RenderManager renderManager,AssetManager assetManager,FrameBufferFactory fbFactory){
        super(renderManager,assetManager,fbFactory,"Pipeline/GradientFog/GradientFog.j3md");
        useInput("FogGradient",assetManager.loadTexture("Pipeline/GradientFog/defaultGradient.bmp"));
        useInput("FrustumNearFar",frustumNearFar);
    }


    public GradientFogPass sceneCamera(Camera cam){
        useInput(PassIn.CAMERA,cam);
        return this;
    }
    
    public GradientFogPass inColor(Texture inScene){
        useInput("Scene",inScene);
        return this;
    }

    public GradientFogPass gradient(Texture gradient){
        useInput("FogGradient",gradient);
        return this;
    }

    public GradientFogPass inDepth(Texture inDepth){
        useInput("Depth",inDepth);
        return this;
    }


    public GradientFogPass outColor(Texture outScene){
        useOutput(RenderPass.RENDER_OUT_COLOR,outScene);
        return this;
    }
    
    @Override
    public void onInput(Object key,Object value){
        super.onInput(key, value);
        if(key==PassIn.CAMERA){
            Camera cam=(Camera)value;
            frustumNearFar.set(cam.getFrustumNear(),cam.getFrustumFar());
        }
    }

}