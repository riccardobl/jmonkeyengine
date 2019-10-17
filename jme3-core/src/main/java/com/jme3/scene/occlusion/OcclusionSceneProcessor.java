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
    protected RenderManager renderManager;
    protected ViewPort viewPort;
    protected boolean initialized=false;
    protected Map<Spatial,OcclusionQuery> queries=new WeakHashMap<Spatial,OcclusionQuery>();
    protected OcclusionLogic logic=new SimpleGeometriesExclusionLogic().chain(new BasicOcclusionTestLogic()).chain(new UseOcclusionTechnique());
    protected GeometryList occludables=new GeometryList(new OpaqueComparator()); 

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

    private GeometryList filterInPlaceAndGetOccludableList(GeometryList glist){
        // reuse "occludables" list  to reduce garbage
        int ngeom=glist.size(); // debug

        // We remove all the occluded geometries from the geometry list.
        // While doing so we populate another geometry list with all the geometries
        // including those that are occluded, since we will need it later for the occlusion test. 
        // note: there is probably a better way to do this
       for (int i=0;i<glist.size();i++){
            Geometry g=glist.get(i);
            occludables.add(g);

            OcclusionQuery ocquery=queries.get(g);
            if(ocquery==null)continue; // skip if query is not initialized yet.

            // set element to null (defacto mark for removal) if culled
            if(!ocquery.get())   glist.set(i,null);            
        }

        // Compact the list and remove all the nulls.
        glist.compact();

        System.out.println("Culled "+(ngeom-glist.size())+" geometries");// debug

        return occludables;
    }


    private void doOcclusionTest(GeometryList glist,boolean flush){
        for (Geometry geom:glist){
            assert geom != null;

            OcclusionQuery ocquery=queries.get(geom);
            if(ocquery==null){ 
                // Initialize the query if it doesn't exist
                ocquery=new OcclusionQuery(renderManager);
                queries.put(geom,ocquery);
            }else  if(!ocquery.isReady())continue; // Skip if we are still waiting for the results of the previous test.
        
            // Get the occlusion geometry from the logic
            Geometry testGeom=logic.getOcclusionGeometry(renderManager,geom);

            // Occlusion test.
            ocquery.begin();
            renderManager.renderGeometry(testGeom);
            ocquery.end();
            //

            // logic cleanup
            logic.cleanup(renderManager);
        }

        if(flush)glist.clear();

    }

    public void preBucketFlush(Bucket bucket,RenderQueue rq,Camera cam){
        if(bucket==Bucket.Opaque){
            GeometryList glist=rq.getGeometryList(Bucket.Opaque);
            occludables=filterInPlaceAndGetOccludableList(glist);
            // Occlusion test is done later after all the opaque geometries have been rendered.
        }else if(bucket==Bucket.Transparent){ // nb in the engine the transparent bucket is rendered After the opaque bucket.
            GeometryList glist=rq.getGeometryList(Bucket.Transparent);
            occludables=filterInPlaceAndGetOccludableList(glist);
            // We do the occlusion test now, because we want to test transparent geometries only against
            // opaque geometries. A transparent mesh can't occlude another transparent mesh but opaque geometries
            // can fully occlude transparent geometries.
            doOcclusionTest(occludables,true);
        }
    }


    public void postBucketFlush(Bucket bucket,RenderQueue rq,Camera cam){
        if(bucket==Bucket.Opaque){
           doOcclusionTest(occludables,true);
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