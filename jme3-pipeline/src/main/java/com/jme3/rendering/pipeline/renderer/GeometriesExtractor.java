package com.jme3.rendering.pipeline.renderer;

import java.util.List;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public interface GeometriesExtractor {
    public void extract(Spatial spatial,GeometryLists out);
    public void clear(GeometryLists out);
    public void sort(GeometryLists out);
    public void setArgs(List<Object> args);
}