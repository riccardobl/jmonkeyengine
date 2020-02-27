package com.jme3.rendering.pipeline;

import java.util.LinkedHashMap;
import java.util.Map;

import com.jme3.rendering.pipeline.params.PipelineParam;

public abstract class PipelinePass{
    protected int id;

    public void initialize(RenderPipeline pipeline) {

    }

    public void cleanup() {

    }

    public void setId(int id) {
        this.id=id;
    }

    public int getId() {
        return this.id;
    }

    public void postRun(float tpf) {

    }

    public void preAttach() {

    }

    public void postAttach() {

    }

    public void preDetach() {

    }

    public void postDetach() {

    }

    public void preRun(float tpf) {

    }



    public abstract void run(float tpf);

}