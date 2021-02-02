package com.jme3.scene.control;

import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.texture.FrameBuffer;

public interface RenderControl extends Control{
    public void onRender(Spatial spatial,Camera cam,FrameBuffer outFb);



}
