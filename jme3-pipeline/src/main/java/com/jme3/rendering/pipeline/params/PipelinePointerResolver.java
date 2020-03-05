package com.jme3.rendering.pipeline.params;

import java.util.function.BiFunction;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.PipelinePointerConstructor;
import com.jme3.rendering.pipeline.Pipeline;

/**
 * PipelinePointerResolver
 */
public interface PipelinePointerResolver {
    public <T> T resolve(Class type, Pipeline pipeline,PipelinePass pass, T ref, PipelinePointerConstructor<T> init) ;
}