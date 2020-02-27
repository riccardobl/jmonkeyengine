package com.jme3.rendering.pipeline.params.literalpointers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.params.PipelineParam;
import com.jme3.rendering.pipeline.params.PipelinePointerResolver;
import com.jme3.rendering.pipeline.params.PipelineTextureParam;

import com.jme3.texture.Texture;

/**
 * PipelineParamsPool
 * 
 */
public class PipelineLiteralPointers implements PipelinePointerResolver{
    Map<String,PipelineParam> storage=new HashMap<String,PipelineParam>();
    Consumer<PipelineTextureParam> txInitializer=null;

    public void setDefaultTextureInitializer( Consumer<PipelineTextureParam>  f){
        txInitializer=f;
    }


    public  <T> PipelineParamPointerImpl<T> newPointer(Class<T> type,String name) {
        return new PipelineParamPointerImpl<T>(this,name);
    }

    public PipelineTextureParamPointerImpl newTexturePointer(String name) {
        return new PipelineTextureParamPointerImpl(txInitializer,this,name);
    }

    public  <T> PipelineParamPointerImpl<T> newPointerFrom(Class<T> type,PipelineParam<T> p) {
        return new PipelineParamPointerImpl<T>(p);
    }

    public PipelineTextureParamPointerImpl newTexturePointerFrom(PipelineTextureParam p) {
        return new PipelineTextureParamPointerImpl(p);
    }

    // public PipelineTextureParamPointer getTexture(String name){
    //     return new PipelineTextureParamPointer(this,name);
    // }

    static enum PointerType{
        VALUE,TEXTURE
    }

    @Override
    public <T>  T resolve(PipelinePass pass, Object keyo, Object defaultValue) {
        return resolve(pass,keyo,defaultValue,PointerType.VALUE);

    }

    @Override
    public <T> T resolveTexture(PipelinePass pass, Object keyo, Object defaultValue) {
        return resolve(pass,keyo,defaultValue,PointerType.TEXTURE);
    }

    public void initializeTexture(PipelineTextureParam tx){
        if(txInitializer!=null)txInitializer.accept(tx);

    }

    public <T> T resolve(PipelinePass pass, Object keyo, Object defaultValue, PointerType type) {
        assert keyo != null;
        String key=(String)keyo;
        int passId=-1;
        char fc=key.charAt(0);
        if(fc == '<'){
            if(passId == -1) passId=pass.getId();
            key=key.substring(1);
            while(true){
                if(passId > 0) passId--;
                else break;
                String tkey=key;
                if(passId > 0) tkey=passId + key;
                if(storage.containsKey(tkey)) break;
            }
        }else if(fc == '>'){
            key=key.substring(1);
            if(passId == -1) passId=pass.getId();
        }
        if(passId > 0) key=passId + key;
        PipelineParam param=(PipelineParam)storage.get(key);
        if(param == null){
            if(type == PointerType.VALUE){
                param=PipelineParam.from(defaultValue);
            }else if(type == PointerType.TEXTURE){
                PipelineTextureParam txparam=PipelineTextureParam.newEmpty();
                param=txparam;
            }
            storage.put(key,param);
        }
        return (T)param;
    }

}