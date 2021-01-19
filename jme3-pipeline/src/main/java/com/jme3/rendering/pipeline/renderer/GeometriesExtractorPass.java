package com.jme3.rendering.pipeline.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.jme3.renderer.queue.GeometryList;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.functional.VoidBiFunction;

public class GeometriesExtractorPass extends PipelinePass<GeometriesExtractorPass> {
    public static class Params {
        public static int OutLists = 0;
        public static int Spatial = 1;
        public static int Function = 2;
        public static int Args = 3;
    }
    
    protected final GeometriesExtractor defaultExtractor=new GeometryBucketsExtractor();
    protected GeometryLists out;
    protected Spatial in;
    protected GeometriesExtractor extractor;
    protected final ArrayList<Object> args = new ArrayList<Object>();

    // private Map<String, Association> scenes = new HashMap<String, Association>();

    private void reset() {
        in = null;
        out = null;
        extractor = defaultExtractor;
        args.clear();
        // scenes.clear();
    }

    // private Association get(String key) {
    // Association a = scenes.get(key);
    // if (a == null) {
    // a = new Association();
    // scenes.put(key, a);
    // }
    // return a;
    // }

    public GeometriesExtractorPass useRootSpatial(Spatial sp) {
        useInput(Params.Spatial, sp);
        return this;
    }

    
    public GeometriesExtractorPass outLists(GeometryLists out) {
        useOutput(Params.OutLists, out);
        return this;
    }

    public GeometriesExtractorPass useFunction(GeometriesExtractor ext, Object... args) {
        useInput(Params.Function, ext);
        for (int i = 0; i < args.length; i++) {
            useInput(Params.Args + i, args[i]);
        }
        return this;
    }

    // public GeometriesExtractorPass processScene(
    // Node root,
    // GeometryLists out ,
    // GeometriesExtractor extractor,
    // Object... extractorArgs
    // ){
    // String sceneName=root.getName();
    // return processScene(sceneName,root,out,extractor,extractorArgs);
    // }

    // public GeometriesExtractorPass processScene(
    // String sceneName,
    // Node root,
    // GeometryLists out,
    // GeometriesExtractor extractor,
    // Object... extractorArgs

    // ){
    // useInput(sceneName,extractor);
    // useInput(sceneName,out);
    // useInput(sceneName,root);
    // for(Object a:extractorArgs)useInput(sceneName,a);
    // return this;
    // }

    @Override
    protected void preAttach(Pipeline pipeline) {
    }

    @Override
    protected void postAttach(Pipeline pipeline) {
    }

    @Override
    protected void preDetach(Pipeline pipeline) {
    }

    @Override
    protected void postDetach(Pipeline pipeline) {
    }

    @Override
    protected void beforeRun(Pipeline pipeline, float tpf) {
    }

    @Override
    protected void afterRun(Pipeline pipeline, float tpf) {
    }

    @Override
    protected void beforeIO(Pipeline pipeline) {
        reset();
    }

    @Override
    protected void onInput(Pipeline pipeline, Object keyo, Object value) {
        if (keyo instanceof Number) {
            int key = ((Number) keyo).intValue();
            if (key == Params.Spatial) {
                in = (Spatial) value;
            } else if (key == Params.Function) {
                extractor = (GeometriesExtractor) value;
            } else if (key >= Params.Args) {
                args.add(value);
            }
        }
        // if(value instanceof Spatial){
        // Association a=get(key.toString());
        // a.in=(Node)value;
        // }else if(value instanceof GeometriesExtractor){
        // Association a=get(key.toString());
        // a.extractor=(GeometriesExtractor )value;
        // }else {
        // Association a=get(key.toString());
        // a.args.add(value);
        // }
    }

    @Override
    protected void onOutput(Pipeline pipeline, Object keyo, Object value) {
        if (keyo instanceof Number) {
            int key = ((Number) keyo).intValue();

            if (key == Params.OutLists) {
                out = (GeometryLists) value;
            }
        }
        // if(value instanceof GeometryLists){
        // Association a=get(key.toString());
        // a.out=(GeometryLists)value;
        // }
    }

    @Override
    protected void afterIO(Pipeline pipeline) {
    }

    @Override
    protected void onRun(Pipeline pipeline, float tpf) {
        // for(Association a:scenes.values()){
        assert in != null;
        assert out != null;
        assert extractor != null;

        if (in == null || out == null || extractor == null) return;

        extractor.setArgs(args);
        extractor.clear(out);
        extractor.extract(in, out);
        extractor.sort(out);
    }
    // }

}