package com.jme3.scene.control;

import java.util.List;

import com.jme3.input.event.InputEvent;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.scene.Spatial;

public interface InputHandlerControl extends Control{
    public void onMouseMotionEvent(Spatial spatial,MouseMotionEvent e);
    public void onKeyEvent(Spatial spatial,KeyInputEvent e);
    public void onMouseButtonEvent(Spatial spatial,MouseButtonEvent e);
    public void onJoyAxisEvent(Spatial spatial,JoyAxisEvent e);
    public void onJoyButtonEvent(Spatial spatial,JoyButtonEvent e);
    public void onTouchEvent(Spatial spatial,TouchEvent e);
}
