package com.jme3.rendering.pipeline.params;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.texture.Texture;

public interface PipelineParamPointer<T> {
 
    public static final PipelineParamPointer<Texture> DEFAULT_OUT=new PipelineParamPointer<Texture>(){
        PipelineParam<Texture> param= new PipelineParam<Texture>(null);
        @Override
        public PipelineParam<Texture> get(PipelinePass pass) {
            return param ;
        }

        @Override
        public PipelinePointerResolver getPointerResolver() {
            return null;
        }

        @Override
        public Object getAddress() {
            return null;
        }
    };
    /**
     * Resolves the pointer and gets the param.
     */
    public PipelineParam<T> get(PipelinePass pass);    
    public PipelinePointerResolver getPointerResolver();
    public Object getAddress();
}