package com.jme3.rendering.pipeline.renderer;

import java.util.List;

import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.GeometryComparator;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.renderer.queue.OpaqueComparator;
import com.jme3.renderer.queue.TransparentComparator;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.rendering.pipeline.logic.CullState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class GeometryBucketsExtractor implements GeometriesExtractor {
    private  static final java.util.logging.Logger logger =  java.util.logging.Logger.getLogger( GeometryBucketsExtractor.class.getName());
    private final GeometryComparator opaqueComparator = new OpaqueComparator();
    private final GeometryComparator transparentComparator = new TransparentComparator();
    private  Camera cam;

    public GeometryBucketsExtractor() {
    }


    @Override
    public void setArgs(List<Object> args){
        cam=(Camera)args.get(0);
    }

    @Override
    public void clear(GeometryLists out) {
        out.reduceToSize(2);
        for (GeometryList l : out) {
            for (Spatial s : l) {
                s.queueDistance = Float.NEGATIVE_INFINITY;
            }
            l.clear();
        }
    }

    @Override
    public void sort(GeometryLists out) {
        for (GeometryList l : out) {
            l.setCamera(cam);
            l.sort();
        }
    }

    private GeometryList getList(GeometryLists out, Bucket bucket) {
        switch (bucket) {
            case Opaque: {
                GeometryList list = out.expandAndGet(0, () -> new GeometryList(opaqueComparator));
                list.setComparator(opaqueComparator);
                return list;
            }
            case Transparent: {
                GeometryList list = out.expandAndGet(1, () -> new GeometryList(opaqueComparator));
                list.setComparator(transparentComparator);
                return list;
            }
            default:
        }
        return null;
    }

    private void addToQueue(Geometry g, Bucket bucket, GeometryLists out) {
        GeometryList list = getList(out, bucket);
        if (list != null) list.add(g);
    }

    @Override
    public void extract(Spatial root, GeometryLists out) {
        CullState cullState=root.getState(CullState.class, CullState::new);
        if(cullState.culled)  return;
     
        // scene.runControlRender(this, vp);
        if (root instanceof Node) {
            // Recurse for all children
            Node n = (Node) root;
            List<Spatial> children = n.getChildren();
            // Saving cam state for culling
            // int camState = cam.getPlaneState();
            for (int i = 0; i < children.size(); i++) {
                // Restoring cam state before proceeding children recursively
                // cam.setPlaneState(camState);
                extract(children.get(i), out);
            }
        } else if (root instanceof Geometry) {
            // add to the render queue
            Geometry gm = (Geometry) root;
            if (gm.getMaterial() == null) {
                throw new IllegalStateException("No material is set for Geometry: " + gm.getName());
            }
            addToQueue(gm, root.getQueueBucket(), out);
        }
    }

}