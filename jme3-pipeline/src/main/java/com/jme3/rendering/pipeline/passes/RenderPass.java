package com.jme3.rendering.pipeline.passes;

import java.util.ArrayList;
import java.util.List;

import com.jme3.material.MatParamOverride;
import com.jme3.material.TechniqueDef;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.shader.VarType;
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
    
    private List<Texture> outColors=new ArrayList<Texture>();;
    private List<Texture> oldOutColors=new ArrayList<Texture>();;

    private Texture outDepth;
    private Texture oldOutDepth;

    private MatParamOverride mrtParam;
    private String useTechnique=TechniqueDef.DEFAULT_TECHNIQUE_NAME;

    public static int RENDER_OUT_COLOR=Short.MAX_VALUE;
    public static int RENDER_OUT_DEPTH=Short.MIN_VALUE;
    
    protected void beforeInOutSet(){
        List<Texture> oldC=outColors;
        outColors=oldOutColors;
        oldOutColors=oldC;
        outColors.clear();

        oldOutDepth=outDepth;
        outDepth=null;

    }

    protected void afterInOutSet(){
        if(oldOutDepth!=outDepth){
            invalidateFrameBuffer();
            return;
        }
        if(oldOutColors.size()!=outColors.size()||!outColors.containsAll(oldOutColors)){
            invalidateFrameBuffer();
            return;
        }         
    }

  
    
    protected RenderPass(RenderManager renderManager,FrameBufferFactory fbFactory){
        this.renderManager=renderManager;
        this.fbFactory=fbFactory;
    }

    public RenderPass technique(String name){
        if(name==null)name=TechniqueDef.DEFAULT_TECHNIQUE_NAME;
        useTechnique=name;
        return this;
    }

    public String getTechnique(){
        return useTechnique;
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




    protected FrameBuffer getFrameBuffer(List<Texture> outColors,Texture outDepth){
        if(outFb!=null)return outFb;
        Format outDepthF=null;
        Format outColorF=null;
        int width=2;
        int height=2;
        boolean srgb=false;
        int samples=1;

        if(outDepth != null&&outDepth!=getFbFactory().getDefaultTarget()){
            Image depthImg=outDepth.getImage();
            outDepthF=depthImg.getFormat();
            width=depthImg.getWidth();
            height=depthImg.getHeight();
            samples=depthImg.getMultiSamples();
            srgb=false;
        }

        if(outColors != null&&outColors.get(0)!=null&&outColors.get(0)!=getFbFactory().getDefaultTarget()){
            Image colorImg=outColors.get(0).getImage();
            outColorF=colorImg.getFormat();
            width=colorImg.getWidth();
            height=colorImg.getHeight();
            samples=colorImg.getMultiSamples();
            srgb=colorImg.getColorSpace() == ColorSpace.sRGB;
        }

        if(mrtParam==null){
            mrtParam=new MatParamOverride(VarType.Boolean, "MRT", false);
        }
        mrtParam.setValue(outColors.size()>1);

        outFb= fbFactory.get(width,height,outColorF,outDepthF,outColors,outDepth,srgb,samples);
        return outFb;
    }


    public void onOutput(Object key,Object value){
        if(key instanceof Number){
            int keyn=((Number)key).intValue();

            if(keyn==RENDER_OUT_DEPTH){
                outDepth=(Texture)value;   
                    // invalidateFrameBuffer();
            }else if(keyn>=RENDER_OUT_COLOR){
                // List<Texture> cls=(List<Texture>)value;
                int tid=keyn-RENDER_OUT_COLOR;
                while(outColors.size()<=tid)outColors.add(null);
                outColors.set(tid, (Texture)value);
                // invalidateFrameBuffer();

                // if(cls.size()!=outColors.size()||!outColors.containsAll(cls)){
                //     outColors.clear();
                //     for(Texture pp:cls)outColors.add(pp);        
                //     invalidateFrameBuffer();
                // }         
            }
        } 
    }

    public abstract void onRender(float tpf,int w,int h,FrameBuffer outFb);

    @Override
    public void onRun(float tpf){
        if(outDepth==null&&(outColors==null||outColors.size()==0))return;
        FrameBuffer outFb=getFrameBuffer(outColors,outDepth);

        int w=getFbFactory().getFrameBufferWidth(outFb);
        int h=getFbFactory().getFrameBufferHeight(outFb);
        

        String otch=renderManager.getForcedTechnique();
        renderManager.setForcedTechnique(useTechnique);
        renderManager.addForcedMatParam(mrtParam);

        onRender(tpf,w,h,outFb);

        renderManager.removeForcedMatParam(mrtParam);    
        renderManager.setForcedTechnique(otch);

    }


    
}