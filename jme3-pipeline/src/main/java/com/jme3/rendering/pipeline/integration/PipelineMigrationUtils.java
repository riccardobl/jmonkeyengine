package com.jme3.rendering.pipeline.integration;

import java.util.HashMap;
import java.util.Map;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.renderer.ViewPort;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.params.literalpointers.PipelinePointers;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture2D;

import com.jme3.rendering.pipeline.test.TestApp;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.ColorSpace;

/**
 * Migrate from jme core renderer to scriptable pipeline based renderer.
 * @author Riccardo Balbo
 */
public class PipelineMigrationUtils {
    private static Map<Application,FrameBufferFactory> fbFactoryCache=new HashMap<Application,FrameBufferFactory>();
    private  static Map<ViewPort,ViewPort> vpCache=new HashMap<ViewPort,ViewPort>();

    public static FrameBufferFactory getFrameBufferFactory(Application app){
        FrameBufferFactory fbFactory=fbFactoryCache.get(app);
        if(fbFactory==null){
            fbFactory=new FrameBufferFactory();
            fbFactoryCache.put(app,fbFactory);
        }
        fbFactory.setDefaultFrameBuffer(app.getViewPort().getCamera().getWidth(), app.getViewPort().getCamera().getHeight(),app.getViewPort().getOutputFrameBuffer());
        return fbFactory;
    }



    public static ViewPort getMainViewPort(Application app){
        ViewPort defaultVp=app.getViewPort();
        ViewPort vp=vpCache.get(defaultVp);
        if(vp==null){
            vp=new ViewPort("MainView",defaultVp.getCamera());
            vp.setBackgroundColor(defaultVp.getBackgroundColor());
            vp.setClearColor(defaultVp.isClearColor());
            vp.setClearDepth(defaultVp.isClearDepth());
            vp.setClearStencil(defaultVp.isClearStencil());
            vp.setOutputFrameBuffer(defaultVp.getOutputFrameBuffer());
            for(Spatial s:defaultVp.getScenes())vp.attachScene(s);
            defaultVp.setEnabled(false);
            vpCache.put(defaultVp,vp);
        }
        return vp;
    }

	public static PipelinePointers getPointerFactory(Application app) {

        ViewPort defaultVp=app.getViewPort();
        int width=defaultVp.getCamera().getWidth();
        int height=defaultVp.getCamera().getHeight();

        Format format=Format.RGB111110F;
        ColorSpace colorSpace=ColorSpace.Linear;
        int numSamples=1;

        PipelinePointers pointers=new PipelinePointers();
        pointers.setDefaultConstructor(Texture2D.class,
            (pipeline,pass,tx)->{
                SmartTexture2D txb=SmartTexture.from(tx);
                txb.width(width);
                txb.height(height);
                txb.format(format);
                txb.colorSpace(colorSpace);
                txb.numSamples(numSamples);              
                return txb.get(pipeline,pass);
            }
        );
		return pointers;
	}

    

}