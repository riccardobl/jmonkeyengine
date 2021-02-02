package com.jme3.rendering.pipeline.passes;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
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
 * FXAA Antialiasing
 * @author Riccardo Balbo
 */
public class FXAAPass extends Effect{
    private RenderPass<? extends RenderPass> renderer;

    private static class PassIn{
        private static int scene=0;
        private static int depth=-1;
        // private static int subPixelShift=-2;
        // private static int spanMax=-4;
        // private static int reduceMul=-3;
        
    }
  

    private int inputI=0;

    public FXAAPass(Jme3ContextCreator contextFactory,FrameBufferFactory fbFactory,AssetManager assetManager){
        super(fbFactory);
        Material mat=new Material(assetManager,"Pipeline/FXAA/FXAA.j3md");
        renderer=contextFactory.newSurfaceRenderPass(mat, fbFactory);
        getEffectPipeline().add(renderer);
    }
    
    // public FXAAPass reset(){
    //     for(int i=0;;i++)  if(useInput(PassIn.scene+i,null)==null)break; //reset
    //     useInput(PassIn.depth,null);
        
    //     return this;
    // }
       
    
    public FXAAPass inColor(Texture texture){
        useInput(PassIn.scene+inputI,texture);
        inputI++; 
        return this;
    }

    public FXAAPass inDepth(Texture texture){
        useInput(PassIn.depth,texture);
        return this;
    }

    public FXAAPass outDepth(Texture outDepth){
        useOutput(RenderOutput.Depth,outDepth);
        return this;
    }

    public FXAAPass outColor(Texture... outScene){
        for(int i=0;i<outScene.length;i++)useOutput(RenderOutput.Color+i,outScene[i]);       
        return this;
    }

    public FXAAPass subPixelShift(Float v){
        renderer.useParam(VarType.Float,"SubPixelShift" , v==null?0.25f:v);
        return this;
    }

    public FXAAPass spanMax(Float v){
        renderer.useParam(VarType.Float,"SpanMax" , v==null?8f:v);
        return this;
    }

    public FXAAPass reduceMul(Float v){
        renderer.useParam(VarType.Float,"ReduceMul" , v==null?0.123f:v);
        return this;
    }

    @Override
    protected void beforeIO(Pipeline pipeline) {
        renderer.resetOutColors();
        renderer.resetOutDepth();
        renderer.clearParam("InputDepth");
        for(int i=0;;i++)  if(renderer.clearParam("Input"+i)==null)break; //reset
    }

    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {
  
        if(key instanceof Number){
            int keyi=((Number)key).intValue();
            if(keyi==PassIn.depth){
                renderer.useParam(VarType.Texture2D,"InputDepth",value);
            }else if(keyi>=PassIn.scene){
                renderer.useParam(VarType.Texture2D,"Input"+keyi,value);
            }
        }
        
    }
   
    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {
        if(key instanceof Number){
            int keyi=((Number)key).intValue();
            if(keyi==RenderOutput.Depth){
                renderer.outDepth((Texture)value);
            }else if(keyi>=RenderOutput.Color){
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