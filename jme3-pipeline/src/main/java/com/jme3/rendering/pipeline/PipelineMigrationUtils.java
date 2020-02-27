package com.jme3.rendering.pipeline;

import java.util.HashMap;
import java.util.Map;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.renderer.ViewPort;
import com.jme3.rendering.pipeline.params.literalpointers.PipelineLiteralPointers;
import com.jme3.rendering.pipeline.test.TestApp;
import com.jme3.scene.Spatial;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.ColorSpace;

/**
 * PipelineMigrationUtils
 */
public class PipelineMigrationUtils {
    static Map<Application,FrameBufferFactory> fbFactoryCache=new HashMap<Application,FrameBufferFactory>();

    public static FrameBufferFactory getFrameBufferFactory(Application app){
        FrameBufferFactory fbFactory=fbFactoryCache.get(app);
        if(fbFactory==null){
            fbFactory=new FrameBufferFactory();
            fbFactoryCache.put(app,fbFactory);
        }
        fbFactory.setDefaultFrameBuffer(app.getViewPort().getCamera().getWidth(), app.getViewPort().getCamera().getHeight(),app.getViewPort().getOutputFrameBuffer());
        return fbFactory;
    }


    static Map<ViewPort,ViewPort> vpCache=new HashMap<ViewPort,ViewPort>();

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

	public static PipelineLiteralPointers getLiteralPointers(Application app) {
        ViewPort defaultVp=app.getViewPort();

        int width=defaultVp.getCamera().getWidth();
        int height=defaultVp.getCamera().getHeight();

        Format format=Format.RGB111110F;
        ColorSpace colorSpace=ColorSpace.Linear;
        int numSamples=1;

        PipelineLiteralPointers pointers=new PipelineLiteralPointers();
        pointers.setDefaultTextureInitializer(
            (tx)->{
                tx.width(width);
                tx.height(height);
                tx.format(format);
                tx.colorSpace(colorSpace);
                tx.numSamples(numSamples);                
            }
        );
		return pointers;
	}


}