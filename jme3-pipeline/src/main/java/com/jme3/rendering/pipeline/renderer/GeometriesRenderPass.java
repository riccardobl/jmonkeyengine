package com.jme3.rendering.pipeline.renderer;

import java.util.List;

import com.jme3.material.MatParam;
import com.jme3.material.MatParamOverride;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.scene.Geometry;
import com.jme3.shader.VarType;
import com.jme3.system.Timer;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;

public interface GeometriesRenderPass<T extends GeometriesRenderPass>  extends FrameBufferPass<T>{

      void beforeRender(Pipeline pipeline, float tpf, int w, int h,GeometryLists lists);
      void afterRender(Pipeline pipeline, float tpf, int w, int h,GeometryLists lists);
    
      void beforeGeometryListRender(Pipeline pipeline, float tpf, int w, int h,GeometryList list);
      void afterGeometryListRender(Pipeline pipeline, float tpf, int w, int h,GeometryList list);

      void beforeGeometryRender(Pipeline pipeline, float tpf, int w, int h,Geometry geo);
      void afterGeometryRender(Pipeline pipeline, float tpf, int w, int h,Geometry geo);



      void renderGeometry(Geometry geom) ;


    public  T  useParam(VarType type,String key,Object value);

    public  T  useParam(MatParam param);
        public  T overrideParam(String key,MatParamOverride override);

    
    public  T useTimer(Timer timer);
    public  T useCamera(Camera cam,boolean orthogonal);
    public  T useGeometryLists(GeometryLists lists);

    public  T forceTechnique(String tech);
    
    public  T forceRenderState(Object renderState);


}