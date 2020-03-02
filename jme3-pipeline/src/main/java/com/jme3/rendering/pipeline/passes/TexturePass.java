package com.jme3.rendering.pipeline.passes;

import com.jme3.asset.AssetManager;
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
import com.jme3.shader.VarType;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;

/**
 * TexturePass
 */
public class TexturePass extends RenderPass{
    protected final Material mat;


	protected Picture screen;
    protected Camera cam;
    
    public TexturePass(RenderManager renderManager,AssetManager assetManager,FrameBufferFactory fbFactory,String matDef){
        super(renderManager, fbFactory);
        mat=new Material(assetManager,matDef);
        
        checkWp();

        this.screen = new Picture("TexturePass"+hashCode());
        this.screen.setWidth(1);
        this.screen.setHeight(1);
        this.screen.setMaterial(mat);
        this.cam=new Camera(128,128);
     
    }


    protected void checkWp(){
        for(TechniqueDef t:mat.getMaterialDef().getTechniqueDefs(getTechnique())){
            if(t.getWorldBindings().size()>0){
                throw new RuntimeException("You cannot use WorldParameters in TexturePass");
            }
        }
    }

    @Override
    public TexturePass technique(String tech){
        super.technique(tech);
        checkWp();
        return this;
    }




    @Override
    protected void onRender(float tpf,int w,int h,FrameBuffer outFb) {
        RenderManager renderManager= getRenderManager();
              
        cam.resize(w, h, false);
        cam.setViewPort(0, 1, 0, 1);
        cam.update();

        renderManager.setCamera(cam, false);
        renderManager.getRenderer().setFrameBuffer(outFb);
        renderManager.getRenderer().clearBuffers(isClearColor(),isClearDepth(),isClearStencil());

        renderManager.renderGeometry(screen);
    }

    protected void onInput(Object key,Object value){
        if(key instanceof String)applyParam((String)key,value);
    }

    protected void applyParam(String name, Object value) {
        if(value instanceof Integer){
            mat.setParam(name,VarType.Int,value);
        }else if(value instanceof Float){
            mat.setParam(name,VarType.Float,value);
        }else if(value instanceof Boolean){
            mat.setParam(name,VarType.Boolean,value);
        }else if(value instanceof Texture){
            mat.setTexture(name,(Texture)value);
        }else if(value instanceof Vector2f){
            mat.setParam(name,VarType.Vector2,value);
        }else if(value instanceof Vector3f){
            mat.setParam(name,VarType.Vector3,value);
        }else if(value instanceof Vector4f){
            mat.setParam(name,VarType.Vector4,value);
        }else if(value instanceof ColorRGBA){
            mat.setParam(name,VarType.Vector4,value);
        }else if(value instanceof Matrix3f){
            mat.setParam(name,VarType.Matrix3,value);
        }else if(value instanceof Matrix4f){
            mat.setParam(name,VarType.Matrix4,value);
        }else if(value instanceof float[] || value instanceof Float[]){
            mat.setParam(name,VarType.FloatArray,value);
        }else if(value instanceof int[] || value instanceof Integer[]){
            mat.setParam(name,VarType.IntArray,value);
        }else if(value instanceof Vector2f[]){
            mat.setParam(name,VarType.Vector2Array,value);
        }else if(value instanceof Vector3f[]){
            mat.setParam(name,VarType.Vector3Array,value);
        }else if(value instanceof Vector4f[]){
            mat.setParam(name,VarType.Vector4Array,value);
        }else if(value instanceof Matrix3f[]){
            mat.setParam(name,VarType.Matrix4Array,value);
        }else if(value instanceof Matrix3f[]){
            mat.setParam(name,VarType.Matrix3Array,value);
        }else{
            // throw new UnsupportedDataTypeException();
        }


    }


    
}