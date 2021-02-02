package com.jme3.rendering.pipeline.passes;

import com.jme3.asset.AssetManager;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.TechniqueDef;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.WorldParamsUtil;
import com.jme3.rendering.pipeline.WorldParamsUtil.WorldParam;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.params.primitives.MutablePrimitive;
import com.jme3.scene.Geometry;
import com.jme3.shader.BufferObject;
import com.jme3.shader.VarType;
import com.jme3.system.Timer;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import com.jme3.util.struct.Struct;

/**
 * An abstract pass that execute a shader on an 1x1 quad.
 * @author Riccardo Balbo
 */
public class MaterialPass<T extends MaterialPass> extends RenderPass<T>{
    private  static final java.util.logging.Logger logger =  java.util.logging.Logger.getLogger( MaterialPass.class.getName());
    private final Material mat;
	private Picture screen;

    protected MaterialPass(RenderManager renderManager, 
    FrameBufferFactory fbFactory, Timer timer,
    AssetManager assetManager,String matDef){
        super(renderManager, fbFactory, new Camera(128,128), timer,false);
        mat=new Material(assetManager,matDef);
        
        configureMat();

        this.screen = new Picture("MaterialPass"+hashCode());
        this.screen.setWidth(1);
        this.screen.setHeight(1);
        this.screen.setMaterial(mat);
        
     
    }






    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {

        if (key instanceof Number) {
            int keyn = ((Number) key).intValue();
            if (keyn == RENDER_OUT_DEPTH) {
                Texture tx = (Texture) value;
         
                int outWidth = tx.getImage().getWidth();
                int outHeight = tx.getImage().getHeight();
                WorldParamsUtil.updateResolution(this, outWidth,outHeight, WorldParamsUtil.WorldParam.Resolution.name(), WorldParamsUtil.WorldParam.ResolutionInverse.name());
  
                value=onMatParamOutput(pipeline, keyn, tx);
            } else if (keyn >= RENDER_OUT_COLOR) {
                Texture tx = (Texture) value;

                int outWidth = tx.getImage().getWidth();
                int outHeight = tx.getImage().getHeight();
                WorldParamsUtil.updateResolution(this, outWidth,outHeight, WorldParamsUtil.WorldParam.Resolution.name(), WorldParamsUtil.WorldParam.ResolutionInverse.name());
             
                value=onMatParamOutput(pipeline, keyn, tx);
            }
        }

        super.onOutput(pipeline, key, value);
    }




    @Override
    protected void beforeRender(Pipeline pipeline,float tpf,int w,int h){
        Camera cam=getCamera();
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











    
}