package com.jme3.rendering.pipeline;

@FunctionalInterface
public interface PipelinePointerConstructor<T> {
    public T construct(Pipeline pipeline,PipelinePass pass,T inst);
}