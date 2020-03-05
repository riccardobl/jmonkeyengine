package com.jme3.rendering.pipeline.test;

import java.util.Arrays;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.ViewPort;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.integration.PipelineMigrationUtils;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.integration.PipelineRunnerAppState;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.params.literalpointers.PipelinePointers;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture2D;
import com.jme3.rendering.pipeline.passes.FXAAPass;
import com.jme3.rendering.pipeline.passes.GradientFogPass;
import com.jme3.rendering.pipeline.passes.RenderViewPortPass;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Image.Format;

/**
 * TestApp
 */
public class TestApp extends SimpleApplication{
    public static void main(String[] args) {
        new TestApp().start();
    }


    void loadPipeline(){


        Format colorFormat=Format.RGB16F;
        Format depthFormat=Format.Depth;
        
        ViewPort mainVp=PipelineMigrationUtils.getMainViewPort( this);

        // initialization
        FrameBufferFactory fbFactory=PipelineMigrationUtils.getFrameBufferFactory(this);
        PipelinePointers pointers=PipelineMigrationUtils.getPointerFactory(this);
    

        Pipeline pipeline=new Pipeline();

        // runner
        PipelineRunnerAppState pipelineAppState=new PipelineRunnerAppState();
        pipelineAppState.addPipeline(pipeline);
        stateManager.attach(pipelineAppState);


        // pipeline plot
        pipeline.add(
            new RenderViewPortPass(renderManager, fbFactory)
            .viewPort(mainVp)
            .outColors(  pointers.newPointer(Texture2D.class).rel().next("scene"))
            .outDepth(
                pointers.newPointer(Texture2D.class,
                (pp,pass,tx)->{
                    SmartTexture2D txb=SmartTexture.from(tx);
                    txb.format(depthFormat);
                    return txb.get(pp,pass);
                }
            ).rel().next("depth")            
        ));

        pipeline.add(
            new GradientFogPass(renderManager,assetManager, fbFactory)
            .sceneCamera(mainVp.getCamera())
            .inColor(pointers.newPointer(Texture2D.class).rel().previous("scene"))
            .inDepth(pointers.newPointer(Texture2D.class).rel().previous("depth"))
            .outColor(pointers.newPointer(Texture2D.class).rel().next("scene"))
        );

        pipeline.add(
            new FXAAPass(renderManager,assetManager, fbFactory)
            .inColor(pointers.newPointer(Texture2D.class).rel().previous("scene"))
            .outColor(fbFactory.getDefaultTarget())
        );

    }

	@Override
	public void simpleInitApp() {
        loadPipeline();

        /** Load a teapot model (OBJ file from test-data) */
        Spatial teapot = assetManager.loadModel("Models/Teapot/Teapot.obj");
        Material mat_default = new Material( assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        teapot.setMaterial(mat_default);
        rootNode.attachChild(teapot);

        /** Create a wall (Box with material and texture from test-data) */
        Box box = new Box(2.5f, 2.5f, 1.0f);
        Spatial wall = new Geometry("Box", box );
        Material mat_brick = new Material( assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_brick.setTexture("ColorMap", assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg"));
        wall.setMaterial(mat_brick);
        wall.setLocalTranslation(2.0f,-2.5f,0.0f);
        rootNode.attachChild(wall);

        /** Display a line of text (default font from test-data) */
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText helloText = new BitmapText(guiFont, false);
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setText("Hello World");
        helloText.setLocalTranslation(300, helloText.getLineHeight(), 0);
        guiNode.attachChild(helloText);

        /** Load a Ninja model (OgreXML + material + texture from test_data) */
        Spatial ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja.scale(0.05f, 0.05f, 0.05f);
        ninja.rotate(0.0f, -3.0f, 0.0f);
        ninja.setLocalTranslation(0.0f, -5.0f, -2.0f);
        rootNode.attachChild(ninja);
        /** You must add a light to make the model visible */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f).normalizeLocal());
        rootNode.addLight(sun);

	}

    
}