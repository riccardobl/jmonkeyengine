package com.jme3.rendering.pipeline.renderer;

import java.util.ArrayList;

import com.jme3.renderer.queue.GeometryList;
import com.jme3.util.SafeArrayList;

public class GeometryLists extends SafeArrayList<GeometryList> {
     
    
    public GeometryLists(){
        super(GeometryList.class);
        
    }
    

}