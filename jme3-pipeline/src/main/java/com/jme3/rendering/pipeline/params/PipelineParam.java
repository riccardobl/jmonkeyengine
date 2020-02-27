package com.jme3.rendering.pipeline.params;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Image.Format;

/**
 * PipelineParam
 */
public class PipelineParam<T>{  

    public static  <T> PipelineParam<T> from(PipelineParam<T> p){
        return new PipelineParam<T>(p.getValue());
    }

    public static  <T> PipelineParam<T> from(T p){
        return new PipelineParam<T>(p);
    }

    public static  <T> PipelineParam<T> newEmpty(){
        return new PipelineParam<T>();
    }


    private T value;

    protected  PipelineParam(T value){
        this.value=value;
    }

    protected  PipelineParam(){}

    public  T getValue() {
        return (T)value;
    }

    public void setValue(T v) {
        this.value=v;
    }
}