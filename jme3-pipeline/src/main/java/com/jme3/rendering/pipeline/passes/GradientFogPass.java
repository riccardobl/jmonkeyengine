package com.jme3.rendering.pipeline.passes;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.WorldParamsUtil.WorldParam;
import com.jme3.rendering.pipeline.jme3.context.Jme3ContextCreator;
import com.jme3.rendering.pipeline.jme3.renderer.WorldParams;
import com.jme3.rendering.pipeline.params.primitives.MutableNumber;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture;
import com.jme3.rendering.pipeline.renderer.RenderOutput;
import com.jme3.rendering.pipeline.renderer.generic.RenderPass;
import com.jme3.shader.VarType;
import com.jme3.system.Timer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;

/**
 * Gradient based fog
 * @author Riccardo Balbo
 */
public class GradientFogPass  extends Effect{
    private  static final java.util.logging.Logger logger =  java.util.logging.Logger.getLogger( GradientFogPass.class.getName());
    private RenderPass<? extends RenderPass> renderer;
    private static class PassIn{
        private static int depth=200;
        private static int cam=100;
        private static int scene=0;
    }
    private int inputI=0;
    private int outI=0;
 
    public GradientFogPass(Jme3ContextCreator contextFactory,FrameBufferFactory fbFactory,AssetManager assetManager,Timer timer){
        super(fbFactory);
        Material mat=new Material(assetManager,"Pipeline/GradientFog/GradientFog.j3md");
        renderer=contextFactory.newSurfaceRenderPass(mat, fbFactory);
        getEffectPipeline().add(renderer);
    }


      
    public GradientFogPass density(MutableNumber<Float> v){
        useInput("Density",v);
        return this;
    }

    public GradientFogPass gradient(Texture gradient){
        SmartTexture smt=SmartTexture.from(gradient);
        smt.minFilter(MinFilter.BilinearNoMipMaps);
        smt.magFilter(MagFilter.Bilinear);
        smt.wrapAxis(WrapMode.EdgeClamp,WrapMode.EdgeClamp, WrapMode.EdgeClamp);
        useInput("FogGradient",gradient);
        return this;
    }

 
    public GradientFogPass inScene(Texture texture, Texture depth,Camera cam){
        if(inputI>=100)throw new RuntimeException("Too many inputs");
        useInput(PassIn.scene+inputI,texture);
        useInput(PassIn.depth+inputI,depth);
        useInput(PassIn.cam+inputI,cam);
        inputI++; 
        return this;
    }
    

    public GradientFogPass outColor(Texture outScene){
        useOutput(RenderOutput.Color+outI,outScene);       
        outI++;
        return this;
    }
    

    @Override
    public void beforeIO(Pipeline pipeline){
        renderer.resetOutColors();
        renderer.resetOutDepth();
        for(int i=0;;i++)  {
            if(
                renderer.clearParam("Scene"+i)==null
                ||renderer.clearParam("SceneCamera"+i)==null
                ||renderer.clearParam("Depth"+i)==null
            )break; //reset
        }
    }

    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {  
        if(key instanceof Number){
            int keyi=((Number)key).intValue();
            if(keyi>=PassIn.cam){
                renderer.useParam(VarType.Texture2D,"Depth"+(keyi-PassIn.depth),value);
            }else if(keyi>=PassIn.cam){
                renderer.useParam(VarType.ShaderStorageBufferObject,"SceneCam"+(keyi-PassIn.cam),WorldParams.updateAndGet((Camera)value));
            }else if(keyi>=PassIn.scene){
                renderer.useParam(VarType.Texture2D,"Scene"+keyi,value);
            }
        }        
    }

    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {
        if(key instanceof Number){
            int keyi=((Number)key).intValue();
            if(keyi>=RenderOutput.Color){
                renderer.outColor(keyi-RenderOutput.Color,(Texture)value);
            }
        }
    }


    @Override
    protected void preAttach(Pipeline pipeline) {
        
    }


    @Override
    protected void postAttach(Pipeline pipeline) {
        
    }


    @Override
    protected void preDetach(Pipeline pipeline) {
        
    }


    @Override
    protected void postDetach(Pipeline pipeline) {
        
    }


    @Override
    protected void afterIO(Pipeline pipeline) {
        
    }


    @Override
    protected void beforeRun(Pipeline pipeline, float tpf) {
        
    }


    @Override
    protected void afterRun(Pipeline pipeline, float tpf) {
        
    }
}