package com.jme3.rendering.pipeline.jme3.context;

import java.util.ArrayList;
import java.util.Collection;

import com.jme3.system.SystemListener;

public class GlobalListener implements SystemListener {
    private final Collection<SystemListener> listeners;
    boolean initialized=false;
    public GlobalListener(Collection<SystemListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void reshape(int width, int height) {
        for (SystemListener l : listeners) l.reshape(width, height);
    }

    @Override
    public void update() {
        if(!initialized){
            for (SystemListener l : listeners) l.initialize();
            initialized=true;
        }
        for (SystemListener l : listeners) l.update();
    }

    @Override
    public void requestClose(boolean esc) {
        for (SystemListener l : listeners) l.requestClose(esc);
    }

    @Override
    public void gainFocus() {
        for (SystemListener l : listeners) l.gainFocus();
    }

    @Override
    public void loseFocus() {
        for (SystemListener l : listeners) l.loseFocus();

    }

    @Override
    public void handleError(String errorMsg, Throwable t) {
        for (SystemListener l : listeners) l.handleError(errorMsg, t);

    }

    @Override
    public void destroy() {
        for (SystemListener l : listeners) l.destroy();
    }

}