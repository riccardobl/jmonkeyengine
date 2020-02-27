package com.jme3.rendering.pipeline.passes;

import java.util.Arrays;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.params.PipelineParam;
import com.jme3.rendering.pipeline.params.PipelineParamPointer;
import com.jme3.texture.Texture;

/**
 * GradientFogPass
 */
public class GradientFogPass  extends TexturePass{
    private Camera cam;
    private final Vector2f frustumNearFar=new Vector2f();
    private final PipelineParam<Vector2f> frustumNearFarParam=PipelineParam.from(frustumNearFar);
    private final PipelineParam<Texture> gradient=PipelineParam.newEmpty();

    public GradientFogPass(RenderManager renderManager,AssetManager assetManager,FrameBufferFactory fbFactory){
        super(renderManager,assetManager,fbFactory,"Pipeline/GradientFog/GradientFog.j3md");

        gradient.setValue(assetManager.loadTexture("Pipeline/GradientFog/defaultGradient.bmp"));
        
        inParam("FogGradient",gradient);
        inParam("FrustumNearFar",frustumNearFarParam);

    }


    public GradientFogPass sceneCamera(Camera cam){
        this.cam=cam;
        return this;
    }
    
    public GradientFogPass inScene(PipelineParamPointer<Texture> inScene){
        inParam("Scene",inScene);
        return this;
    }

    public GradientFogPass gradient(PipelineParamPointer<Texture> gradient){
        this.gradient.setValue(gradient.get(this).getValue());
        return this;
    }

    public GradientFogPass inDepth(PipelineParamPointer<Texture> inDepth){
        inParam("Depth",inDepth);
        return this;
    }


    public GradientFogPass outScene(PipelineParamPointer<Texture> inScene){
        outColors(Arrays.asList(inScene));
        return this;
    }


    @Override
    public void run(float tpf){
        if(cam==null)return;
        frustumNearFar.set(cam.getFrustumNear(),cam.getFrustumFar());
        super.run(tpf);
    }
    
}