package com.jme3.scene.control;

import com.jme3.scene.Spatial;

public interface LogicControl extends Control {
    public void onLogicUpdate(Spatial spatial,float tpf);

  
}
