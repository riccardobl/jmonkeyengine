package com.jme3.scene.occlusion;

import java.util.Map;
import java.util.WeakHashMap;

import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.renderer.queue.OpaqueComparator;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.occlusion.logic.BasicOcclusionTestLogic;
import com.jme3.scene.occlusion.logic.SimpleGeometriesExclusionLogic;
import com.jme3.scene.occlusion.logic.UseOcclusionTechnique;
import com.jme3.texture.FrameBuffer;

/**
 * OcclusionSceneProcessor
 */
public class OcclusionSceneProcessor implements SceneProcessor {
    RenderManager renderManager;
    ViewPort viewPort;
    boolean initialized=false;
    Map<Spatial,OcclusionQuery> queries=new WeakHashMap<Spatial,OcclusionQuery>();
    GeometryList opaqueOccludables=new GeometryList(new OpaqueComparator()); 
    OcclusionLogic logic=new SimpleGeometriesExclusionLogic().chain(new BasicOcclusionTestLogic()).chain(new UseOcclusionTechnique());

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        renderManager=rm;
        viewPort=vp;
        initialized=true;
    }

    public void setOcclusionLogic(OcclusionLogic logic){
        this.logic=logic;
    }

    public OcclusionLogic getOcclusionLogic(){
        return this.logic;
    }

    public void preBucketFlush(Bucket bucket,RenderQueue rq,Camera cam){
        if(bucket==Bucket.Opaque){
            opaqueOccludables.clear();
            GeometryList glist=rq.getGeometryList(Bucket.Opaque);
            int ngeom=glist.size();
            for (int i=0;i<glist.size();i++){
                Geometry g=glist.get(i);
                opaqueOccludables.add(g);
                OcclusionQuery ocquery=queries.get(g);
                if(ocquery==null)continue;
                if(!ocquery.get())   glist.set(i,null);            
            }
            glist.compact();
            System.out.println("Culled "+(ngeom-glist.size())+" geometries");
        }
    }



    public void postBucketFlush(Bucket bucket,RenderQueue rq,Camera cam){
        if(bucket==Bucket.Opaque){
            GeometryList glist=opaqueOccludables;
            for (Geometry geom:glist){
                assert geom != null;
                OcclusionQuery ocquery=queries.get(geom);
                if(ocquery==null){
                    ocquery=new OcclusionQuery(renderManager);
                    queries.put(geom,ocquery);
                }else{
                    if(!ocquery.isReady())continue;
                }
                Geometry testGeom=logic.getOcclusionGeometry(renderManager,geom);
                ocquery.begin();
                renderManager.renderGeometry(testGeom);
                ocquery.end();
                logic.cleanup(renderManager);
            }
        }

    }
    

    @Override
    public void reshape(ViewPort vp, int w, int h) {

    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void preFrame(float tpf) {

    }

    @Override
    public void postQueue(RenderQueue rq) {

    }

    @Override
    public void postFrame(FrameBuffer out) {

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void setProfiler(AppProfiler profiler) {

    }

    
}