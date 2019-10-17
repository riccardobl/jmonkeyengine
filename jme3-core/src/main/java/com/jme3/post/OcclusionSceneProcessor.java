package com.jme3.post;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
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
import com.jme3.scene.shape.Box;
import com.jme3.texture.FrameBuffer;

/**
 * OcclusionSceneProcessor
 */
public class OcclusionSceneProcessor implements SceneProcessor {
    RenderManager renderManager;
    ViewPort viewPort;
    boolean initialized=false;

    class OcclusionQuery{
        QueryObject obj;
        boolean cachedResult=true;
        OcclusionQuery(QueryObject obj){
            this.obj=obj;
        }
    }
    Map<Spatial,OcclusionQuery> queries=new WeakHashMap<Spatial,OcclusionQuery>();
    GeometryList opaqueOccludables=new GeometryList(new OpaqueComparator());

    interface OcclusionLogic {
        public Geometry getOcclusionGeometry(Geometry g);
        public default void cleanup() { };
        public default OcclusionLogic chain(OcclusionLogic logic2) {
            OcclusionLogic logic1=this;
            return new OcclusionLogic() {

                @Override
                public Geometry getOcclusionGeometry(Geometry g) {
                    g = logic1.getOcclusionGeometry(g);
                    return g == null ? null : logic2.getOcclusionGeometry(g);
                }

                @Override
                public void cleanup() {
                    logic1.cleanup();
                    logic2.cleanup();
                }

            };
        }        
    }


    OcclusionLogic occludeWithLodOrBoundingbox = new OcclusionLogic() {
        int olod=-1;
        Box boundingBox=new Box(1f,1f,1f);
        Geometry testGeom=new Geometry("OcclusionGeom",boundingBox);
      
        @Override
        public Geometry getOcclusionGeometry(Geometry geom) {
            olod=-1;
            boolean useLod =  false;//geom.getMesh().getNumLodLevels()>0; // todo make configurable
            if (useLod) {
                olod=testGeom.getLodLevel();
                testGeom.setMesh(geom.getMesh());
                testGeom.setLodLevel(geom.getMesh().getNumLodLevels() - 1);
                testGeom.setLocalTransform(geom.getWorldTransform());
            } else {
                testGeom.setMesh(boundingBox);
                BoundingBox bbox = (BoundingBox) geom.getWorldBound();
                testGeom.setLocalScale(bbox.getExtent(testGeom.getLocalScale()));
                testGeom.setLocalTranslation(bbox.getCenter(testGeom.getLocalTranslation()));
                testGeom.setLocalRotation(Quaternion.IDENTITY);
            }
            testGeom.setMaterial(geom.getMaterial());
            testGeom.updateGeometricState();
            return testGeom;
        }

        @Override
        public void cleanup() {
            if(olod!=-1){
                testGeom.setLodLevel(olod);
            }
            testGeom.setMaterial(null);
            testGeom.setMesh(boundingBox);
            
        }
    };

    OcclusionLogic materialMeshFilter=(geom)->{
        return (
            geom.getMaterial().getMaterialDef().getName().equalsIgnoreCase("unshaded")
            && geom.getMesh().getVertexCount()<500 // todo: we need to check the current lod level, not the main mesh
        )?null:geom;       
    };
    OcclusionLogic logic=materialMeshFilter.chain(occludeWithLodOrBoundingbox);

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
        boolean wait=false;
        if(bucket==Bucket.Opaque){
            opaqueOccludables.clear();
            GeometryList glist=rq.getGeometryList(Bucket.Opaque);
            int ngeom=glist.size();
            for (int i=0;i<glist.size();i++){
                Geometry g=glist.get(i);
                opaqueOccludables.add(g);
                OcclusionQuery ocquery=queries.get(g);
                if(ocquery==null)continue;
                boolean result=ocquery.cachedResult;
                if(ocquery.obj.isResultReady()||wait){
                    result=ocquery.obj.getAndWait()>0;
                    ocquery.cachedResult=result;
                }
                if(!result)    glist.set(i,null);            
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
                OcclusionQuery ocquery=queries.get(geom);
                if(ocquery==null){
                    QueryObject query=new QueryObject(renderManager.getRenderer(),QueryObject.Type.AnySamplesPassed);
                    queries.put(geom,ocquery=new OcclusionQuery(query));
                }else{
                    if(!ocquery.obj.isResultReady())continue;
                }
                Geometry testGeom=logic.getOcclusionGeometry(geom);
                ocquery.obj.begin();
                renderManager.renderGeometry(testGeom);
                ocquery.obj.end();
                logic.cleanup();
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