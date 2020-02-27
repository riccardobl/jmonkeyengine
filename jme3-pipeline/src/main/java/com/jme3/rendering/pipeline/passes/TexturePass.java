package com.jme3.rendering.pipeline.passes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.UnsupportedDataTypeException;

import com.jme3.asset.AssetManager;
import com.jme3.material.MatParamOverride;
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
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.params.PipelineParam;
import com.jme3.rendering.pipeline.params.PipelineParamPointer;
import com.jme3.shader.VarType;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureArray;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.ColorSpace;
import com.jme3.ui.Picture;

/**
 * TexturePass
 */
public class TexturePass extends RenderPass{
    protected final Material mat;
    protected final Map<String,PipelineParam> params=new LinkedHashMap<String,PipelineParam>();
    protected List<PipelineParam<Texture>> outColors=new ArrayList<PipelineParam<Texture>>();;
    protected PipelineParam<Texture> outDepth;
    protected MatParamOverride mrtParam;
	protected Picture screen;
    protected Camera cam;
    protected String useTechnique=TechniqueDef.DEFAULT_TECHNIQUE_NAME;
    
    public TexturePass(RenderManager renderManager,AssetManager assetManager,FrameBufferFactory fbFactory,String matDef){
        super(renderManager, fbFactory);
        mat=new Material(assetManager,matDef);
        
        for(TechniqueDef t:mat.getMaterialDef().getTechniqueDefs(useTechnique)){
            if(t.getWorldBindings().size()>0){
                throw new RuntimeException("You cannot use WorldParameters in TexturePass");
            }
        }

        this.screen = new Picture("TexturePass"+hashCode());
        this.screen.setWidth(1);
        this.screen.setHeight(1);
        this.screen.setMaterial(mat);
        this.cam=new Camera(128,128);
     
    }

    public TexturePass technique(String name){
        if(name==null)name=TechniqueDef.DEFAULT_TECHNIQUE_NAME;
        useTechnique=name;
        return this;
    }

 


    public TexturePass inParam(String name, PipelineParamPointer<?> value) {
        if(value == null) params.remove(name);
        else{
            PipelineParam<?> param=value.get(this);
            params.put(name,param);
        }
        return this;
    }

    public TexturePass inParam(String name, PipelineParam<?> param) {
        if(param == null) params.remove(name);
        else{
            params.put(name,param);
        }
        return this;
    }


    public TexturePass outColors(List<PipelineParamPointer<Texture>> out) {
        this.outColors.clear();
        for(PipelineParamPointer<Texture> pp:out)this.outColors.add(pp.get(this));        
        invalidateFrameBuffer();
        return this;
    }


    public TexturePass outDepth(PipelineParamPointer<Texture> out) {
        this.outDepth=out.get(this);
        invalidateFrameBuffer();
        return this;
    }


    @Override
    public void run(float tpf) {
        RenderManager renderManager= getRenderManager();

        if(mrtParam==null) mrtParam=new MatParamOverride(VarType.Boolean,"Mrt",false);
        mrtParam.setValue(this.outColors.size()>1);

        
        for(Entry<String,PipelineParam> e:params.entrySet()){
            applyParam(e.getKey(),e.getValue().getValue());
        }   

        FrameBuffer outFb=getFrameBuffer(outColors,outDepth);

        int w=getFbFactory().getFrameBufferWidth(outFb);
        int h=getFbFactory().getFrameBufferHeight(outFb);
        cam.resize(w, h, false);
        cam.setViewPort(0, 1, 0, 1);
        cam.update();
        renderManager.setCamera(cam, false);
        renderManager.getRenderer().setFrameBuffer(outFb);
        renderManager.getRenderer().clearBuffers(isClearColor(),isClearDepth(),isClearStencil());

        String otch=renderManager.getForcedTechnique();
        renderManager.setForcedTechnique(useTechnique);

        renderManager.addForcedMatParam(mrtParam);

        renderManager.renderGeometry(screen);

        renderManager.removeForcedMatParam(mrtParam);    

        renderManager.setForcedTechnique(otch);


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