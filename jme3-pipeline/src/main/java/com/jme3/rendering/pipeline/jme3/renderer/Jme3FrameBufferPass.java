package com.jme3.rendering.pipeline.jme3.renderer;

import java.util.ArrayList;
import java.util.List;

import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.renderer.FrameBufferPass;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;

import com.jme3.material.MatParam;
import com.jme3.material.MatParamOverride;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.Technique;
import com.jme3.material.TechniqueDef;
import com.jme3.renderer.Camera;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.renderer.*;
import com.jme3.rendering.pipeline.jme3.context.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.instancing.InstancedGeometry;
import com.jme3.shader.Shader;
import com.jme3.shader.VarType;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.system.JmeSystem;
import com.jme3.system.Timer;
import com.jme3.system.JmeContext.Type;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.SafeArrayList;

public abstract class Jme3FrameBufferPass<T extends Jme3FrameBufferPass> extends PipelinePass<T> implements FrameBufferPass<T> {
    protected FrameBufferFactory fbFactory;

    protected List<Texture> outColors = new ArrayList<Texture>();;
    protected List<Texture> oldOutColors = new ArrayList<Texture>();;

    protected Texture outDepth;
    protected Texture oldOutDepth;
    protected FrameBuffer outFb;

    public void setFrameBufferFactory(FrameBufferFactory fbFactory){
        this.fbFactory=fbFactory;
    }

    public FrameBufferFactory getFrameBufferFactory(){
        return this.fbFactory;
    }

    @Override
    public void invalidateFrameBuffer() {
        outFb = null;
    }

    
    @Override
    public FrameBuffer getFrameBuffer(List<Texture> outColors, Texture outDepth) {
        if (outFb != null) return outFb;
        Format outDepthF = null;
        Format outColorF = null;
        int width = 2;
        int height = 2;
        boolean srgb = false;
        int samples = 1;

        if (outDepth != null && outDepth != fbFactory.getDefaultTarget()) {
            Image depthImg = outDepth.getImage();
            outDepthF = depthImg.getFormat();
            width = depthImg.getWidth();
            height = depthImg.getHeight();
            samples = depthImg.getMultiSamples();
            srgb = false;
        }

        if (outColors != null && outColors.get(0) != null && outColors.get(0) != fbFactory.getDefaultTarget()) {
            Image colorImg = outColors.get(0).getImage();
            outColorF = colorImg.getFormat();
            width = colorImg.getWidth();
            height = colorImg.getHeight();
            samples = colorImg.getMultiSamples();
            srgb = colorImg.getColorSpace() == ColorSpace.sRGB;
        }


        outFb = fbFactory.get(width, height, outColorF, outDepthF, outColors, outDepth, srgb, samples);

        return outFb;
    }

    public T outColors(Texture... colors) {
        for(int i=0;;i++)  if(useOutput(RenderOutput.Color +i,null)==null)break; //reset
        for(int i=0;i<colors.length;i++) useOutput(RenderOutput.Color + (i), colors[i]);        
        return (T) this;
    }

    public T outDepth(Texture depth) {
        useOutput(RenderOutput.Depth,depth);
        return (T)this;
    }

    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {
        if (key instanceof Number) {
            int keyn = ((Number) key).intValue();
            if (keyn == RenderOutput.Depth) {
                outDepth = (Texture) value;
            } else if (keyn >= RenderOutput.Color) {
                Texture tx = (Texture) value;
                int tid = keyn - RenderOutput.Color;
                while (outColors.size() <= tid) outColors.add(null);
                outColors.set(tid, tx);
            }
        }
    }


    @Override
    protected void beforeIO(Pipeline pipeline) {
        List<Texture> oldC = outColors;
        outColors = oldOutColors;
        oldOutColors = oldC;
        outColors.clear();

        oldOutDepth = outDepth;
        outDepth = null;


    }

    @Override
    protected void afterIO(Pipeline pipeline) {

        if (oldOutDepth != outDepth) {
            invalidateFrameBuffer();
            return;
        }
        if (oldOutColors.size() != outColors.size() || !outColors.containsAll(oldOutColors)) {
            invalidateFrameBuffer();
            return;
        }
    }
}