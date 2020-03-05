package com.jme3.rendering.pipeline.params.literalpointers;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.PipelinePointerConstructor;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.params.PipelinePointerResolver;
import com.jme3.rendering.pipeline.params.smartobj.SmartObject;

/**
 * Pointers factory 
 * This is used to create and resolve pointers.
 * Note: pointers should not be manipulated directly unless first resolved with SmartObject.from(pointer).get(..)
 * @author Riccardo Balbo
 */
public class PipelinePointers implements PipelinePointerResolver{
    private final Map<Class,PipelinePointerConstructor> constructors=new HashMap<Class,PipelinePointerConstructor>();
    private final Map<Object,Object>  globalStorage=new HashMap<Object,Object>();  
    private final Map<PipelinePass,PassStorage> passStorage=new WeakHashMap<PipelinePass,PassStorage>();  

    private static final class PassStorage{    Map<Object,Object> outputStorage=new HashMap<Object,Object>();   }

    public <T> void setDefaultConstructor(Class<T> cl,  PipelinePointerConstructor<T> f) {
        constructors.put(cl,f);
    }
    

    public <T>  PointerBuilder<T> newPointer(Class<T> type) {
        return newPointer(type,null);
    }


    public <T> PointerBuilder<T> newPointer(Class<T> type,  PipelinePointerConstructor<T> constructor) {       
        return new PointerBuilder<T>(this,type,constructor);
    }


     public final class PointerBuilder<T>{
        SmartObject<T>  sobj;
        T obj;

        PointerBuilder(){

        }

        public final class AbsolutePointerBuilder{
            AbsolutePointerBuilder(){

            }
            public T to(Object key){
                sobj.setPointer(key);
                return obj;
            }      
        }

        public final class RelativePointerBuilder{
            int dir=0;
            RelativePointerBuilder(){

            }

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

        protected PointerBuilder(PipelinePointerResolver res,Class<T> type, PipelinePointerConstructor<T> constructor){
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



    private PassStorage getPassStorage(PipelinePass pass){
        PassStorage st=passStorage.get(pass);
        if(st==null){
            st=new PassStorage();
            passStorage.put(pass,st);
        }
        return st;
    }
    public <T> T resolve(Class type, Pipeline pipeline,PipelinePass pass, T ref, PipelinePointerConstructor<T> init) {
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
                Pipeline pp=pipeline;
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
            PipelinePointerConstructor<T>  defaultC=constructors.get(type);
            if(defaultC != null) obj=defaultC.construct(pipeline,pass,obj);
            if(init != null)   obj=init.construct(pipeline,pass,obj);
            storage.put(sref.getPointerAddr(),obj);
        }
        return obj;   
    }


}