package com.jme3.rendering.pipeline.jme3.renderer;

import java.util.Arrays;
import java.util.Collection;

import com.jme3.material.Material;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.GeometryComparator;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.renderer.queue.OpaqueComparator;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.jme3.context.Jme3ContextCreator;
import com.jme3.rendering.pipeline.renderer.GeometryLists;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;

public class Jme3SurfaceRenderPass extends Jme3GeometriesRenderPass {
    private Picture screen;
    private Camera cam;

    public Jme3SurfaceRenderPass(Material mat,Jme3ContextCreator contextFactory, FrameBufferFactory fbFactory) {
        super(contextFactory, fbFactory);
        this.screen = new Picture("SurfacePass");
        this.screen.setWidth(1);
        this.screen.setHeight(1);
        this.screen.setMaterial(mat);
        GeometryList geoLists=new GeometryList(new GeometryComparator(){
            @Override public int compare(Geometry o1, Geometry o2) {return 0;  }
            @Override public void setCamera(Camera cam) { }            
        });        
        geoLists.add(this.screen);
        this.useGeometryLists(new GeometryLists(Arrays.asList(geoLists)));
        cam=new Camera(1024,768);
        useCamera(cam, false);
    }

    private void resizeCamera(Texture fit){
        int w;
        int h;
        w=fit.getImage().getWidth();
        h=fit.getImage().getHeight();
        if(cam.getWidth()!=w
        ||cam.getHeight()!=h
        ||cam.getViewPortLeft()!=0
        || cam.getViewPortRight()!=1
        ||cam.getViewPortBottom()!=0
        ||cam.getViewPortTop()!=1){
            cam.resize(w, h, false);
            cam.setViewPort(0, 1, 0, 1);
            cam.update();
        }
    }
    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {
        if (value instanceof Texture) {
            resizeCamera((Texture)value);
        }
        super.onOutput(pipeline, key, value);
    }

    @Override
    public PipelinePass setName(String name){
        this.screen.setName(name);
        return         super.setName(name);
      
    }

   

   
}
