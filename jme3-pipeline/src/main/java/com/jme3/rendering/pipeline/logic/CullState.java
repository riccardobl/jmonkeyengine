package com.jme3.rendering.pipeline.logic;

import com.jme3.util.StatefulObject.State;

public class CullState extends State {
    public volatile boolean culled=false;
    public volatile long cameraStateId=-1;
}   
