package com.jme3.rendering.pipeline.jme3.context;

import com.jme3.system.SystemListener;
import com.jme3.util.Function.VoidFunction;
import com.jme3.util.functional.NoArgVoidFunction;

public class OnInitListener implements SystemListener {
    private NoArgVoidFunction onInit;
    public OnInitListener(NoArgVoidFunction onInit){
        this.onInit=onInit;
    }

    @Override
    public void initialize() {
        onInit.eval();
    }

    @Override
    public void reshape(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public void requestClose(boolean esc) {
        // TODO Auto-generated method stub

    }

    @Override
    public void gainFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public void loseFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleError(String errorMsg, Throwable t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
    
}
