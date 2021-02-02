package com.jme3.rendering.pipeline.logic;

import java.util.Collection;
import java.util.List;

import com.jme3.input.event.InputEvent;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.scene.control.Control;
import com.jme3.scene.control.InputHandlerControl;
import com.jme3.util.functional.VoidBiFunction;

public class InputEventQueueExecutorPass extends PipelinePass {

    final Collection<InputEvent> inputQueue;
    final Collection<Control> handlers;

    public InputEventQueueExecutorPass(Collection<InputEvent> inputQueue, Collection<Control> handlers) {
        this.inputQueue = inputQueue;
        this.handlers = handlers;
    }

    @Override
    protected void onRun(Pipeline pipeline, float tpf) {
        for (Control c : handlers) {

            if (!(c instanceof InputHandlerControl)) return;
            InputHandlerControl h = (InputHandlerControl) c;
            for (InputEvent e : inputQueue) {

                if (e instanceof KeyInputEvent) {
                    h.onKeyEvent(h.getSpatial(),(KeyInputEvent) e);

                } else if (e instanceof JoyButtonEvent) {
                    h.onJoyButtonEvent(h.getSpatial(),(JoyButtonEvent) e);

                } else if (e instanceof JoyAxisEvent) {
                    h.onJoyAxisEvent(h.getSpatial(),(JoyAxisEvent) e);

                } else if (e instanceof MouseButtonEvent) {
                    h.onMouseButtonEvent(h.getSpatial(),(MouseButtonEvent) e);

                } else if (e instanceof MouseMotionEvent) {
                    h.onMouseMotionEvent(h.getSpatial(),(MouseMotionEvent) e);

                } else if (e instanceof TouchEvent) {
                    h.onTouchEvent(h.getSpatial(),(TouchEvent) e);

                }
            }

        }
    }

    @Override
    protected void preAttach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void postAttach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void preDetach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void postDetach(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeIO(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void afterIO(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeRun(Pipeline pipeline, float tpf) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void afterRun(Pipeline pipeline, float tpf) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {
        // TODO Auto-generated method stub

    }

}
