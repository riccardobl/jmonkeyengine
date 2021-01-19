package com.jme3.rendering.pipeline.jme3.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jme3.audio.AudioRenderer;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.system.JmeSystem;
import com.jme3.system.SystemListener;
import com.jme3.system.JmeContext.Type;

public class Jme3Context {
    private  JmeContext context;
    private final Collection<SystemListener> listeners=new ArrayList<SystemListener>();
    private final SystemListener globalListener=new GlobalListener(listeners);
    private RenderManager renderManager;
    private AudioRenderer audioRenderer;
    void setContext(JmeContext context){
        this.context=context;
        this.context.setSystemListener(globalListener);
    }
    public void addListener(SystemListener l){
        listeners.add(l);
        if(context!=null){
            l.initialize();
        }
    }
    public void removeListener(SystemListener l){
        listeners.remove(l);
    }
    /**
     * @deprecated Used only for legacy code
     */
    @Deprecated
    public RenderManager getRenderManager(){
        if(renderManager!=null)return renderManager;
        else {
            renderManager=new RenderManager(context.getRenderer());
            return renderManager;
        }
    }
    public JmeContext get(){
        return context;
    }

    void setAudio(AudioRenderer audioRenderer){
        this.audioRenderer=audioRenderer;
    }
    public AudioRenderer getAudio(){
        return audioRenderer;
    }
}