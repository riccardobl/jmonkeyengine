package com.jme3.post;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

import com.jme3.profile.AppProfiler;
import com.jme3.renderer.Camera;
import com.jme3.renderer.QueryObject;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.renderer.queue.OpaqueComparator;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.FrameBuffer;

/**
 * OcclusionSceneProcessor
 */
public class OcclusionSceneProcessor implements SceneProcessor {
    RenderManager renderManager;
    ViewPort viewPort;
    boolean initialized=false;
    Map<Spatial,QueryObject> queries=new WeakHashMap<Spatial,QueryObject>();
    GeometryList opaqueOccludables=new GeometryList(new OpaqueComparator());

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        renderManager=rm;
        viewPort=vp;
        initialized=true;
    }


    public void preBucketFlush(Bucket bucket,RenderQueue rq,Camera cam){
        if(bucket==Bucket.Opaque){
            opaqueOccludables.clear();
            GeometryList glist=rq.getGeometryList(Bucket.Opaque);
            int ngeom=glist.size();
            for (int i=0;i<glist.size();i++){
                Geometry g=glist.get(i);
                opaqueOccludables.add(g);
                QueryObject query=queries.get(g);
                if(query==null||!query.isResultReady())continue;
                // assert query.isResultReady() : "Query not ready.";
                if(query.getAndWait()<=0)    glist.set(i,null);            
            }
            glist.compact();
            System.out.println("Culled "+(ngeom-glist.size())+" geometries");
            
        }
    }



    public void postBucketFlush(Bucket bucket,RenderQueue rq,Camera cam){
        if(bucket==Bucket.Opaque){
            GeometryList glist=opaqueOccludables;
            String oftech=renderManager.getForcedTechnique();
            renderManager.setForcedTechnique("OcclusionTest");
            for (Geometry geom:glist){
                assert geom != null;
                QueryObject query=queries.get(geom);
                if(query==null){
                    query=new QueryObject(renderManager.getRenderer(),QueryObject.Type.AnySamplesPassed);
                    queries.put(geom,query);
                }
                int olodlev=geom.getLodLevel();
                geom.setLodLevel(geom.getMesh().getNumLodLevels()-1);
                query.begin();
                renderManager.renderGeometry(geom);
                query.end();
                geom.setLodLevel(olodlev);
            }
            renderManager.setForcedTechnique(oftech);
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