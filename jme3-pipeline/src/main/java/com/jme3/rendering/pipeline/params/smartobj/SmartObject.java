package com.jme3.rendering.pipeline.params.smartobj;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.PipelinePointerConstructor;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.params.PipelinePointerResolver;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureArray;
import com.jme3.texture.TextureCubeMap;

/**
 * SmartObjects are special wrappers used to simplify the manipulation of certain objects and to transparently resolve pointers.
 * A SmartObject can be created from any object by using SmartObject.from(obj). Caching, creation and destructions are handled automatically.
 * @author Riccardo Balbo
 */
public class SmartObject<T> {

    private static final Map<Object,SmartObject> objCache=new WeakHashMap<Object,SmartObject>();
    public static <T,R extends SmartObject> R from(T p) {    
        R sobj;
        if(p instanceof Texture2D ){
            sobj=(R)SmartTexture.from((Texture2D)p);
        }else if(p instanceof Texture3D ){
            sobj=(R)SmartTexture.from((Texture3D)p);
        }else if(p instanceof TextureCubeMap ){
            sobj=(R)SmartTexture.from((TextureCubeMap)p);
        }else if(p instanceof TextureArray ){
            sobj= (R)SmartTexture.from((TextureArray)p);
        }else{
            sobj=(R)objCache.get(p);
            if(sobj == null)objCache.put(p,sobj=(R)new SmartObject<T>(p));
        }      
        return (R)sobj;
    }


    private PipelinePointerResolver resolver;
    private T value;
    private T resolved;
    private PipelinePointerConstructor<T> constructor;
    private Object pointerAddr;
    private int relativePointerDir=0;

    protected SmartObject(T value){
        this.value=value;
    }

    protected T prepareValue(T value){return value;}
    protected T releaseValue(T value){return value;}

    public T get(Pipeline pipeline,PipelinePass pass){
        if(pointerAddr!=null&&resolver!=null){
            return prepareValue(resolved=getPointerResolver().resolve(value.getClass(),pipeline, pass, value,constructor));           
        }
        return prepareValue(value);       
    }


    public T release(){
        if(pointerAddr!=null&&resolver!=null){
            return releaseValue(resolved);
        }else{
            return releaseValue(value);
        }
    }


    public void setPointer(Object addr){
        pointerAddr=addr;
        relativePointerDir=0;
    }

    public void setRelativePointer(int dir,Object addr){
        pointerAddr=addr;
        relativePointerDir=dir;
    }

    public boolean isRelativePointer(){
        return relativePointerDir!=0;
    }

    public int getRelativePointerDir(){
        return relativePointerDir;
    }

    public boolean isPointer(){
        return pointerAddr!=null;
    }

    public Object getPointerAddr(){
        return pointerAddr;
    }

    public void setConstructor( PipelinePointerConstructor<T>  c){
        constructor=c;
    }

    public void setPointerResolver(PipelinePointerResolver res){
        this.resolver=res;
    }

    public PipelinePointerResolver getPointerResolver(){
        return this.resolver;
    }

}