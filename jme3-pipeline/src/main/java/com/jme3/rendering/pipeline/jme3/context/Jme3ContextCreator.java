package com.jme3.rendering.pipeline.jme3.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jme3.audio.AudioContext;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.Listener;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.system.JmeSystem;
import com.jme3.system.SystemListener;
import com.jme3.system.JmeContext.Type;

public class Jme3ContextCreator{
    private ThreadLocal<Jme3Context> local = new ThreadLocal<Jme3Context>().withInitial(()-> new Jme3Context());
    private Type type;
    private AppSettings settings;

    public Jme3ContextCreator(AppSettings settings, Type type) {
        this.settings = settings;
        this.type = type;
    }
    
    public Jme3Context getContext() {
        return getContext(false);
    }

    public Jme3Context getContext(boolean withAudio) {
        Jme3Context context = local.get();
        JmeContext jmeContext=context.get();
        if (jmeContext == null) {
            jmeContext = JmeSystem.newContext(settings, type);
            context.setContext(jmeContext);            
            if(!jmeContext.initInThread()){
                throw new RuntimeException("Can't initialize OpenGL context");
            }
        }
        if(withAudio&&context.getAudio()==null){
            context.setAudio(JmeSystem.newAudioRenderer(settings));
            Listener listener = new Listener();            
            context.getAudio().initialize();
            context.getAudio().setListener(listener);

        }     
        AudioContext.setAudioRenderer(context.getAudio());
        return context;
    }




    public void destroy(){
        Jme3Context context = local.get();
        JmeContext jmeContext=context.get();
        if(jmeContext!=null&&jmeContext.isCreated()){
            jmeContext.deinitInThread();
        }
    }

}