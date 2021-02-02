package com.jme3.rendering.pipeline.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.plugins.OGGLoader;
import com.jme3.bullet.BulletPhysicsPass;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
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
import com.jme3.rendering.pipeline.passes.DeferredPBRPass;
import com.jme3.rendering.pipeline.passes.FXAAPass;
import com.jme3.rendering.pipeline.passes.GradientFogPass;
import com.jme3.rendering.pipeline.passes.MSAASolverPass;
import com.jme3.rendering.pipeline.passes.PrintPass;
import com.jme3.rendering.pipeline.passes.RenderViewPortPass;
import com.jme3.rendering.pipeline.logic.InputEventQueueExecutorPass;
import com.jme3.rendering.pipeline.logic.LogicExecutorPass;
import com.jme3.rendering.pipeline.logic.ControlExtractorPass;
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
import com.jme3.rendering.pipeline.jme3.controls.LegacyControlExecutorPass;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.event.InputEvent;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.plugins.J3MLoader;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.control.InputHandlerControl;
import com.jme3.scene.control.LogicControl;
import com.jme3.scene.control.RenderControl;
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
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.SafeArrayList;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;

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
            assetManager.registerLoader(J3MLoader.class,"j3m");
            assetManager.registerLoader(GLSLLoader.class,"vert","frag","geom","tsctrl","tseval","glsl","glsllib");
            assetManager.registerLoader(OGGLoader.class,"ogg");
            assetManager.registerLoader(AWTLoader.class,"png");
            assetManager.registerLoader(com.jme3.export.binary.BinaryImporter.class,"j3o");

            Timer worldTimer = new NanoTimer();

            Camera worldCam = new Camera(settings.getWidth(), settings.getHeight());
            worldCam.setFrustumPerspective(45f, (float)worldCam.getWidth() / worldCam.getHeight(), 1f, 1000f);
            worldCam.setLocation(new Vector3f(0f, 0f, 100f));
            worldCam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
    
            Node worldRoot=new Node("World");




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
                return txb.get(pipeline, pass);
            });


          

            Collection<Control> gameLogicQueue=new SafeArrayList<Control>(Control.class);
            ConcurrentLinkedQueue<Control> physicsLogic=new ConcurrentLinkedQueue<Control>();
            Collection<Control> inputHandlers=new SafeArrayList<Control>(Control.class);
            Collection<InputEvent> inputEventsQueue=new SafeArrayList<InputEvent>(InputEvent.class);
            Collection<Control> jmeLegacyControls=new SafeArrayList<Control>(Control.class);


            // INPUTS
            Pipeline inputPipeline = new Pipeline(pointers);
            inputPipeline
            .add(
                new JmeInputHandlerPass(inputManager,contextCreator, inputEventsQueue)
            );
            runner.addPipeline(inputPipeline);

            // LOGIC
            Pipeline logicPipeline = new Pipeline(pointers);
            logicPipeline
            .add(
                new UpdateTimerPass(worldTimer)
            )     
            .add(new ControlExtractorPass(worldRoot,jmeLegacyControls,c->(c instanceof AbstractControl)).setName("Extract jme controls (legacy)"))
            .add(
                new ControlExtractorPass(worldRoot,gameLogicQueue,c->(c instanceof LogicControl)).setName("Extract Game Logic")
            )
            // .add(
            //     new ControlExtractorPass(worldRoot,gameLogicQueue,c->(c instanceof RenderControl)).name("Extract Render Logic")
            // )
            .add(
                new ControlExtractorPass(worldRoot,physicsLogic,c->(c instanceof PhysicsControl)).setName("Extract Physics")
            )
            .add(
                new ControlExtractorPass(worldRoot,inputHandlers,c->(c instanceof InputHandlerControl)).setName("Extract Input Handlers")
            )            
            .add(
                new LegacyControlExecutorPass(contextCreator,jmeLegacyControls).setName("Execute jme3 Controls Logic (legacy)")
            )
            .add( 
                new InputEventQueueExecutorPass(inputEventsQueue,inputHandlers).setName("Execute Input handlers")
            )
            .add(
                new LogicExecutorPass(gameLogicQueue).setName("Execute Controls Logic")
            )
            .add(
                new UpdateGeometryPass(worldRoot).setName("Update Geometry data")
            ).add(
                new JmeAudioRenderPass(contextCreator).setName("Render jme3 Audio")
            );
            runner.addPipeline(logicPipeline);

            // Physics
            Pipeline physicsPipeline=new Pipeline(pointers);
            physicsPipeline.add(
                  new BulletPhysicsPass(physicsLogic).setName("Execute Physics Logic")
             );
            runner.addPipeline(physicsPipeline);

            // GRAPHICS
            Pipeline renderingPipeline = new Pipeline(pointers);            
            renderingPipeline
          
                .add(
                    new com.jme3.rendering.pipeline.logic.CullPass(worldCam,worldRoot)
                )
                .add(
                    new GeometriesExtractorPass()
                        .useRootSpatial(worldRoot)
                        .useFunction(new GeometryBucketsExtractor(),worldCam)
                        .outLists(  pointers.newPointer(GeometryLists.class).abs().to("world-renderqueues") ).setName("Extract Geometries")
                )
                .add(
                    contextCreator.newClearPass( fbFactory) 
                        .useBackgroundColor(ColorRGBA.BlackNoAlpha)
                        .clearColor(true)
                        .clearDepth(true)
                        .clearStencil(false)
                        .outColors( 
                            
                    pointers.newPointer(Texture2D.class,(pp,pass,tx)->{
                            Texture2D tx2d=pointers.getDefaultConstructor(Texture2D.class).construct(pp, pass, tx);
                            SmartTexture2D txb=SmartTexture.from(tx2d);
                            // txb.numSamples(4);
                            return txb.get(pp,pass);

                        }).abs().to("data1"),
                        pointers.newPointer(Texture2D.class,(pp,pass,tx)->{
                            Texture2D tx2d=pointers.getDefaultConstructor(Texture2D.class).construct(pp, pass, tx);
                            SmartTexture2D txb=SmartTexture.from(tx2d);
                            // txb.numSamples(4);
                            return txb.get(pp,pass);

                        }).abs().to("data2"),
                        pointers.newPointer(Texture2D.class,(pp,pass,tx)->{
                            Texture2D tx2d=pointers.getDefaultConstructor(Texture2D.class).construct(pp, pass, tx);
                            SmartTexture2D txb=SmartTexture.from(tx2d);
                            // txb.numSamples(4);
                            return txb.get(pp,pass);

                        }).abs().to("color")
                        
                        
                        )
                        .outDepth(pointers.newPointer(Texture2D.class, (pp,pass,tx)->{
                            Texture2D tx2d=pointers.getDefaultConstructor(Texture2D.class).construct(pp, pass, tx);
                            SmartTexture2D txb=SmartTexture.from(tx2d);
                            // txb.numSamples(4);
                            txb.format(Format.Depth);
                            return txb.get(pp,pass);
                        }
                ).abs().to("depth"))
                )
                .add(
                    contextCreator.newGeometriesRenderPass(fbFactory)
                        .useTimer(worldTimer)
                        .useCamera(worldCam,false)
                        .useGeometryLists(pointers.newPointer(GeometryLists.class).abs().to("world-renderqueues"))
                        .outColors( 
                            pointers.newPointer(Texture2D.class).abs().to("data1"),
                            pointers.newPointer(Texture2D.class).abs().to("data2")
                            
                        )
                        .outDepth(pointers.newPointer(Texture2D.class).abs().to("depth"))
                )
                .add(
                    contextCreator.newClearPass( fbFactory) 
                        .useBackgroundColor(ColorRGBA.Black)
                        .clearColor(true)
                        .clearDepth(true)
                        .clearStencil(true)
                        .outColors( fbFactory.getDefaultTarget())
                        .outDepth( fbFactory.getDefaultTarget())
                )
                // .add(
                //     new MSAASolverPass(contextCreator, fbFactory, assetManager)
                //     .inColor(MSAASolverMethod.RESOLVE_METHOD_AVERAGE, pointers.newPointer(Texture2D.class).rel().previous("color"))
                //     .inDepth(MSAASolverMethod.RESOLVE_METHOD_AVERAGE, pointers.newPointer(Texture2D.class).rel().previous("depth"))
                //     .outColor( 
                //         pointers.newPointer(Texture2D.class).rel().next("color")
                //     )
                //     .outDepth( 
                //         pointers.newPointer(Texture2D.class, (pp,pass,tx)->{
                //             SmartTexture2D txb=SmartTexture.from(tx);
                //             txb.format(Format.Depth);

                //             return txb.get(pp,pass);
                //         }).rel().next("depth")
                //     )
                // )
                .add(
                   new DeferredPBRPass(contextCreator,fbFactory,assetManager)
                   .inData(
                        pointers.newPointer(Texture2D.class).abs().to("data1"),
                        pointers.newPointer(Texture2D.class).abs().to("data2")
                   )
                   .inDepth(pointers.newPointer(Texture2D.class).abs().to("depth"))
                   .outColor(  pointers.newPointer(Texture2D.class,(pp,pass,tx)->{
                    Texture2D tx2d=pointers.getDefaultConstructor(Texture2D.class).construct(pp, pass, tx);
                    SmartTexture2D txb=SmartTexture.from(tx2d);
                    // txb.numSamples(4);
                    return txb.get(pp,pass);

                }).abs().to("color"))
                )
                .add(
                    new FXAAPass(contextCreator,fbFactory,assetManager)
                    .inColor(
                        pointers.newPointer(Texture2D.class).rel().previous("color")
                    )
                    .outColor( 
                        fbFactory.getDefaultTarget()
                    )
                )
                .add(
                    contextCreator.newFinalizeRenderPass(fbFactory)
                );
            runner.addPipeline(renderingPipeline);
            
            contextCreator.getContext(true).addListener(new OnInitListener(()->{
                Geometry model = (Geometry) assetManager.loadModel("Models/Tank/tank.j3o");
                MikktspaceTangentGenerator.generate(model);
                worldRoot.attachChild(model);

                Material pbrMat = assetManager.loadMaterial("Models/Tank/tank.j3m");
                model.setMaterial(pbrMat);
        
                // Geometry geo=new Geometry("Test",new Sphere(100,100,2f));
                // geo.setMaterial(new Material(assetManager,"Pipeline/Materials/BaseMat.j3md"));
                // worldRoot.attachChild(geo);
                // RigidBodyControl rb=new RigidBodyControl(10f);
                // geo.addControl(rb);
                // geo.addControl(new MoveALittleControl());
                // geo.addControl(new MoveWithKeysControl());

                ForwardToDeferredPBR.migrate(assetManager,worldRoot);
                
                AudioNode audioSource = new AudioNode(assetManager, "Sound/Effects/Foot steps.ogg", DataType.Buffer);
                audioSource.setPositional(false);
                audioSource.setLooping(true);
                audioSource.play();
                worldRoot.attachChild(audioSource);

                // Node player=new Node("Player");
                // player.addControl(new Camerafol);
            }));

     

            // new Thread(()->{
            // gameloop
            while(true){
                float tpf=worldTimer.getTimePerFrame();
                runner.run(tpf);
            }
        // }).start();

           
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