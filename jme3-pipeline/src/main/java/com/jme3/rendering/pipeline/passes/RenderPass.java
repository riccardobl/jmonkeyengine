package com.jme3.rendering.pipeline.passes;

import java.util.ArrayList;
import java.util.List;

import com.jme3.material.MatParamOverride;
import com.jme3.material.TechniqueDef;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.WorldParamsUtil;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.gl.GLDebuggerAppState;
import com.jme3.rendering.pipeline.renderer.gl.WorldParams;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.shader.VarType;
import com.jme3.system.Timer;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.struct.Struct;

/**
 * An abstract pass that renders something.
 * 
 * @author Riccardo Balbo
 */
public abstract class RenderPass<T extends RenderPass> extends PipelinePass<T> {

    private FrameBuffer outFb;

    private boolean clearColor = true, clearDepth = true, clearStencil = true;

    private List<Texture> outColors = new ArrayList<Texture>();;
    private List<Texture> oldOutColors = new ArrayList<Texture>();;

    private Texture outDepth;
    private Texture oldOutDepth;

    private MatParamOverride mrtParam;
    private String useTechnique = TechniqueDef.DEFAULT_TECHNIQUE_NAME;

    // private int outWidth;
    // private int outHeight;

    public static int RENDER_OUT_COLOR = Short.MAX_VALUE;
    public static int RENDER_OUT_DEPTH = Short.MIN_VALUE;

    private FrameBufferFactory fbFactory;
    private RenderManager renderManager;
    private boolean orthogonalCam;
    private Camera camera;
    private Timer timer;

    protected RenderPass(RenderManager renderManager, FrameBufferFactory fbFactory, Camera cam, Timer tim, boolean orthogonalCam

    ) {
        super();
        this.fbFactory = fbFactory;
        this.renderManager = renderManager;
        this.setTimer(tim);
        this.setCamera(cam, orthogonalCam);

        
    }

    protected Timer getTimer() {
        return timer;
    }

    protected void setTimer(Timer timer) {
        this.timer = timer;
        if(timer!=null)useInput("WorldTimer", getTimer() );
    }

    protected void setCamera(Camera cam, boolean ortho) {
        this.camera = cam;
        this.orthogonalCam = ortho;
        if(this.camera!=null)useInput("WorldCamera",  getCamera() );
    }

    protected Camera getCamera() {
        return this.camera;
    }

    protected FrameBufferFactory getFrameBufferFactory() {
        return fbFactory;
    }

    protected void setFrameBufferFactory(FrameBufferFactory fb) {
        this.fbFactory = fb;
    }

    protected void setRenderManager(RenderManager rm) {
        this.renderManager = rm;
    }

    protected RenderManager getRenderManager() {
        return this.renderManager;
    }

    public T technique(String name) {
        if (name == null) name = TechniqueDef.DEFAULT_TECHNIQUE_NAME;
        useTechnique = name;
        return (T) this;
    }

    public String getTechnique() {
        return useTechnique;
    }

    public T clearColor(boolean v) {
        clearColor = v;
        return (T) this;
    }

    public T clearDepth(boolean v) {
        clearDepth = v;
        return (T) this;
    }

    public T clearStencil(boolean v) {
        clearStencil = v;
        return (T) this;
    }

    protected void invalidateFrameBuffer() {
        outFb = null;
    }

    // protected FrameBufferFactory getFbFactory() {
    // return fbFactory;
    // }

    public boolean isClearColor() {
        return clearColor;
    }

    public boolean isClearDepth() {
        return clearDepth;
    }

    public boolean isClearStencil() {
        return clearStencil;
    }

