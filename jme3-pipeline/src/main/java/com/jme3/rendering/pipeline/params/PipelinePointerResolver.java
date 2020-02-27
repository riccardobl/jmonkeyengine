package com.jme3.rendering.pipeline.params;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.params.PipelineParam;

/**
 * PipelinePointerResolver
 */
public interface PipelinePointerResolver {
    public <T> T resolve(PipelinePass pass, Object key, Object defaultValue);
    public <T> T resolveTexture(PipelinePass pass, Object key, Object defaultValue);
}