package com.jme3.rendering.pipeline.jme3.inputs;

import com.jme3.input.InputManager;
import com.jme3.input.JoyInput;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.TouchInput;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.jme3.context.*;
import com.jme3.system.JmeContext;
import com.jme3.app.Application;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.controls.*;
import com.jme3.input.event.*;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.util.IntMap;
import com.jme3.util.IntMap.Entry;
import com.jme3.util.SafeArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JmeInputHandlerPass extends PipelinePass<JmeInputHandlerPass>  {
    private final Jme3ContextCreator contextFactory;
    private final InputManager inputManager;
    private List<InputEvent> outQueue;

    public enum Params {
         OutputQueue
    }
    // private final Vector2f cursorPos = new Vector2f();
    // private boolean eventsPermitted = false;
    // private final IntMap<Long> pressedButtons = new IntMap<Long>();
    // private final IntMap<Float> axisValues = new IntMap<Float>();
    // private final ArrayList<InputEvent> inputQueue = new ArrayList<InputEvent>();

    public JmeInputHandlerPass(InputManager inputManager,Jme3ContextCreator contextFactory) {
        this.contextFactory = contextFactory;
        this.inputManager=inputManager;
    }

  

    public JmeInputHandlerPass outQueue(List<InputEvent> output) {
        useInput(Params.OutputQueue, output);
        return this;
    }

    @Override
    protected void beforeIO(Pipeline pipeline) {
        outQueue = null;
    }

    @Override
    protected void onInput(Pipeline pipeline, Object key, Object value) {
    
    }

    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {
        if (key == Params.OutputQueue) {
            outQueue = (List) value;
        }
    }

    @Override
    protected void onRun(Pipeline pipeline, float tpf) {
        if(!inputManager.isInitialized()){
            JmeContext ctx=contextFactory.getContext().get();
            inputManager.initialize(ctx.getMouseInput(), ctx.getKeyInput(),ctx.getJoyInput(),ctx.getTouchInput());
        }
        inputManager.update(tpf, false);
        if (outQueue != null) {
            outQueue.clear();
            outQueue.addAll(inputManager.getInputQueue());
        }
        inputManager.clearQueue();
        // JmeContext context = contextFactory.getContext().get();
        // MouseInput mouseInput = context.getMouseInput();
        // if (mouseInput != null) {
        // if (!mouseInput.isInitialized()) {
        // mouseInput.initialize();
        // mouseInput.setInputListener(this);
        // }
        // mouseInput.update();
        // }

        // KeyInput keyInput = context.getKeyInput();
        // if (keyInput != null) {
        // if (!keyInput.isInitialized()) {
        // keyInput.initialize();
        // keyInput.setInputListener(this);

        // }
        // keyInput.update();
        // }

        // TouchInput touchInput = context.getTouchInput();
        // if (touchInput != null) {
        // if (!touchInput.isInitialized()) {
        // touchInput.initialize();
        // touchInput.setInputListener(this);

        // }
        // touchInput.update();

        // }

        // JoyInput joyInput = context.getJoyInput();
        // if (joyInput != null) {
        // if (!joyInput.isInitialized()) {
        // joyInput.initialize();
        // joyInput.setInputListener(this);

        // }
        // joyInput.update();
        // }

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

    // @Override
    // public void onJoyAxisEvent(JoyAxisEvent evt) {
    // if (!eventsPermitted) {
    // throw new UnsupportedOperationException("JoyInput has raised an event at an
    // illegal time.");
    // }

    // inputQueue.add(evt);
    // }

    // @Override
    // public void onJoyButtonEvent(JoyButtonEvent evt) {
    // if (!eventsPermitted) {
    // throw new UnsupportedOperationException("JoyInput has raised an event at an
    // illegal time.");
    // }

    // inputQueue.add(evt);
    // }

    // @Override
    // public void onMouseMotionEvent(MouseMotionEvent evt) {
    // /*
    // * If events aren't allowed, the event may be a "first mouse event"
    // * triggered by the constructor setting the mouse listener.
    // * In that case, use the event to initialize the cursor position,
    // * but don't queue it for further processing.
    // * This is part of the fix for issue #792.
    // */
    // cursorPos.set(evt.getX(), evt.getY());
    // if (eventsPermitted) {
    // inputQueue.add(evt);
    // }
    // }

    // @Override
    // public void onMouseButtonEvent(MouseButtonEvent evt) {
    // if (!eventsPermitted) {
    // throw new UnsupportedOperationException("MouseInput has raised an event at an
    // illegal time.");
    // }
    // //updating cursor pos on click, so that non android touch events can properly
    // update cursor position.
    // cursorPos.set(evt.getX(), evt.getY());
    // inputQueue.add(evt);

    // }

    // @Override
    // public void onKeyEvent(KeyInputEvent evt) {
    // if (!eventsPermitted) {
    // throw new UnsupportedOperationException("KeyInput has raised an event at an
    // illegal time.");
    // }

    // inputQueue.add(evt);
    // }

    // @Override
    // public void onTouchEvent(TouchEvent evt) {
    // if (!eventsPermitted) {
    // throw new UnsupportedOperationException("TouchInput has raised an event at an
    // illegal time.");
    // }
    // cursorPos.set(evt.getX(), evt.getY());
    // inputQueue.add(evt);
    // }

}