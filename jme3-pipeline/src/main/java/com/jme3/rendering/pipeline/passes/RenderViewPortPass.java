package com.jme3.rendering.pipeline.passes;

import java.util.ArrayList;
import java.util.List;

import com.jme3.material.MatParamOverride;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.params.PipelineParam;
import com.jme3.rendering.pipeline.params.PipelineParamPointer;
import com.jme3.rendering.pipeline.params.PipelineTextureParam;
import com.jme3.rendering.pipeline.test.NumberTransformPass;
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

    protected ViewPort viewPort;
    protected List<PipelineParam<Texture>> outColors=new ArrayList<PipelineParam<Texture>>();;
    protected PipelineParam<Texture> outDepth;
    
    protected MatParamOverride mrtParam;


	public RenderViewPortPass(RenderManager renderManager,FrameBufferFactory fbFactory){
        super(renderManager, fbFactory);

	}

	@Override
	public void run(float tpf) {
        RenderManager renderManager= getRenderManager();

        if(viewPort==null||(outColors==null&&outDepth==null))return;
        
        if(mrtParam==null) mrtParam=new MatParamOverride(VarType.Boolean,"Mrt",false);
        mrtParam.setValue(this.outColors.size()>1);

        

        
        FrameBuffer ofb=viewPort.getOutputFrameBuffer();
        viewPort.setOutputFrameBuffer(getFrameBuffer(outColors, outDepth));

        boolean occ=viewPort.isClearColor();
        boolean ocd=viewPort.isClearDepth();
        boolean ocs=viewPort.isClearStencil();

        viewPort.setClearFlags(isClearColor(), isClearDepth(), isClearStencil());

        renderManager.addForcedMatParam(mrtParam);
        renderManager.renderViewPort(viewPort, tpf);
        viewPort.setOutputFrameBuffer(ofb);
        renderManager.removeForcedMatParam(mrtParam);    

        viewPort.setClearFlags(occ,ocd,ocs);

    }
    
    public RenderViewPortPass outColors( List<PipelineParamPointer<Texture>> outColors){
        this.outColors.clear();
        for(PipelineParamPointer<Texture> pp:outColors)  this.outColors.add(pp.get(this));        
        invalidateFrameBuffer();          
        return this;
    }

    public RenderViewPortPass outDepth( PipelineParamPointer<Texture> outDepth){
        this.outDepth=outDepth.get(this);
        invalidateFrameBuffer();
        return this;
    }

	public RenderViewPortPass viewPort(ViewPort viewPort) {
        this.viewPort=viewPort;
		return this;
	}

}
