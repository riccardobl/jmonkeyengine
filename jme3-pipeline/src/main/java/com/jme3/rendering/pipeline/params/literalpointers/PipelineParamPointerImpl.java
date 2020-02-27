package com.jme3.rendering.pipeline.params.literalpointers;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.params.PipelineParam;
import com.jme3.rendering.pipeline.params.PipelineParamPointer;
import com.jme3.rendering.pipeline.params.PipelinePointerResolver;

/**
 * PipelineValueParamPointer
 */
public class PipelineParamPointerImpl<T> implements PipelineParamPointer<T>{
    protected PipelinePointerResolver resolver;
    protected String key;
    protected Object value;
    protected PipelineParam<T> ref;


    protected PipelineParamPointerImpl( PipelineParam<T>  ref){
        this.ref=ref;
    }

    protected PipelineParamPointerImpl(PipelinePointerResolver resolver,String addr){
        this.resolver=resolver;
        this.key=addr;
    }

    public  PipelineParamPointerImpl<T> value(T val){
        this.value=val;
        return this;
    }


    @Override
    public PipelineParam<T> get(PipelinePass pass) {
        if(getPointerResolver()==null)return this.ref;
        return getPointerResolver().resolve(pass,this.getAddress(),value);
    }

    @Override
    public PipelinePointerResolver getPointerResolver() {
        return  this.resolver;
    }

    @Override
    public Object getAddress() {
        return key;
    }

    
}