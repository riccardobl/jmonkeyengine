package com.jme3.rendering.pipeline;

/**
 * PipelinePointerResolver
 */
public interface PipelinePointerResolver{

    public <T> T resolve(Class type, Pipeline pipeline,PipelinePass pass, T ref, PipelinePointerConstructor<T> init) ;
}