package com.jme3.rendering.pipeline.jme3.renderer;

import java.util.List;

import com.jme3.math.ColorRGBA;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.renderer.ClearPass;
import com.jme3.rendering.pipeline.jme3.context.*;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;

public class Jme3ClearPass extends Jme3FrameBufferPass<Jme3ClearPass> implements ClearPass<Jme3ClearPass> {
    protected boolean clearColor = false, clearDepth = false, clearStencil = false;
    protected ColorRGBA backgroundColor;
    protected Jme3ContextCreator contextFactory;

    public Jme3ClearPass useBackgroundColor(ColorRGBA c) {
        backgroundColor = c;
        return this;
    }

    public Jme3ClearPass(final Jme3ContextCreator contextFactory, final FrameBufferFactory fbFactory) {
        super();
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
        if(backgroundColor!=null)renderer.setBackgroundColor(backgroundColor);
        renderer.clearBuffers(isClearColor(), isClearDepth(), isClearStencil());
    }

    @Override
    public FrameBuffer getFrameBuffer(List outColors, Texture outDepth) {
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

}