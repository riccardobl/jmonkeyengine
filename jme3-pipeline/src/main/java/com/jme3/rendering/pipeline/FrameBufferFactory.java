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
 * Handles creation and caching of framebuffers.
 * @author Riccardo Balbo
 */
public class FrameBufferFactory {
    protected Map<Integer,FrameBuffer> framebufferCache=new HashMap<Integer,FrameBuffer>();
    protected FrameBuffer defaultFrameBuffer=null;
    protected int defaultFbWidth=2,defaultFbHeight=2;
    protected Texture2D defaultTg=null;

    /**
     * Set the default framebuffer on which the final output is supposed to be rendered.
     * @param width Width of the frame buffer
     * @param height Height of the frame buffer
     * @param fb The framebuffer. Null to output to screen.
     */
    public void setDefaultFrameBuffer(int width,int height,FrameBuffer fb){
        defaultFrameBuffer=fb;
        defaultFbHeight=height;
        defaultFbWidth=width;
        defaultTg=null;
    }


    /**
     * Get texture target used to output on the default framebuffer
     */
    public Texture2D getDefaultTarget(){
        if(defaultTg==null)defaultTg=new Texture2D(defaultFbWidth, defaultFbHeight, Format.RGB8);
        return defaultTg;
    }

    
    /**
     * Get framebuffer width, or screen width if fb = null
     * @param fb
     * @return
     */
    public int getFrameBufferWidth(FrameBuffer fb){
        if(fb==null)return defaultFbWidth;
        else return fb.getWidth();
    }

        /**
     * Get framebuffer height, or screen height if fb = null
     * @param fb
     * @return
     */
    public int getFrameBufferHeight(FrameBuffer fb){
        if(fb==null)return defaultFbHeight;
        else return fb.getHeight();
    }

    /**
     * Get a framebuffer that has the required properties. If unavailable, a new framebuffer will be created.
     * @return The framebuffer or NULL if default framebuffer.
     */
    public FrameBuffer get(
            int width, int height, 
            Format colorFormat, 				
            Format depthFormat,
            Collection<Texture> colorOut, 
            Texture depthOut ,
            boolean srgb,
            int samples
    ) {
        if(depthOut==getDefaultTarget())return defaultFrameBuffer;
        for(Texture t:colorOut)       if(t==getDefaultTarget())return defaultFrameBuffer;
        
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



    protected int hashFb( 
        int width, int height, 
        Format colorFormat, 				
        Format depthFormat,
        Collection colorOut, 
        Object depthOut ,
        boolean srgb,
        int samples
    ){
        return Objects.hash(width,height,
        colorFormat==null?0:colorFormat.hashCode(),
        depthFormat==null?0:depthFormat.hashCode(),
        colorOut==null||colorOut.size()==0?0:colorOut.hashCode(),
        depthOut==null?0:depthOut.hashCode(),srgb
        );
    }
}