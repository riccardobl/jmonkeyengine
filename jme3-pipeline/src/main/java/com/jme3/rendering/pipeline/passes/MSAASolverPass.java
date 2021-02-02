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
 * MSAASolverPass
 */
public class MSAASolverPass extends Effect{
    private static class PassIn{
        private static int scene=0;
        private static int method=100;
        private static int depthMethod=-100;
        private static int depth=-1;
    }
    public enum MSAASolverMethod{
        RESOLVE_METHOD_AVERAGE,
        RESOLVE_METHOD_MAX_R,
        RESOLVE_METHOD_MIN_R,
        RESOLVE_METHOD_FIRST_SAMPLE
    }

    private RenderPass<? extends RenderPass> renderer;
    private int inputI=0;
    private int nSamples=-1;

    public MSAASolverPass(Jme3ContextCreator contextFactory,FrameBufferFactory fbFactory,AssetManager assetManager){
        super(fbFactory);
        Material mat=new Material(assetManager,"Pipeline/MSAASolver/MSAASolver.j3md");
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
    
    public MSAASolverPass inColor(MSAASolverMethod method,Texture texture){
        useInput(PassIn.scene+inputI,texture);
        useInput(PassIn.method+inputI,method.ordinal());
        inputI++;
        // renderer.useParam(VarType.Texture2D,"Input"+(inputI),texture);        
        // renderer.useParam(VarType.Int,"ResolveMethod"+inputI,method.ordinal());
        // inputI++;
        return this;
    }



    public MSAASolverPass inDepth(MSAASolverMethod method,Texture texture){
        useInput(PassIn.depth,texture);
        useInput(PassIn.depthMethod,method.ordinal());
        // inputI++;
        // renderer.useParam(VarType.Texture2D,"InputDepth",texture);        
        // renderer.useParam(VarType.Int,"ResolveMethodDepth",method.ordinal());   
        return this;
    }


    public MSAASolverPass outColor(Texture... outScene){
        for(int i=0;i<outScene.length;i++)useOutput(RenderOutput.Color+i,outScene[i]);       
        return this;
    }


    public MSAASolverPass outDepth(Texture outDepth){
        useOutput(RenderOutput.Depth,outDepth);
        // renderer.outDepth(outDepth);       
        return this;
    }

    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {
        int samples=-1;

        if(value instanceof Texture){
            Texture tx = (Texture) value;
            samples=tx.getImage().getMultiSamples();
        }

        if(samples!=-1&&nSamples!=samples){
            renderer.useParam(VarType.Int,"NumSamples",samples);
            nSamples=samples;
        }

        if(key instanceof Number){
            int keyi=((Number)key).intValue();
            if(keyi==PassIn.depth){
                renderer.useParam(VarType.Texture2D,"InputDepth",value);
            }else if(keyi>=PassIn.method){
                int method=keyi-PassIn.method;
                renderer.useParam(VarType.Int,"ResolveMethod"+method,value);
            }else if(keyi>=PassIn.scene){
                renderer.useParam(VarType.Texture2D,"Input"+keyi,value);
            }else if(keyi==PassIn.depthMethod){
                renderer.useParam(VarType.Int,"ResolveMethodDepth",value);
            }
        }
        
    }
    @Override
    protected void beforeIO(Pipeline pipeline) {
        renderer.resetOutColors();
        renderer.resetOutDepth();
        renderer.clearParam("InputDepth");
        for(int i=0;;i++)  if(renderer.clearParam("Input"+i)==null)break; //reset
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
    protected void onRun(Pipeline pipeline, float tpf) {
        if(nSamples<=1){
            skip(pipeline);
        }else{
            super.onRun(pipeline, tpf);
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