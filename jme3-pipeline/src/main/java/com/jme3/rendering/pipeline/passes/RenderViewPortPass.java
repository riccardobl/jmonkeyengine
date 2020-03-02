package com.jme3.rendering.pipeline.passes;

import java.util.ArrayList;
import java.util.List;

import com.jme3.material.MatParamOverride;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.shader.VarType;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.ColorSpace;

/**
 * RenderViewPortPass
 */
public class RenderViewPortPass extends RenderPass{

    private static final Object INPUT_VIEWPORT=new Object();

    private ViewPort viewPort;
    


	public RenderViewPortPass(final RenderManager renderManager,final FrameBufferFactory fbFactory){
        super(renderManager, fbFactory);
	}

	@Override
    protected void onRender(final float tpf,final int w,final int h,final FrameBuffer outFb) {
        final RenderManager renderManager= getRenderManager();

        if(viewPort==null)return;

        
        final FrameBuffer ofb=viewPort.getOutputFrameBuffer();
        viewPort.setOutputFrameBuffer(outFb);

        final boolean occ=viewPort.isClearColor();
        final boolean ocd=viewPort.isClearDepth();
        final boolean ocs=viewPort.isClearStencil();

        viewPort.setClearFlags(isClearColor(), isClearDepth(), isClearStencil());

        renderManager.renderViewPort(viewPort, tpf);
        viewPort.setOutputFrameBuffer(ofb);

        viewPort.setClearFlags(occ,ocd,ocs);

    }
    
    public RenderViewPortPass outColors(List<Texture> out){
        for(int i=0;i<out.size();i++)useOutput(RenderPass.RENDER_OUT_COLOR+i,out.get(i));
        return this;
    }

        
    public RenderViewPortPass outDepth(Texture out){
        useOutput(RenderPass.RENDER_OUT_DEPTH,out);
        return this;
    }

	public RenderViewPortPass viewPort(final ViewPort viewPort) {
        useInput(INPUT_VIEWPORT, viewPort);
        return this;
	}

    @Override
    protected void onInput(final Object key, final Object value) {
        if(key==INPUT_VIEWPORT)viewPort=(ViewPort)value;
    }

}
