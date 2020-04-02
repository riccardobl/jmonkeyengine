package com.jme3.rendering.pipeline.passes;

import java.util.ArrayList;
import java.util.List;

import com.jme3.material.MatParamOverride;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.shader.VarType;
import com.jme3.system.Timer;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.ColorSpace;

/**
 * A pass that renders a viewport. Used to migrate from jme core pipeline.
 * @author Riccardo Balbo
 */
public class RenderViewPortPass extends RenderPass<RenderViewPortPass>{
    private final ViewPort viewPort;  


	public RenderViewPortPass(
        RenderManager renderManager, 
    FrameBufferFactory fbFactory,  
    Timer timer,
    ViewPort vp
    
    ){
        super(renderManager, fbFactory, vp.getCamera(),timer, false);
        viewPort=vp;
	}

   

	@Override
    protected void onRender(Pipeline pipeline,final float tpf,final int w,final int h,final FrameBuffer outFb) {       
        final FrameBuffer ofb=viewPort.getOutputFrameBuffer();
        final boolean occ=viewPort.isClearColor();
        final boolean ocd=viewPort.isClearDepth();
        final boolean ocs=viewPort.isClearStencil();

        viewPort.setOutputFrameBuffer(outFb);
        viewPort.setClearFlags(false,false,false);

        getRenderManager().renderViewPort(viewPort, tpf);
        
        viewPort.setOutputFrameBuffer(ofb);
        viewPort.setClearFlags(occ,ocd,ocs);

    }
    
    public RenderViewPortPass outColors(Texture... out){
        for(int i=0;i<out.length;i++)useOutput(RenderPass.RENDER_OUT_COLOR+i,out[i]);
        return this;
    }

        
    public RenderViewPortPass outDepth(Texture out){
        useOutput(RenderPass.RENDER_OUT_DEPTH,out);
        return this;
    }

    @Override
    protected void beforeRender(Pipeline pipeline, float tpf, int w, int h) {

    }

    @Override
    protected void afterRender(Pipeline pipeline, float tpf, int w, int h) {

    }

    

    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {
        // TODO Set matparamn

    }


    @Override
    protected void preAttach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void postAttach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void preDetach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void postDetach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeRun(Pipeline pipeline, float tpf) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void afterRun(Pipeline pipeline, float tpf) {
        // TODO Auto-generated method stub

    }

    



}
