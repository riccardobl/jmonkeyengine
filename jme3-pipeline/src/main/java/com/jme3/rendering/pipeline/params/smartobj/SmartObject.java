package com.jme3.rendering.pipeline.params.smartobj;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.PipelinePointerConstructor;
import com.jme3.rendering.pipeline.PipelinePointerResolver;
import com.jme3.material.MatParam;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureArray;
import com.jme3.texture.TextureCubeMap;

/**
 * SmartObjects are special wrappers used to simplify the manipulation of
 * certain objects and to transparently resolve pointers. A SmartObject can be
 * created from any object by using SmartObject.from(obj). Caching, creation and
 * destructions are handled automatically.
 * 
 * @author Riccardo Balbo
 */
public class SmartObject<T> {

    private static final ObjectCacher<Object,SmartObject> objCache = new  ObjectCacher<Object,SmartObject>("SmartObjects");
  
    public static <T,R extends SmartObject> R from(T p) {    
        assert p!=null;
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

            
          
            sobj = (R) objCache.get(p);
            if (sobj == null) {
                // boolean hasDefaultConstr = false;
                // for (Constructor<?> constructor : p.getClass().getDeclaredConstructors()) {
                //     if (constructor.getParameterCount() == 0) {
                //         hasDefaultConstr = true;
                //         break;
                //     }
                // }
                if(p instanceof MatParam){
                    objCache.put(p, sobj = (R) new SmartMatParam((MatParam)p));
                }else{
                    objCache.put(p, sobj = (R) new SmartObject<T>(p)); 
                }
                // if (hasDefaultConstr) {
                //     objCache.put(p, sobj = (R) new SmartObject<T>(p));
                // } else {
                //     objCache.put(p, sobj = (R) new SmartObject<T>(p));

                // }
            }
        }
        return (R)sobj;
    }


    private PipelinePointerResolver resolver;
    private final WeakReference<T> value;
    private PipelinePointerConstructor<T> constructor;
    private Object pointerAddr;
    private int relativePointerDir=0;
    // private T overriddenValue;

    protected SmartObject(T value){
        this.value=new WeakReference<T>(value);
    }

    protected T prepareValue(T value){return value;}

    protected T getValue(Pipeline pipeline,PipelinePass pass){
        T value=this.value.get();
        if(pointerAddr!=null&&resolver!=null)   return prepareValue(getPointerResolver().resolve(value.getClass(),pipeline, pass, value,constructor));           
        return prepareValue(value);       
    }

    public T get(Pipeline pipeline,PipelinePass pass){
        T rv= getValue(pipeline, pass);
        // if(pointerAddr!=null&&resolver!=null)  {
        //     SmartObject<T> srv=SmartObject.from(rv);
        //     T ovv=srv.getOverridenValue();
        //    if(ovv!=null)System.out.println("Overriden "+this+" with "+ovv);

        //     return ovv==null?rv:ovv;
        // }else
         return rv;
    }

    // protected T getOverridenValue(){
    //     return overriddenValue;
    // }

    // public void setOverridenValue(Pipeline pipeline,PipelinePass pass,T value){
    //     if(pointerAddr!=null&&resolver!=null)  {
    //         T rv= getValue(pipeline, pass);
    //         SmartObject<T> srv=SmartObject.from(rv);
    //         srv.setOverridenValue(pipeline,pass,value);
    //     }else{
    //         System.out.println("Override "+this+" with "+value);
    //         overriddenValue=value;
    //     }
    // }





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