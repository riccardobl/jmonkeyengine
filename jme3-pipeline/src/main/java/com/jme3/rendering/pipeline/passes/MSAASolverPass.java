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
 * MSAASolverPass
 */
public class MSAASolverPass extends MaterialPass<MSAASolverPass>{
    public enum MSAASolverMethod{
        RESOLVE_METHOD_AVERAGE,
        RESOLVE_METHOD_MAX_R,
        RESOLVE_METHOD_MIN_R,
        RESOLVE_METHOD_FIRST_SAMPLE
    }


    public MSAASolverPass(RenderManager renderManager, FrameBufferFactory fbFactory,AssetManager assetManager){
        super(renderManager,fbFactory,null,assetManager,"Pipeline/MSAASolver/MSAASolver.j3md");
    }
    
    public MSAASolverPass inColor(Texture... inScene){
        for(int i=0;;i++)  if(useInput("Input"+i,null)==null)break; //reset
        for(int i=0;i<inScene.length;i++)   useInput("Input"+i,inScene[i]);        
        return this;
    }

    public MSAASolverPass method(MSAASolverMethod... methods){
        for(int i=0;;i++)  if(useInput("ResolveMethod"+i,null)==null)break; //reset
        for(int i=0;i<methods.length;i++)   useInput("ResolveMethod"+i,methods[i].ordinal());        
        return this;
    }

    public MSAASolverPass outColor(Texture... outScene){
        for(int i=0;;i++)  if(useOutput(RenderPass.RENDER_OUT_COLOR+i,null)==null)break; //reset
        for(int i=0;i<outScene.length;i++)useOutput(RenderPass.RENDER_OUT_COLOR+i,outScene[i]);
        return this;
    }

    int nSamples=-1;
    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {
        int samples=-1;
        if (key instanceof String) {
            if (key.toString().startsWith("Input")) {
                Texture tx = (Texture) value;
                samples=tx.getImage().getMultiSamples();
            }
        }
        
        if(samples!=-1&&nSamples!=samples){
            useInput("NumSamples",samples);
            nSamples=samples;
        }

        super.onInput(pipeline, key, value);
    }


}