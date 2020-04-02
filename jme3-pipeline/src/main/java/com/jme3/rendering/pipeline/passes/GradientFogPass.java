package com.jme3.rendering.pipeline.passes;

import com.jme3.asset.AssetManager;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.WorldParamsUtil.WorldParam;
import com.jme3.rendering.pipeline.params.primitives.MutableNumber;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture;
import com.jme3.rendering.pipeline.renderer.gl.WorldParams;
import com.jme3.system.Timer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;

/**
 * Gradient based fog
 * @author Riccardo Balbo
 */
public class GradientFogPass  extends MaterialPass<GradientFogPass>{
    private  static final java.util.logging.Logger logger =  java.util.logging.Logger.getLogger( GradientFogPass.class.getName());
 
    
    // class SceneCam{
    //     Camera cam;
    //     final Vector2f frustumNearFar=new Vector2f();
    //     final Matrix4f viewProjectionMatrixInverse=new Matrix4f();   
    // }

    // SceneCam cams[];
 
    public GradientFogPass(RenderManager renderManager,  Timer timer,FrameBufferFactory fbFactory,AssetManager assetManager){
        super(renderManager,fbFactory,timer,assetManager,"Pipeline/GradientFog/GradientFog.j3md");
        gradient(assetManager.loadTexture("Pipeline/GradientFog/defaultGradient.png"));
    }


    public GradientFogPass sceneCamera(Camera... cams){
        for(int i=0;;i++)   if(useInput("SceneCamera"+i,null)==null )break; //reset

        // this.cams=new SceneCam[cams.length];
        for(int i=0;i<cams.length;i++){
            useInput("SceneCamera"+i,cams[i]);
            // this.cams[i]=new SceneCam();
            // this.cams[i].cam=cams[i];
            // useInput("FrustumNearFar"+i,   this.cams[i].frustumNearFar);
            // useInput("ViewProjectionMatrixInverse"+i,   this.cams[i].viewProjectionMatrixInverse);
        }
        return this;
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


    public GradientFogPass inColor(Texture...inColor ){
        for(int i=0;;i++)   if(useInput("Scene"+i,null)==null )break; //reset
        for(int i=0;i<inColor.length;i++)useInput("Scene"+i,inColor[i]);
        return this;
    }

    public GradientFogPass inDepth(Texture...inDepth ){
        for(int i=0;;i++) if(useInput("Depth"+i,null)==null  )break;//reset
        for(int i=0;i<inDepth.length;i++)useInput("Depth"+i,inDepth[i]);
        return this;
    }

    public GradientFogPass outColor(Texture... outScene){
        for(int i=0;;i++)   if(useOutput(RenderPass.RENDER_OUT_COLOR+i,null)==null )break; //reset
        for(int i=0;i<outScene.length;i++)useOutput(RenderPass.RENDER_OUT_COLOR+i,outScene[i]);
        return this;
    }

    @Override
    public void beforeIO(Pipeline pipeline){
        super.beforeIO(pipeline);
        // for(SceneCam sceneCam:cams){
        //     sceneCam.frustumNearFar.set(sceneCam.cam.getFrustumNear(),sceneCam.cam.getFrustumFar());   
        //     sceneCam.cam.getViewProjectionMatrix().invert(sceneCam.viewProjectionMatrixInverse);    
        // }
    }



}