package com.jme3.rendering.pipeline.test;

import java.util.Arrays;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.ViewPort;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.integration.PipelineMigrationUtils;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.PipelinePointerFactory;
import com.jme3.rendering.pipeline.gl.GLDebuggerAppState;
import com.jme3.rendering.pipeline.integration.PipelineRunnerAppState;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.params.primitives.MutableNumber;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture2D;
import com.jme3.rendering.pipeline.passes.FXAAPass;
import com.jme3.rendering.pipeline.passes.GradientFogPass;
import com.jme3.rendering.pipeline.passes.MSAASolverPass;
import com.jme3.rendering.pipeline.passes.PrintPass;
import com.jme3.rendering.pipeline.passes.RenderViewPortPass;
import com.jme3.rendering.pipeline.passes.ToneMapPass;
import com.jme3.rendering.pipeline.passes.MSAASolverPass.MSAASolverMethod;
import com.jme3.rendering.pipeline.passes.bloom.BloomEffectBuilder;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.system.Timer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Image.Format;

import org.jesse.Jesse;

/**
 * TestApp
 */
public class TestApp extends SimpleApplication{
    public static void main(String[] args) {
        AppSettings settings=new AppSettings(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL33);
        settings.putBoolean("GraphicsDebug", true);
        TestApp app=new TestApp();
        app.setSettings(settings);
        app.start();
    }


    void loadPipeline(){

        
        
        ViewPort mainVp=PipelineMigrationUtils.getMainViewPort( this);

        // initialization
        FrameBufferFactory fbFactory=PipelineMigrationUtils.getFrameBufferFactory(this);
        PipelinePointerFactory pointers=PipelineMigrationUtils.getPointerFactory(this);
    

        Pipeline pipeline=new Pipeline();

        // runner
        PipelineRunnerAppState pipelineAppState=new PipelineRunnerAppState();
        pipelineAppState.addPipeline(pipeline);
        stateManager.attach(pipelineAppState);


        Timer timer=getTimer();


        Format sceneFormat=Format.RGB16F;
        Format depthFormat=Format.Depth24;
        

        // pipeline plot
        pipeline.add(
            new RenderViewPortPass(renderManager, fbFactory,timer,mainVp)
            .outColors(  
                pointers.newPointer(Texture2D.class,     (pp,pass,tx)->{
                    SmartTexture2D txb=SmartTexture.from(tx);
                    txb.numSamples(8);
                    txb.format(sceneFormat);
                    return txb.get(pp,pass);
                }
        
                ).rel().next("scene"))
            .outDepth(
                pointers.newPointer(Texture2D.class,
                (pp,pass,tx)->{
                    SmartTexture2D txb=SmartTexture.from(tx);
                    txb.format(depthFormat);
                    txb.numSamples(8);
                    return txb.get(pp,pass);
                }
            ).rel().next("depth")            
        ));

        pipeline.add(new MSAASolverPass(renderManager, fbFactory, assetManager)
            .inColor(
                pointers.newPointer(Texture2D.class).rel().previous("scene")
            )
            .outColor(
                pointers.newPointer(Texture2D.class).rel().next("scene")
            )
            .method(
                MSAASolverMethod.RESOLVE_METHOD_AVERAGE
            )
        );

        pipeline.add(new MSAASolverPass(renderManager, fbFactory, assetManager)
        .inColor(
            pointers.newPointer(Texture2D.class).rel().previous("depth")
        )
        .outColor(
            pointers.newPointer(Texture2D.class,(pp,pass,tx)->{
                SmartTexture2D txb=SmartTexture.from(tx);
                txb.format(Format.R32F);
                return txb.get(pp,pass);
            }).rel().next("depth")    
        )
        .method(
            MSAASolverMethod.RESOLVE_METHOD_AVERAGE
        )
    );
        
        pipeline.add(
            new ToneMapPass(renderManager,fbFactory,assetManager)
            .inColors(pointers.newPointer(Texture2D.class).rel().previous("scene"))
            .outColors( pointers.newPointer(Texture2D.class).rel().next("scene"))
        );


        pipeline.add(
            BloomEffectBuilder.newBuilder(renderManager, fbFactory, assetManager)
                .newLayer()
                    .downscale(0.5f, 0.5f) // half size
                    .newBlurPass(0,3) // v blur
                    .newBlurPass(3,0) // h blur
                .newLayer()
                    .downscale(0.5f, 0.5f) // half size of previous layer
                    .newBlurPass(3,0) // h blur
                    .newBlurPass(0,3) // strong hblur
                .buildEffect()
                .brightPoint(new MutableNumber<Float>(0.6f))
                .inColors(pointers.newPointer(Texture2D.class).rel().previous("scene"))
                .outColors( pointers.newPointer(Texture2D.class).rel().next("scene"))
                // .outColors(fbFactory.getDefaultTarget())

        );
        

        pipeline.add(
            new GradientFogPass(renderManager,timer,fbFactory,assetManager)
            .sceneCamera(mainVp.getCamera())
            .inColor(pointers.newPointer(Texture2D.class).rel().previous("scene"))
            .inDepth(pointers.newPointer(Texture2D.class).rel().previous("depth"))
            .outColor(pointers.newPointer(Texture2D.class).rel().next("scene"))
        );
        
        pipeline.add(
            new FXAAPass(renderManager,fbFactory,assetManager)
            .inColor(pointers.newPointer(Texture2D.class).rel().previous("scene"))
            .outColor(fbFactory.getDefaultTarget())
        );


    }

	@Override
	public void simpleInitApp() {
        loadPipeline();
        
        setPauseOnLostFocus(false) ;

        stateManager.attach(new GLDebuggerAppState());

        Jesse.setupDefaultCamera(cam, flyCam);
        Jesse.buildAndAttachScene(assetManager, rootNode);
	}

    
}