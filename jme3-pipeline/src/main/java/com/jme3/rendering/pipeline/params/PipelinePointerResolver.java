package com.jme3.rendering.pipeline.params;

import java.util.function.BiFunction;

import com.jme3.rendering.pipeline.PipelinePass;

/**
 * PipelinePointerResolver
 */
public interface PipelinePointerResolver {
    public <T> T resolve(Class type, PipelinePass pass, T ref, BiFunction<PipelinePass,T,T> init) ;
}