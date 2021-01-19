package com.jme3.rendering.pipeline.renderer;

import java.util.List;

import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;

public interface FrameBufferPass <T extends FrameBufferPass> {
    public FrameBuffer getFrameBuffer(List<Texture> outColors, Texture outDepth) ;
    
     public  T outColors(Texture... colors);
     public  T outDepth(Texture depth) ;
     public void invalidateFrameBuffer();
}