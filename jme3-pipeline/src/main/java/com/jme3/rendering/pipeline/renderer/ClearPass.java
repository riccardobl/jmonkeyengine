package com.jme3.rendering.pipeline.renderer;

import com.jme3.math.ColorRGBA;
import com.jme3.rendering.pipeline.PipelinePass;

public interface ClearPass<T extends ClearPass>extends FrameBufferPass<T>{
    public T useBackgroundColor(ColorRGBA c);
    public  T clearColor(boolean v) ;
    public  T clearDepth(boolean v);
    public  T clearStencil(boolean v);

    public  boolean isClearColor();
    public  boolean isClearDepth();
    public  boolean isClearStencil() ;
}