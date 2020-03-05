package com.jme3.rendering.pipeline.params;

import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.PipelinePointerConstructor;

/**
 * PipelinePointerResolver
 */
public interface PipelinePointerResolver {
    public <T> T resolve(Class type, Pipeline pipeline,PipelinePass pass, T ref, PipelinePointerConstructor<T> init) ;
}