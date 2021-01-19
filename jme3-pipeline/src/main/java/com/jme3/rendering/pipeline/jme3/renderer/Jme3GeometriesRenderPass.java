package com.jme3.rendering.pipeline.jme3.renderer;

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
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
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

/**
 * An abstract pass that renders geometry lists with opengl
 * 
 * Params Override Priority (lower to higher) default params Material Parameters
 * Geometry's overrides world overrides
 * 
 * @author Riccardo Balbo
 */
public class Jme3GeometriesRenderPass extends Jme3FrameBufferPass<Jme3GeometriesRenderPass> implements GeometriesRenderPass<Jme3GeometriesRenderPass> {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Jme3GeometriesRenderPass.class.getName());




    protected Jme3ContextCreator contextFactory;

    protected SafeArrayList<MatParamOverride> worldOverrides = new SafeArrayList<MatParamOverride>(MatParamOverride.class);
    protected SafeArrayList<MatParam> defaultParams = new SafeArrayList<MatParam>(MatParam.class);

    protected MatParamOverride MRT = new MatParamOverride(VarType.Boolean, "MRT", false);
    protected MatParamOverride worldCameraParam = new MatParamOverride(VarType.UniformBufferObject, "WorldCamera", null);
    protected MatParamOverride worldTimerParam = new MatParamOverride(VarType.UniformBufferObject, "WorldTimer", null);
    protected MatParamOverride geometryParam = new MatParamOverride(VarType.UniformBufferObject, "CurrentGeometry", null);
    protected BindUnits bindUnits = new BindUnits();

    protected Camera worldCamera;
    protected Timer worldTimer;
    protected boolean orthoMode = false;
    protected String forcedTechnique;
    protected GeometryLists geoLists;
    protected RenderState forcedRenderState;
    protected RenderState aggregatorRenderState=new RenderState();

    public Jme3GeometriesRenderPass( final Jme3ContextCreator contextFactory,final FrameBufferFactory fbFactory) {
        super();
        this.setFrameBufferFactory(fbFactory);
        this.contextFactory=contextFactory;
        useDefaultInput("WorldCamera", worldCameraParam);
        useDefaultInput("WorldTimer", worldTimerParam);
        useDefaultInput("CurrentGeometry", geometryParam);
        useDefaultInput("MRT", MRT);
    }
    

  

    @Override
    public Jme3GeometriesRenderPass useParam(final VarType type,final String key,final Object value){
        final MatParam param=new MatParam(type,key,value);
        useInput(key,param);
        return this;
    }

    @Override
    public Jme3GeometriesRenderPass useParam(final MatParam param){
        useInput(param.getName(),param);
        return this;
    }

    @Override
    public Jme3GeometriesRenderPass overrideParam(final String key,final MatParamOverride override){
        useInput(key,override);
        return this;
    }

    @Override   
    public Jme3GeometriesRenderPass useTimer(final Timer timer){
        useInput(RenderInput.WorldTimer,timer);
        return this;
    } 

    @Override
    public Jme3GeometriesRenderPass useCamera(final Camera cam,final boolean orthogonal){
        useInput(RenderInput.WorldCamera,cam);
        useInput(RenderInput.OrthogonalMode,orthogonal);
        return this;
    } 

    @Override
    public Jme3GeometriesRenderPass useGeometryLists(final GeometryLists lists){
        useInput(RenderInput.GeometryLists,lists);
        return this;
    } 

    @Override
    public Jme3GeometriesRenderPass forceTechnique(final String tech){
        useInput(RenderInput.ForcedTechnique,tech);
        return this;
    } 

    @Override
    public Jme3GeometriesRenderPass forceRenderState(final Object renderState){
        useInput(RenderInput.ForcedRenderState,renderState);
        return this;
    } 



    @Override
    protected void beforeIO(final Pipeline pipeline) {
        super.beforeIO(pipeline);
        worldOverrides.clear();
        defaultParams.clear();

    }

    @Override
    protected void onInput(final Pipeline pipeline, final Object key, final Object value) {
        if (key instanceof RenderInput) {
            switch ((RenderInput) key) {
                case WorldCamera: {
                    worldCamera = (Camera) value;
                    break;
                }
                case WorldTimer: {
                    worldTimer = (Timer) value;
                    break;
                }
                case OrthogonalMode: {
                    orthoMode = (Boolean) value;
                    break;
                }
                case ForcedTechnique: {
                    forcedTechnique = forcedTechnique.toString();
                    break;
                }
                case GeometryLists: {
                    geoLists = (GeometryLists) value;
                    break;
                }
                case ForcedRenderState:{
                    forcedRenderState=(RenderState)value;
                    break;
                }
            }
        } else if(key instanceof String && value instanceof MatParam){
            if(value instanceof MatParamOverride){
                worldOverrides.add((MatParamOverride)value);
            }else{
                defaultParams.add((MatParam)value);
            }
            // else if (key instanceof String || key instanceof Enum) {
            // String name = key.toString();
            // MatParamOverride mpo;
            // if (value instanceof MatParamOverride) {
            //     mpo = (MatParamOverride) value;
            //     worldOverrides.add(mpo);
            // } else {
            //     mpo = generatedMpoCache.get(name);
            //     if (mpo.getValue() != value) mpo = null;
            //     if (mpo == null) {
            //         mpo = MaterialHandler.createMpo(name, value);
            //         generatedMpoCache.put(name, mpo);
            //     }

            //     defaultParams.add(mpo);
            // }
        }
    }

    @Override
    protected void afterIO(final Pipeline pipeline) {
        worldCameraParam.setValue(WorldParams.updateAndGet(worldCamera).get());
        worldTimerParam.setValue(WorldParams.updateAndGet(worldTimer, 1f).get());

        super.afterIO(pipeline);

    }
  
    @Override
    protected void beforeRun(final Pipeline pipeline, final float tpf) {
        this.contextFactory.getContext().get(); // Initialize context before run
    }

    public void renderGeometry(final Geometry geom,ViewPort vp) {        
        geom.runControlRender(this.contextFactory.getContext().getRenderManager(), vp);
        renderGeometry(geom);
    }


    @Override
    public void renderGeometry(final Geometry geom) {

        final GLRenderer renderer= (GLRenderer) this.contextFactory.getContext().get().getRenderer();

        final Mesh mesh = geom.getMesh();
        final int lodLevel = geom.getLodLevel();
        if (geom instanceof InstancedGeometry) { // Managed instance rendering
            final InstancedGeometry instGeom = (InstancedGeometry) geom;
            renderer.renderMesh(mesh, lodLevel, instGeom.getActualNumInstances(), instGeom.getAllInstanceData());
        } else { // Raw rendering
            renderer.renderMesh(mesh, lodLevel, 1, null);
        }
    }

    ViewPort virtualViewPort=null;

    @Override
    protected void onRun(final Pipeline pipeline, final float tpf) {
        if (outDepth == null && (outColors == null || outColors.size() == 0)) {
            System.out.println("No outputs " + outColors.size() + " " + this);
            return;
        }

        final GLRenderer renderer= (GLRenderer) this.contextFactory.getContext().get().getRenderer();
        Jme3DebuggerAppState.beginSection(getName());

        // Set output framebuffer
        final FrameBuffer outFb = getFrameBuffer(outColors, outDepth);
        renderer.setFrameBuffer(outFb);

        // Clear if required.
        // renderer.clearBuffers(isClearColor(), isClearDepth(), isClearStencil());

        final int w = getFrameBufferFactory().getFrameBufferWidth(outFb);
        final int h = getFrameBufferFactory().getFrameBufferHeight(outFb);
        MRT.setValue(outColors.size() > 1);

        beforeRender(pipeline, tpf, w, h, geoLists);

        if(virtualViewPort==null||virtualViewPort.getCamera()!=worldCamera){
            virtualViewPort=new ViewPort(getName(),worldCamera);
        }

        virtualViewPort.setOutputFrameBuffer(outFb);
        virtualViewPort.setClearColor(false);
        virtualViewPort.setClearDepth(false);
        virtualViewPort.setClearStencil(false);
        virtualViewPort.setBackgroundColor(ColorRGBA.BlackNoAlpha);

        

        for (final GeometryList geoList : geoLists) {
            beforeGeometryListRender(pipeline, tpf, w, h, geoList);

            for (int i = 0; i < geoList.size(); i++) {
                final Geometry geom = geoList.get(i);
                final Material material = geom.getMaterial();

                // If no technique is selected -> Select the default.
                if (material.getActiveTechnique() == null) material.selectTechnique(TechniqueDef.DEFAULT_TECHNIQUE_NAME, renderer.getCaps());

                // Force technique
                Technique otech = null;
                if (forcedTechnique != null) {
                    otech = material.getActiveTechnique();
                    material.selectTechnique(forcedTechnique, renderer.getCaps());
                }

                // Get technique
                final Technique tech = material.getActiveTechnique();
                if (tech.getDef().isNoRender()) continue; // Nothing to do here. Continue

                // Set current geometry
                geometryParam.setValue(WorldParams.updateAndGet(worldCamera, geom).get());

                // Overrides
                final SafeArrayList<MatParam> defaultParams = this.defaultParams;
                final SafeArrayList<MatParamOverride> geoOverrides = geom.getWorldMatParamOverrides();
                final SafeArrayList<MatParamOverride> worldOverrides = this.worldOverrides;

                // Get shader (build source)
                final Shader shader = tech.getShader(null, null, renderer.getCaps());

                // Reset uniform state
                MaterialHandler.clearUniformsSetByCurrent(shader);

                // Update uniforms
                MaterialHandler.updateShaderMaterialParameters(material, renderer, shader, defaultParams, geoOverrides, worldOverrides, bindUnits);

                // Clear unused uniforms
                MaterialHandler.resetUniformsNotSetByCurrent(shader);

                // Set render states
                RenderState techRenderState=tech.getDef().getRenderState();
                if(techRenderState==null)techRenderState=RenderState.DEFAULT;

                final RenderState matRenderState=material.getAdditionalRenderState();
                final RenderState forcedRenderState=this.forcedRenderState;
                
                aggregatorRenderState.set(techRenderState);

                if(matRenderState!=null){
                    aggregatorRenderState.copyMergedTo(matRenderState, aggregatorRenderState);
                }

                if(forcedRenderState!=null){
                    aggregatorRenderState.copyMergedTo(forcedRenderState, aggregatorRenderState);
                }               
                renderer.applyRenderState(aggregatorRenderState);

                // Set the shader to use
                renderer.setShader(shader);

                // Render the geometry with the given lod level
                beforeGeometryRender(pipeline, tpf, w, h, geom);
                Jme3DebuggerAppState.beginSection(geom.getName());
                renderGeometry(geom,virtualViewPort);
                Jme3DebuggerAppState.endSection();
                afterGeometryRender(pipeline, tpf, w, h, geom);

                // Reset forced technique
                if (otech != null) {
                    material.selectTechnique(otech.getDef().getName(), renderer.getCaps());
                }
            }
            afterGeometryListRender(pipeline, tpf, w, h, geoList);

        }
        afterRender(pipeline, tpf, w, h, geoLists);
        Jme3DebuggerAppState.endSection();
    }

    @Override
    protected void preAttach(final Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void postAttach(final Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void preDetach(final Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void postDetach(final Pipeline pipeline) {
        // TODO Auto-generated method stub

    }


    @Override
    protected void afterRun(final Pipeline pipeline, final float tpf) {
        // TODO Auto-generated method stub

    }

    @Override
    public FrameBuffer getFrameBuffer(final List outColors, final Texture outDepth) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void beforeRender(final Pipeline pipeline, final float tpf, final int w, final int h, final GeometryLists lists) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterRender(final Pipeline pipeline, final float tpf, final int w, final int h, final GeometryLists lists) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeGeometryListRender(final Pipeline pipeline, final float tpf, final int w, final int h, final GeometryList list) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterGeometryListRender(final Pipeline pipeline, final float tpf, final int w, final int h, final GeometryList list) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeGeometryRender(final Pipeline pipeline, final float tpf, final int w, final int h, final Geometry geo) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterGeometryRender(final Pipeline pipeline, final float tpf, final int w, final int h, final Geometry geo) {
        // TODO Auto-generated method stub

    }

}