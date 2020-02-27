package com.jme3.rendering.pipeline.params.literalpointers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.params.PipelineParam;
import com.jme3.rendering.pipeline.params.PipelineParamPointer;
import com.jme3.rendering.pipeline.params.PipelinePointerResolver;
import com.jme3.rendering.pipeline.params.PipelineTextureParam;
import com.jme3.texture.Texture;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.image.ColorSpace;

public class PipelineTextureParamPointerImpl implements PipelineParamPointer<Texture>{

    protected String key;
    protected PipelinePointerResolver resolver;
    protected PipelineTextureParam ref;
    protected Consumer<PipelineTextureParam> txInitializer=null;
    protected List<Consumer<PipelineTextureParam>> enqueuedActions=new ArrayList<Consumer<PipelineTextureParam>>();

    protected PipelineTextureParamPointerImpl(Consumer<PipelineTextureParam> initializer,PipelinePointerResolver resolver,String key){
        this.key=key;
        this.resolver=resolver;
        this.txInitializer=initializer;
    }

    protected PipelineTextureParamPointerImpl(PipelineTextureParam ref){
        this.ref=ref;
    }

    @Override
    public PipelinePointerResolver getPointerResolver() {
        return resolver;
    }

    @Override
    public Object getAddress() {
        return key;
    }

    @Override
    public PipelineParam<Texture> get(PipelinePass pass) {
        PipelineTextureParam ref=null;
        if(getPointerResolver() == null) ref= this.ref;
        else{
            ref=(PipelineTextureParam)getPointerResolver().resolveTexture(pass,this.getAddress(),null);
            assert ref != null;
            if(txInitializer != null&&!ref.isInitialized()){
                txInitializer.accept(ref);
                ref.setInitialized();
            }
        }

        for(Consumer<PipelineTextureParam> a:enqueuedActions){
            a.accept(ref);
        }


        return ref;
    }


    public PipelineTextureParamPointerImpl  minFilter(MinFilter f){
        enqueuedActions.add((param) -> {
            param.minFilter(f);
        });
        return this;
    }

    public PipelineTextureParamPointerImpl  magFilter(MagFilter f){
        enqueuedActions.add((param) -> {
            param.magFilter(f);
        });
        return this;
    }

    public PipelineTextureParamPointerImpl  anisotropicFilter(int f){
        enqueuedActions.add((param) -> {
            param.anisotropicFilter(f);
        });        
        return this;
    }

    public PipelineTextureParamPointerImpl wrapAxis(WrapMode s, WrapMode t, WrapMode r){
        enqueuedActions.add((param) -> {
            param.wrapAxis(s,t,r);
        });        
        return this;
    }

    public PipelineTextureParamPointerImpl width(int v){
        enqueuedActions.add((param) -> {
            param.width(v);
        });        
        return this;
    }


    public PipelineTextureParamPointerImpl height(int v){
        enqueuedActions.add((param) -> {
            param.height(v);
        });    
        return this;
    }


    public PipelineTextureParamPointerImpl numSamples(int v){
        enqueuedActions.add((param) -> {
            param.numSamples(v);
        });    
        return this;
    }

    public PipelineTextureParamPointerImpl format(Format v){
        enqueuedActions.add((param) -> {
            param.format(v);
        });    
        return this;
    }

    public PipelineTextureParamPointerImpl colorSpace(ColorSpace v){
        enqueuedActions.add((param) -> {
            param.colorSpace(v);
        });    
        return this;
    }

    public PipelineTextureParamPointerImpl depth(int v){
        enqueuedActions.add((param) -> {
            param.depth(v);
        });    
        return this;
    }

    public PipelineTextureParamPointerImpl length(int v){
        enqueuedActions.add((param) -> {
            param.length(v);
        });    
        return this;
    }

    public PipelineTextureParamPointerImpl cubeMap(boolean v){
        enqueuedActions.add((param) -> {
            param.cubeMap(v);
        });    
        return this;
    }
    
    public PipelineTextureParamPointerImpl alloc2D(int width,int height,Format format,ColorSpace colorSpace,int numSamples){
        enqueuedActions.add((param) -> {
            param.alloc2D(width, height, format, colorSpace, numSamples);
        });    
        return this;
    }

    public PipelineTextureParamPointerImpl alloc3D(int width,int height,int depth,Format format,ColorSpace colorSpace,int numSamples){
        enqueuedActions.add((param) -> {
            param.alloc3D(width, height, depth, format, colorSpace, numSamples);
        });    
        return this;
    }

    public PipelineTextureParamPointerImpl allocCube(int width,int height,Format format,ColorSpace colorSpace,int numSamples){
        enqueuedActions.add((param) -> {
            param.allocCube(width, height, format, colorSpace, numSamples);
        });  
        return this;
    }

    public PipelineTextureParamPointerImpl allocArray(int width,int height,int length,Format format,ColorSpace colorSpace,int numSamples){
        enqueuedActions.add((param) -> {
            param.allocArray(width, height, length, format, colorSpace, numSamples);
        });  
        return this;
    }

    public PipelineTextureParamPointerImpl allocMipMaps(int sizes[]){
        enqueuedActions.add((param) -> {
            param.allocMipMaps(sizes);
        });  
        return this;
    }




 

    



}