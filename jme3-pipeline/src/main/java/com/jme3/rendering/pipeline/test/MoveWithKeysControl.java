package com.jme3.rendering.pipeline.test;

import java.io.IOException;
import java.util.List;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.input.KeyInput;
import com.jme3.input.event.InputEvent;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.control.InputHandlerControl;
import com.jme3.scene.control.SpatialControl;

public class MoveWithKeysControl extends SpatialControl implements  InputHandlerControl {



    @Override
    public void onKeyEvent(Spatial spatial,KeyInputEvent e){

        if (!e.isPressed()) return;
        if (e.getKeyCode() == KeyInput.KEY_LEFT) {
            spatial.getLocalTransform().getTranslation().addLocal(-10f, 0, 0);
            getSpatial().setLocalTransform(spatial.getLocalTransform());
        } else if (e.getKeyCode() == KeyInput.KEY_RIGHT) {
            spatial.getLocalTransform().getTranslation().addLocal(10f, 0, 0);
            getSpatial().setLocalTransform(spatial.getLocalTransform());
        }
    }

    @Override
    public void onMouseMotionEvent(Spatial spatial, MouseMotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMouseButtonEvent(Spatial spatial, MouseButtonEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onJoyAxisEvent(Spatial spatial, JoyAxisEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onJoyButtonEvent(Spatial spatial, JoyButtonEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTouchEvent(Spatial spatial, TouchEvent e) {
        // TODO Auto-generated method stub

    }


}
