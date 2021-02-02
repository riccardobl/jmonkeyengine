package com.jme3.rendering.pipeline.jme3.renderer;

import java.util.List;

import com.jme3.material.MatParam;
import com.jme3.material.MatParamOverride;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.jme3.context.*;
import com.jme3.rendering.pipeline.renderer.GeometryLists;
import com.jme3.scene.Geometry;
import com.jme3.shader.VarType;
import com.jme3.system.Timer;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;

public class Jme3ClearPass extends Jme3RenderPass<Jme3ClearPass> {
    protected boolean clearColor = false, clearDepth = false, clearStencil = false;
    protected ColorRGBA backgroundColor;
    protected Jme3ContextCreator contextFactory;

    public Jme3ClearPass useBackgroundColor(ColorRGBA c) {
        backgroundColor = c;
        return this;
    }

    public Jme3ClearPass(final Jme3ContextCreator contextFactory, final FrameBufferFactory fbFactory) {
        this.setFrameBufferFactory(fbFactory);
        this.contextFactory = contextFactory;
    }

    @Override
    public boolean isClearColor() {
        return clearColor;
    }

    @Override
    public boolean isClearDepth() {
        return clearDepth;
    }

    @Override
    public boolean isClearStencil() {
        return clearStencil;
    }

    @Override
    public Jme3ClearPass clearColor(boolean v) {
        clearColor = v;
        return this;
    }

    @Override
    public Jme3ClearPass clearDepth(boolean v) {
        clearDepth = v;
        return this;
    }

    @Override
    public Jme3ClearPass clearStencil(boolean v) {
        clearStencil = v;
        return this;
    }

    @Override
    protected void onRun(Pipeline pipeline, float tpf) {
        if (outDepth == null && (outColors == null || outColors.size() == 0)) {
            System.out.println("No outputs " + outColors.size() + " " + this);
            return;
        }

        GLRenderer renderer = (GLRenderer) this.contextFactory.getContext().get().getRenderer();
        Jme3DebuggerAppState.beginSection(getName());

        // Set output framebuffer
        FrameBuffer outFb = getFrameBuffer(outColors, outDepth);
        renderer.setFrameBuffer(outFb);

        // Clear if required.
        if (backgroundColor != null) renderer.setBackgroundColor(backgroundColor);
        renderer.clearBuffers(isClearColor(), isClearDepth(), isClearStencil());
    }

    @Override
    protected void beforeRender(Pipeline pipeline, float tpf, int w, int h, GeometryLists lists) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void afterRender(Pipeline pipeline, float tpf, int w, int h, GeometryLists lists) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeGeometryListRender(Pipeline pipeline, float tpf, int w, int h, GeometryList list) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void afterGeometryListRender(Pipeline pipeline, float tpf, int w, int h, GeometryList list) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeGeometryRender(Pipeline pipeline, float tpf, int w, int h, Geometry geo) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void afterGeometryRender(Pipeline pipeline, float tpf, int w, int h, Geometry geo) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void renderGeometry(Geometry geom) {
        // TODO Auto-generated method stub

    }

    @Override
    public Jme3ClearPass useParam(VarType type, String key, Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Jme3ClearPass useParam(MatParam param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Jme3ClearPass overrideParam(String key, MatParamOverride override) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Jme3ClearPass useTimer(Timer timer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Jme3ClearPass useCamera(Camera cam, boolean orthogonal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Jme3ClearPass useGeometryLists(GeometryLists lists) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Jme3ClearPass forceTechnique(String tech) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Jme3ClearPass forceRenderState(Object renderState) {
        // TODO Auto-generated method stub
        return null;
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

    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object clearParam(String key) {
        // TODO Auto-generated method stub
        return null;
    }


}