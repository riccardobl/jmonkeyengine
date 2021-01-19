package com.jme3.rendering.pipeline.test;

import java.util.ArrayList;
import java.util.Arrays;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.plugins.OGGLoader;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.integration.PipelineMigrationUtils;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.PipelinePointerFactory;
import com.jme3.rendering.pipeline.PipelineRunner;
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
import com.jme3.rendering.pipeline.passes.RunGameLogicPass;
import com.jme3.rendering.pipeline.passes.ToneMapPass;
import com.jme3.rendering.pipeline.passes.UpdateGeometryPass;
import com.jme3.rendering.pipeline.passes.UpdateTimerPass;
import com.jme3.rendering.pipeline.passes.MSAASolverPass.MSAASolverMethod;
import com.jme3.rendering.pipeline.passes.bloom.BloomEffectBuilder;
import com.jme3.rendering.pipeline.renderer.GeometriesExtractorPass;
import com.jme3.rendering.pipeline.renderer.GeometryBucketsExtractor;
import com.jme3.rendering.pipeline.renderer.GeometryLists;
import com.jme3.rendering.pipeline.jme3.inputs.*;
import com.jme3.rendering.pipeline.jme3.renderer.*;
import com.jme3.rendering.pipeline.jme3.LegacyApplicationWrapper;
import com.jme3.rendering.pipeline.jme3.appstates.StatesHandlerPass;
import com.jme3.rendering.pipeline.jme3.audio.JmeAudioRenderPass;
import com.jme3.rendering.pipeline.jme3.context.*;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.plugins.J3MLoader;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shader.plugins.GLSLLoader;
import com.jme3.system.AppSettings;
import com.jme3.system.NanoTimer;
import com.jme3.system.SystemListener;
import com.jme3.system.Timer;
import com.jme3.system.JmeContext.Type;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.ColorSpace;

public class SimpleRender implements SystemListener {
    public static void main(String[] args) {
        try{
        new SimpleRender().start();
        }catch(Throwable e){
            e.printStackTrace();
        }
    }
    

    public void start(){
            AppSettings settings=new AppSettings(true);
            settings.setAudioRenderer(AppSettings.LWJGL_OPENAL);
            InputManager inputManager=new InputManager();
            
            AssetManager assetManager=new DesktopAssetManager();
            assetManager.registerLocator("/",ClasspathLocator.class);
            assetManager.registerLoader(J3MLoader.class,"j3md");
            assetManager.registerLoader(GLSLLoader.class,"vert","frag","geom","tsctrl","tseval","glsl","glsllib");
            assetManager.registerLoader(OGGLoader.class,"ogg");

            Timer worldTimer = new NanoTimer();

            Camera worldCam = new Camera(settings.getWidth(), settings.getHeight());
            worldCam.setFrustumPerspective(45f, (float)worldCam.getWidth() / worldCam.getHeight(), 1f, 1000f);
            worldCam.setLocation(new Vector3f(0f, 0f, 100f));
            worldCam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
    
            Node worldRoot=new Node("World");

            Geometry geo=new Geometry("Test",new Sphere(100,100,2f));
            geo.setMaterial(new Material(assetManager,"Pipeline/Materials/BaseMat.j3md"));
            worldRoot.attachChild(geo);



            // initialization
            FrameBufferFactory fbFactory=new FrameBufferFactory();
            fbFactory.setDefaultFrameBuffer(settings.getWidth(),settings.getHeight(),null);
            PipelineRunner runner=new PipelineRunner();
            Jme3ContextCreator contextCreator=new Jme3ContextCreator(settings,Type.Display );


       
            PipelinePointerFactory pointers = new PipelinePointerFactory();
            pointers.setDefaultConstructor(Texture2D.class, (pipeline, pass, tx) -> {
                SmartTexture2D txb = SmartTexture.from(tx);
                txb.width(settings.getWidth());
                txb.height(settings.getHeight());
                txb.format(Format.RGB16F);
                txb.colorSpace(ColorSpace.Linear);
                txb.numSamples(1);
                return txb.get(pipeline, pass);
            });

     
            // LOGIC
            Pipeline logicPipeline = new Pipeline();
            logicPipeline
            .add(
                new UpdateTimerPass(worldTimer)
            )
            .add(
                new JmeInputHandlerPass(inputManager,contextCreator)
            )
            .add(
                new RunGameLogicPass(worldRoot)
            ).add(
                new UpdateGeometryPass(worldRoot)
            ).add(
                new JmeAudioRenderPass(contextCreator)
            );
            runner.addPipeline(logicPipeline);


            // GRAPHICS
            Pipeline renderingPipeline = new Pipeline();            
            renderingPipeline
                .add(
                    new Jme3ClearPass(contextCreator,fbFactory)
                        .useBackgroundColor(ColorRGBA.Red)
                        .clearColor(true)
                        .clearDepth(true)
                        .clearStencil(false)
                        .outColors( fbFactory.getDefaultTarget())
                )
                .add(
                    new GeometriesExtractorPass()
                        .useRootSpatial(worldRoot)
                        .useFunction(new GeometryBucketsExtractor(),worldCam)
                        .outLists(  pointers.newPointer(GeometryLists.class).rel().next("world-renderqueues") )
                )
                .add(
                    new Jme3GeometriesRenderPass(contextCreator,fbFactory)
                        .useTimer(worldTimer)
                        .useCamera(worldCam,false)
                        .useGeometryLists(pointers.newPointer(GeometryLists.class).rel().previous("world-renderqueues"))
                        .outColors( fbFactory.getDefaultTarget())
                )
                .add(
                    new Jme3FinalizeRender(contextCreator)
                );
            runner.addPipeline(renderingPipeline);
            
            contextCreator.getContext(true).addListener(new OnInitListener(()->{
                AudioNode audioSource = new AudioNode(assetManager, "Sound/Effects/Foot steps.ogg", DataType.Buffer);
                audioSource.setPositional(false);
                audioSource.setLooping(true);
                audioSource.play();
                worldRoot.attachChild(audioSource);

                // Node player=new Node("Player");
                // player.addControl(new Camerafol);
            }));

     

            // gameloop
            while(true){
                float tpf=worldTimer.getTimePerFrame();
                runner.run(tpf);
            }

           
    }

    @Override
    public void initialize() {

    }

    @Override
    public void reshape(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public void requestClose(boolean esc) {
        // TODO Auto-generated method stub

    }

    @Override
    public void gainFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public void loseFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleError(String errorMsg, Throwable t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void destroy() {

    }
}