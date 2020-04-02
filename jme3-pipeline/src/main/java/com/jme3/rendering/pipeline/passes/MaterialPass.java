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
import com.jme3.rendering.pipeline.renderer.gl.WorldParams;
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


    private void configureMat(){
        for(TechniqueDef t:mat.getMaterialDef().getTechniqueDefs(getTechnique())){
            if(t.getWorldBindings().size()>0){
                throw new RuntimeException("You cannot use WorldParameters in a MaterialPass");
            }
        }
    
        // enableGlobalParam("All",false);
        // for(MatParam pam:mat.getMaterialDef().getMaterialParams()){
        //     if(logger.isLoggable(java.util.logging.Level.  FINE  ))logger.log(java.util.logging.Level.FINE,
        //         "Try enable global param for {0}",pam
        //     );
        //     enableGlobalParam(pam.getName(),true);
        // }
    }

    @Override
    public T technique(String tech){
        super.technique(tech);
        configureMat();
        return (T)this;
    }


    @Override
    protected void onInput(Pipeline pipeline,Object key,Object value){
        if(key instanceof String)applyParam(pipeline,(String)key,value);
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

    @Override
    protected void onRender(Pipeline pipeline,float tpf,int w,int h,FrameBuffer outFb) {
        RenderManager renderManager= getRenderManager();                        
        renderManager.renderGeometry(screen);
    }

    @Override
    protected void afterRender(Pipeline pipeline, float tpf, int w, int h) {
        // TODO Auto-generated method stub

    }

    protected  Object onMatParamOutput(Pipeline pipeline,int key,Object v){
        return v;
    }
    protected  Object onMatParamInput(Pipeline pipeline,String name,Object v){
        return v;
    }

    protected void applyParam(Pipeline pipeline,String name, Object value) {
        if(mat.getMaterialDef().getMaterialParam(name)==null)return;
        
        if(value instanceof MutablePrimitive)value=((MutablePrimitive)value).getValue();           
        else if(value instanceof Camera){
            value=WorldParams.updateAndGet((Camera)value);
        }else if(value instanceof Timer){
            value=WorldParams.updateAndGet((Timer)value,getSpeed());
        } else if(value instanceof Geometry){
            value=WorldParams.updateAndGet(getCamera(),(Geometry)value);
        }

        if(value instanceof Struct)value=((Struct)value).get();

        if(logger.isLoggable(java.util.logging.Level.  FINEST  )){
            logger.log(java.util.logging.Level.FINEST,
            "Set param {0}={1}",new Object[]{name,value.getClass()}
            );
        }

        // if(value instanceof Integer){
        //     mat.setParam(name,VarType.Int,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof Float){
        //     mat.setParam(name,VarType.Float,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof Boolean){
        //     mat.setParam(name,VarType.Boolean,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof Texture){
        //     mat.setTexture(name,(Texture)onMatParamInput(pipeline,name,value));
        // }else if(value instanceof Vector2f){
        //     mat.setParam(name,VarType.Vector2,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof Vector3f){
        //     mat.setParam(name,VarType.Vector3,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof Vector4f){
        //     mat.setParam(name,VarType.Vector4,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof ColorRGBA){
        //     mat.setParam(name,VarType.Vector4,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof Matrix3f){
        //     mat.setParam(name,VarType.Matrix3,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof Matrix4f){
        //     mat.setParam(name,VarType.Matrix4,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof float[] || value instanceof Float[]){
        //     mat.setParam(name,VarType.FloatArray,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof int[] || value instanceof Integer[]){
        //     mat.setParam(name,VarType.IntArray,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof Vector2f[]){
        //     mat.setParam(name,VarType.Vector2Array,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof Vector3f[]){
        //     mat.setParam(name,VarType.Vector3Array,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof Vector4f[]){
        //     mat.setParam(name,VarType.Vector4Array,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof Matrix3f[]){
        //     mat.setParam(name,VarType.Matrix4Array,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof Matrix3f[]){
        //     mat.setParam(name,VarType.Matrix3Array,onMatParamInput(pipeline,name,value));
        // }else if(value instanceof BufferObject){
            mat.setParam(name, onMatParamInput(pipeline,name,value));
        // }
    }

    @Override
    protected void preAttach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void postAttach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void preDetach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void postDetach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeRun(Pipeline pipeline, float tpf) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void afterRun(Pipeline pipeline, float tpf) {
        // TODO Auto-generated method stub

    }




    
}