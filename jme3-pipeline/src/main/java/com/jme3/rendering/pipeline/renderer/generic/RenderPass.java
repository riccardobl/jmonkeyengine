package com.jme3.rendering.pipeline.renderer.generic;

import java.util.List;

import com.jme3.material.MatParam;
import com.jme3.material.MatParamOverride;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.renderer.GeometryLists;
import com.jme3.scene.Geometry;
import com.jme3.shader.VarType;
import com.jme3.system.Timer;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;

public abstract class RenderPass<T extends RenderPass> extends PipelinePass {
    public abstract T useBackgroundColor(ColorRGBA c);

    public abstract T clearColor(boolean v);

    public abstract T clearDepth(boolean v);

    public abstract T clearStencil(boolean v);

    public abstract boolean isClearColor();

    public abstract boolean isClearDepth();

    public abstract boolean isClearStencil();

    public abstract FrameBuffer getFrameBuffer(List<Texture> outColors, Texture outDepth);

    public abstract T outColors(Texture... colors);
    public abstract T outColor(int i,Texture colors);
    public abstract T resetOutColors();
    public abstract T resetOutDepth();

    public abstract T outDepth(Texture depth);

    public abstract void invalidateFrameBuffer();

    protected  abstract void beforeRender(Pipeline pipeline, float tpf, int w, int h, GeometryLists lists);

    protected abstract void afterRender(Pipeline pipeline, float tpf, int w, int h, GeometryLists lists);

    protected abstract void beforeGeometryListRender(Pipeline pipeline, float tpf, int w, int h, GeometryList list);

    protected abstract void afterGeometryListRender(Pipeline pipeline, float tpf, int w, int h, GeometryList list);

    protected abstract void beforeGeometryRender(Pipeline pipeline, float tpf, int w, int h, Geometry geo);

    protected abstract void afterGeometryRender(Pipeline pipeline, float tpf, int w, int h, Geometry geo);

    protected abstract void renderGeometry(Geometry geom);

    public abstract T useParam(VarType type, String key, Object value);

    public abstract Object clearParam(String key);

    public abstract T useParam(MatParam param);

    public abstract T overrideParam(String key, MatParamOverride override);

    public abstract T useTimer(Timer timer);

    public abstract T useCamera(Camera cam, boolean orthogonal);

    public abstract T useGeometryLists(GeometryLists lists);

    public abstract T forceTechnique(String tech);

    public abstract T forceRenderState(Object renderState);

}