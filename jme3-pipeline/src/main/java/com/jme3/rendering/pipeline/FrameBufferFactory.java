package com.jme3.rendering.pipeline;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Image.Format;

/**
 * FramebufferFactory
 */
public class FrameBufferFactory {
    private Map<Integer,FrameBuffer> framebufferCache=new HashMap<Integer,FrameBuffer>();
    protected FrameBuffer defaultFrameBuffer=null;
    protected int defaultFbWidth=2,defaultFbHeight=2;

    public void setDefaultFrameBuffer(int width,int height,FrameBuffer fb){
        defaultFrameBuffer=fb;
        defaultFbHeight=height;
        defaultFbWidth=width;
    }


    public int getFrameBufferWidth(FrameBuffer fb){
        if(fb==null)return defaultFbWidth;
        return fb.getWidth();
    }

    public int getFrameBufferHeight(FrameBuffer fb){
        if(fb==null)return defaultFbHeight;
        return fb.getHeight();
    }

    protected int hashFb( 
        int width, int height, 
        Format colorFormat, 				
        Format depthFormat,
        Collection colorOut, 
        Object depthOut ,
        boolean srgb,
        int samples
    ){
        return Objects.hash(width,height,colorFormat.hashCode(),depthFormat.hashCode(),colorOut.hashCode(),depthOut.hashCode(),srgb);
    }
    
    public FrameBuffer get(
            int width, int height, 
            Format colorFormat, 				
            Format depthFormat,
            Collection<Texture> colorOut, 
            Texture depthOut ,
            boolean srgb,
            int samples
    ) {
        if(depthOut==null)return defaultFrameBuffer;
        for(Texture t:colorOut)       if(t==null)return defaultFrameBuffer;
        
        Integer hash=hashFb(width,height,colorFormat,depthFormat,colorOut,depthOut,srgb,samples);
        FrameBuffer fb=framebufferCache.get(hash);
        if(fb==null){
            fb = new FrameBuffer(width, height, samples);
            fb.setSrgb(srgb);
            fb.setMultiTarget(colorOut.size()>1);

            for (Texture tx : colorOut) {            
                if(tx.getImage().getMultiSamples()!=samples){
                    throw new UnsupportedOperationException("Framebuffer and targets must have the same number of samples");
                }
                fb.addColorTexture((Texture2D)tx);            
            }

            if(depthOut!=null){
                if(depthOut.getImage().getMultiSamples()!=samples){
                    throw new UnsupportedOperationException("Framebuffer and targets must have the same number of samples");
                }
                fb.setDepthTexture((Texture2D)depthOut);
            }
            framebufferCache.put(hash,fb);
         }
         return fb;
    }
}