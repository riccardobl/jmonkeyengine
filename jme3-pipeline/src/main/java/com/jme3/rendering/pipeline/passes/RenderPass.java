package com.jme3.rendering.pipeline.passes;

import java.util.ArrayList;
import java.util.List;

import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.params.PipelineParam;
import com.jme3.rendering.pipeline.params.PipelineParamPointer;
import com.jme3.rendering.pipeline.params.PipelineTextureParam;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.ColorSpace;

public abstract class RenderPass extends PipelinePass{

    private FrameBuffer outFb;
    private final FrameBufferFactory fbFactory;
    private final RenderManager renderManager;

    private boolean clearColor=true,clearDepth=true,clearStencil=true;
    
    protected RenderPass(RenderManager renderManager,FrameBufferFactory fbFactory){
        this.renderManager=renderManager;
        this.fbFactory=fbFactory;
    }

    public RenderPass clearColor(boolean v){
        clearColor=v;
        return this;
    }


    public RenderPass clearDepth(boolean v){
        clearDepth=v;
        return this;
    }

    public RenderPass clearStencil(boolean v){
        clearStencil=v;
        return this;
    }

    protected RenderManager getRenderManager(){
        return renderManager;
    }

    protected void invalidateFrameBuffer(){
        outFb=null;
    }

    protected FrameBufferFactory getFbFactory(){
        return fbFactory;
    }

    public boolean isClearColor(){
        return clearColor;
    }

    public boolean isClearDepth(){
        return clearDepth;
    }

    public boolean isClearStencil(){
        return clearStencil;
    }


    public  void afterFrameBufferUpdate(FrameBuffer fb){
        
    }

    protected FrameBuffer getFrameBuffer(List<PipelineParam<Texture>> outColors,PipelineParam<Texture> outDepth){
        if(outFb!=null)return outFb;
        Format outDepthF=null;
        Format outColorF=null;
        int width=2;
        int height=2;
        boolean srgb=false;
        int samples=1;

        if(outDepth != null&&outDepth.getValue()!=null){
            Image depthImg=outDepth.getValue().getImage();
            outDepthF=depthImg.getFormat();
            width=depthImg.getWidth();
            height=depthImg.getHeight();
            samples=depthImg.getMultiSamples();
            srgb=false;
        }

        if(outColors != null&&outColors.get(0).getValue()!=null){
            Image colorImg=outColors.get(0).getValue().getImage();
            outColorF=colorImg.getFormat();
            width=colorImg.getWidth();
            height=colorImg.getHeight();
            samples=colorImg.getMultiSamples();
            srgb=colorImg.getColorSpace() == ColorSpace.sRGB;
        }

        Texture depthTx=null;
        List<Texture> colorTxs=null;
        
        if(outColors != null&&outColors.get(0).getValue()!=null){
            colorTxs=new ArrayList<>();
            for(PipelineParam<Texture> p:outColors)  colorTxs.add(p.getValue());
        }
        if(outDepth != null&&outDepth.getValue()!=null){
            depthTx=outDepth.getValue();
        }
        outFb= fbFactory.get(width,height,outColorF,outDepthF,colorTxs,depthTx,srgb,samples);
        afterFrameBufferUpdate(outFb);
        return outFb;
    }





    
}