    protected FrameBuffer getFrameBuffer(List<Texture> outColors, Texture outDepth) {
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

        if (mrtParam == null) {
            mrtParam = new MatParamOverride(VarType.Boolean, "MRT", false);
        }
        mrtParam.setValue(outColors.size() > 1);

        outFb = fbFactory.get(width, height, outColorF, outDepthF, outColors, outDepth, srgb, samples);

        return outFb;
    }

    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {
        if (key instanceof Number) {
            int keyn = ((Number) key).intValue();
            if (keyn == RENDER_OUT_DEPTH) {
                outDepth = (Texture) value;

                int outWidth = outDepth.getImage().getWidth();
                int outHeight = outDepth.getImage().getHeight();
                WorldParamsUtil.updateResolution(this, outWidth, outHeight, WorldParamsUtil.WorldParam.Resolution.name(), WorldParamsUtil.WorldParam.ResolutionInverse.name());

            } else if (keyn >= RENDER_OUT_COLOR) {
                Texture tx = (Texture) value;
                int tid = keyn - RENDER_OUT_COLOR;
                while (outColors.size() <= tid) outColors.add(null);
                outColors.set(tid, tx);

                int outWidth = tx.getImage().getWidth();
                int outHeight = tx.getImage().getHeight();
                WorldParamsUtil.updateResolution(this, outWidth, outHeight, WorldParamsUtil.WorldParam.Resolution.name(), WorldParamsUtil.WorldParam.ResolutionInverse.name());

            }
        }

    }


  
    @Override
    protected void beforeIO(Pipeline pipeline) {
        if (getCamera() != null) WorldParamsUtil.updateFrustumNearFar(this, getCamera(), WorldParamsUtil.WorldParam.FrustumNearFar.name());
        if (getTimer() != null) WorldParamsUtil.updateTime(this, getTimer(), getSpeed(), WorldParamsUtil.WorldParam.Time.name(), WorldParamsUtil.WorldParam.DeltaTime.name(),
                WorldParamsUtil.WorldParam.IntTime.name(), WorldParamsUtil.WorldParam.Tpf.name());

        List<Texture> oldC = outColors;
        outColors = oldOutColors;
        oldOutColors = oldC;
        outColors.clear();

        oldOutDepth = outDepth;
        outDepth = null;

    }

    @Override
    protected void afterIO(Pipeline pipeline) {

        // setDefaultGlobalParam(GlobalParams.ResolutionInverse, Vector2f::new, (mpo) ->
        // {
        // Vector2f v = (Vector2f) mpo.getValue();
        // v.set(1f / outWidth, 1f / outHeight);
        // });

        // setDefaultGlobalParam(GlobalParams.Resolution, Vector2f::new, (mpo) -> {
        // Vector2f v = (Vector2f) mpo.getValue();
        // v.set(outWidth, outHeight);
        // });

        if (oldOutDepth != outDepth) {
            invalidateFrameBuffer();
            return;
        }
        if (oldOutColors.size() != outColors.size() || !outColors.containsAll(oldOutColors)) {
            invalidateFrameBuffer();
            return;
        }
    }

    protected abstract void onRender(Pipeline pipeline, float tpf, int w, int h, FrameBuffer outFb);

    protected abstract void beforeRender(Pipeline pipeline, float tpf, int w, int h);

    protected abstract void afterRender(Pipeline pipeline, float tpf, int w, int h);

    @Override
    protected void onRun(Pipeline pipeline, float tpf) {
        if (outDepth == null && (outColors == null || outColors.size() == 0)) {
            System.out.println("No outputs " + outColors.size() + " " + this);
            return;
        }

        GLDebuggerAppState.beginSection(getName());

        Renderer renderer = renderManager.getRenderer();

        String otch = renderManager.getForcedTechnique();
        Camera ocam = renderManager.getCurrentCamera();
        boolean oortho = renderManager.isCurrentCameraOrtho();

        FrameBuffer outFb = getFrameBuffer(outColors, outDepth);
        int w = fbFactory.getFrameBufferWidth(outFb);
        int h = fbFactory.getFrameBufferHeight(outFb);

        beforeRender(pipeline, tpf, w, h);
        renderManager.setForcedTechnique(useTechnique);
        renderManager.addForcedMatParam(mrtParam);

        renderManager.setCamera(this.getCamera(), this.orthogonalCam);


        renderer.setFrameBuffer(outFb);
        renderer.clearBuffers(isClearColor(), isClearDepth(), isClearStencil());

        // forEachWorldParam(renderManager::addForcedMatParam);

        WorldParams.updateAndGet(this.getCamera());
        if(this.getTimer()!=null)WorldParams.updateAndGet(this.getTimer(), this.getSpeed());

        onRender(pipeline, tpf, w, h, outFb);

        // forEachWorldParam(renderManager::removeForcedMatParam);

        renderManager.removeForcedMatParam(mrtParam);
        renderManager.setForcedTechnique(otch);
        if (ocam != null) renderManager.setCamera(ocam, oortho);

        afterRender(pipeline, tpf, w, h);

        GLDebuggerAppState.endSection();

    }

}