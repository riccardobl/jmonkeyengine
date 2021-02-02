package com.jme3.rendering.pipeline.passes;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.TestFunction;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.jme3.context.Jme3ContextCreator;
import com.jme3.rendering.pipeline.params.primitives.MutableNumber;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture;
import com.jme3.rendering.pipeline.renderer.RenderInput;
import com.jme3.rendering.pipeline.renderer.RenderOutput;
import com.jme3.rendering.pipeline.renderer.generic.RenderPass;
import com.jme3.shader.VarType;
import com.jme3.system.Timer;
import com.jme3.texture.Texture;

/**
 * DeferredPBRPass
 */
public class DeferredPBRPass extends Effect{
    private static class PassIn{
        private static int data=0;
        private static int depth=-1;
    }
   
    private RenderPass<? extends RenderPass> renderer;

    public DeferredPBRPass(Jme3ContextCreator contextFactory,FrameBufferFactory fbFactory,AssetManager assetManager){
        super(fbFactory);
        Material mat=new Material(assetManager,"Pipeline/DeferredPBR/DeferredPBR.j3md");
        mat.getAdditionalRenderState().setDepthFunc(TestFunction.Always);
        renderer=contextFactory.newSurfaceRenderPass(mat, fbFactory);
        getEffectPipeline().add(renderer);
    }

    // public MSAASolverPass reset(){
    //     // for(int i=0;;i++)  if(renderer.clearParam("Input"+i)==null)break; //reset
    //     // renderer.clearParam("InputDepth");
    //     // return this;
    //     for(int i=0;;i++)  if(useInput(PassIn.scene+i,null)==null)break; //reset
    //     useInput(PassIn.depth,null);
    //     return this;
    // }
    
    public DeferredPBRPass inData(Texture data1,Texture data2){
        useInput(PassIn.data+0,data1);
        useInput(PassIn.data+1,data2);

        return this;
    }



    public DeferredPBRPass inDepth(Texture texture){
        useInput(PassIn.depth,texture);

        return this;
    }


    public DeferredPBRPass outColor(Texture outScene){
        useOutput(RenderOutput.Color,outScene);       
        return this;
    }




    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {      
        if(key instanceof Number){
            int keyi=((Number)key).intValue();
            if(keyi==PassIn.depth){
                renderer.useParam(VarType.Texture2D,"Depth",value);
            }else if(keyi>=PassIn.data){
                renderer.useParam(VarType.Texture2D,"Data"+(keyi+1),value);
            }
        }
        
    }

    @Override
    protected void beforeIO(Pipeline pipeline) {
        renderer.resetOutColors();
        renderer.resetOutDepth();
        renderer.clearParam("Depth");
        for(int i=0;;i++)  if(renderer.clearParam("Data"+(i+1))==null)break; //reset
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