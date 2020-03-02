package com.jme3.rendering.pipeline.params.literalpointers;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiFunction;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.RenderPipeline;
import com.jme3.rendering.pipeline.params.PipelinePointerResolver;
import com.jme3.rendering.pipeline.params.texture.SmartObject;

/**
 * PipelineParamsPool
 * 
 */
public class PipelineLiteralPointers implements PipelinePointerResolver{
    Map<Class,BiFunction> constructors=new HashMap<Class,BiFunction>();

    public <T> void setDefaultConstructor(Class<T> cl,  BiFunction<PipelinePass,T,T> f) {
        constructors.put(cl,f);
    }
    
    // public <T> T newPointer(Class<T> type, String name) {
    //     return newPointer(type,name,null);
    // }


    // public <T> T newPointer(Class<T> type, String name, Function<T,T> constructor) {
    //     T obj=null;
    //     try{
    //         obj=type.newInstance();
    //     }catch(InstantiationException | IllegalAccessException e){
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    //     SmartObject<T> sobj=SmartObject.from(obj);
    //     sobj.setPointerResolver(this);
    //     sobj.setPointer(name);
    //     sobj.setConstructor(constructor);
    //     return obj;
    // }

    public <T>  PointerBuilder<T> newPointer(Class<T> type) {
        return newPointer(type,null);
    }


    public <T> PointerBuilder<T> newPointer(Class<T> type,  BiFunction<PipelinePass,T,T> constructor) {
       
        return new PointerBuilder<T>(this,type,constructor);
    }


     public class PointerBuilder<T>{
   

        SmartObject<T>  sobj;
        T obj;

        public class AbsolutePointerBuilder{
            public T to(Object key){
                sobj.setPointer(key);
                return obj;
            }

      
        }

        public class RelativePointerBuilder{
            int dir=0;
            
            public T previous(Object key){
                dir=-1;
                sobj.setRelativePointer(dir, key);
                return obj;
            }

            public T next(Object key){
                dir=1;
                sobj.setRelativePointer(dir, key);
                return obj;
            }
            
            public T previous(Object key,int skip){
                dir=-skip;
                sobj.setRelativePointer(dir, key);
                return obj;
            }
   
        }

        public PointerBuilder(PipelinePointerResolver res,Class<T> type, BiFunction<PipelinePass,T,T> constructor){
            try{
                obj=type.newInstance();
            }catch(InstantiationException | IllegalAccessException e){
                e.printStackTrace();
            }
            sobj=SmartObject.from(obj);
            sobj.setConstructor(constructor);
            sobj.setPointerResolver(res);
        }

        public AbsolutePointerBuilder abs(){
            return new AbsolutePointerBuilder();
        }

        public RelativePointerBuilder rel(){
            return new RelativePointerBuilder();
        }
    }

   static final class PassStorage{
        Map<Object,Object> outputStorage=new HashMap<Object,Object>();  
    }

    final Map<Object,Object>  globalStorage=new HashMap<Object,Object>();  
    final Map<PipelinePass,PassStorage> passStorage=new WeakHashMap<PipelinePass,PassStorage>();  


    private PassStorage getPassStorage(PipelinePass pass){
        PassStorage st=passStorage.get(pass);
        if(st==null){
            st=new PassStorage();
            passStorage.put(pass,st);
        }
        return st;
    }
    public <T> T resolve(Class type, PipelinePass pass, T ref, BiFunction<PipelinePass,T,T> init) {
        SmartObject<T> sref=SmartObject.from(ref);
        if(!sref.isPointer())throw new RuntimeException("Cannot resolve a non pointer..");

        Map<Object,Object> storage=null;
        if(!sref.isRelativePointer()){
            storage=globalStorage;
        }else{
            if(sref.getRelativePointerDir()>0){
                PassStorage st=getPassStorage(pass);
                storage=st.outputStorage;
            }else {
                int n=-sref.getRelativePointerDir();
                RenderPipeline pp=pass.getPipeline();
                for(int i =pass.getId()-1;i>=0;i--){
                    PassStorage st=getPassStorage(pp.get(i));
                    if(st.outputStorage.containsKey(sref.getPointerAddr())){
                        n--;
                        if(n==0)   storage=st.outputStorage;
                    }
                }
                if(storage==null)            storage=globalStorage;
            }
        }

        T obj=(T)storage.get(sref.getPointerAddr());
        if(obj == null){
            try{
                obj=(T)type.newInstance();
            }catch(InstantiationException | IllegalAccessException e){
                e.printStackTrace();
            }
            BiFunction<PipelinePass,T,T> defaultC=constructors.get(type);
            if(defaultC != null) obj=defaultC.apply(pass,obj);
            if(init != null)   obj=init.apply(pass,obj);
            storage.put(sref.getPointerAddr(),obj);
        }
        return obj;
    

    }


}