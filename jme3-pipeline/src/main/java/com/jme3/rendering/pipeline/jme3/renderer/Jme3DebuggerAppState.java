package com.jme3.rendering.pipeline.jme3.renderer;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.shader.Shader.ShaderSource;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.util.BufferUtils;

import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;

import static org.lwjgl.opengl.KHRDebug.*;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.lwjgl.opengl.GLUtil;

/**
 * GLDebuggerAppState
 * 
 * Usage:
 *  stateManager.attach(new GLDebuggerAppState());
 *  GLDebuggerAppState.label((Texture)tx);
 *  GLDebuggerAppState.beginSection("Render Main");
 *     ...
 *     GLDebuggerAppState.beginSection("Render Shadows");
 *          ...
 *          ...
 *     GLDebuggerAppState.endSection();
 *     ... 
 *     ...
 *  GLDebuggerAppState.endSection();
 */

public class Jme3DebuggerAppState extends BaseAppState{
    private static int SECTION_ID=0;
    private static volatile boolean ENABLED=false;

    // private static ThreadLocal<LinkedList<Section>> CPUTIME_STACK;
    private static final Map<String,Result> _RESULTS=new ConcurrentHashMap<String,Result>();
    private static final Map<Thread,LinkedList<Section>> CPUTIME_STACK=new ConcurrentHashMap<Thread,LinkedList<Section>>();
    
    private static volatile Thread RENDER_THREAD;

    private static class Result{
        public volatile boolean outUpdated=false;
        public volatile long cpuTime;
    }

    private static class Section{
        public String name;
        public long cTime;
    }

    @Override
    protected void initialize(Application app) {
        
    }

    @Override
    protected void cleanup(Application app) {

    }

    public void reset() {
        // CPUTIME_STACK=ThreadLocal.withInitial(LinkedList<Section>::new);
        _RESULTS.clear();
    }

  

    @Override
    protected void onEnable() {
        ENABLED=true;
        RENDER_THREAD=Thread.currentThread();
        reset();

        glEnable(GL_DEBUG_OUTPUT);

        GLUtil.setupDebugMessageCallback(System.out);

        IntBuffer buf=BufferUtils.createIntBuffer(0);
        glDebugMessageControl(GL_DONT_CARE,GL_DONT_CARE,GL_DONT_CARE,buf,true);
        BufferUtils.destroyDirectBuffer(buf);

    }

    @Override
    protected void onDisable() {
        if(!ENABLED||RENDER_THREAD==null) return;

        ENABLED=false;
        glDisable(GL_DEBUG_OUTPUT);
        reset();
    }

    private static void pushDebugGroup(String label,boolean closeImmediately){
        Result r=_RESULTS.get(label);
        if(r != null){
            r.outUpdated=true;
            double time=r.cpuTime;
            time/=1000000.;
            label+=  String.format(" | CPU %.2f ms",time);
        }
        glPushDebugGroup(GL_DEBUG_SOURCE_APPLICATION,SECTION_ID++,label);
        if(closeImmediately)glPopDebugGroup();
    }
    

    @Override
    public void postRender() {
        if(!ENABLED||RENDER_THREAD==null) return;

        for(Entry<String,Result> rr:_RESULTS.entrySet()){
            Result r=rr.getValue();
            String label=rr.getKey();
            if(!r.outUpdated){
                pushDebugGroup(label,true);
                r.outUpdated=false;
            }
        }
    }

    

    /**
    *  Declare the beginning of a command group
    */
    public static void beginSection(String label) {
        if(!ENABLED||RENDER_THREAD==null) return;
        LinkedList<Section> stack=CPUTIME_STACK.get(Thread.currentThread());
        if(stack==null){
            stack=new LinkedList<Section>();
            CPUTIME_STACK.put(Thread.currentThread(),stack);
        }
        Section s=new Section();
        s.cTime=System.nanoTime();
        s.name=label;
        stack.addFirst(s);
    
        if(Thread.currentThread() == RENDER_THREAD){
            pushDebugGroup(label,false);
        }else{
            s.name+=" ("+Thread.currentThread().getName()+")";
        }
    }

    /**
     *  Declare the beginning of a command group
     */
    public static void beginSection(String label, Object... args) {
        if(!ENABLED||RENDER_THREAD==null)  return;
        beginSection(String.format(label,args));
    }
   

    /**
     * End the last declared command group
     */
    public static void endSection(){
        if(!ENABLED||RENDER_THREAD==null) return;

        LinkedList<Section> stack=CPUTIME_STACK.get(Thread.currentThread());
       
        Section cpuS=stack.removeFirst();

        Result r=_RESULTS.get(cpuS.name);
        if(r==null){
            r=new Result();
            _RESULTS.put(cpuS.name,r);
        }
        r.cpuTime=System.nanoTime()-cpuS.cTime;

        if(Thread.currentThread()==RENDER_THREAD){
            glPopDebugGroup();
        }

    }
    
    /**
     * Set debug label for a FrameBuffer
     */
    public static void label(FrameBuffer fb,String label){
        if(!ENABLED||RENDER_THREAD==null) return;
        glObjectLabel(GL_FRAMEBUFFER,fb.getId(),"Framebuffer "+fb.getId()+" "+label);
    }


    public static void label(ShaderSource sh,String label){
        if(!ENABLED||RENDER_THREAD==null) return;
        System.out.println("Label shader "+"Shader "+sh.getId()+" "+label);
        glObjectLabel(GL_SHADER,sh.getId(),"Shader "+sh.getId()+" "+label);
    }

     /**
     * Set debug label for an image
     */
     public static void label(Image img,String label){
        if(!ENABLED||RENDER_THREAD==null) return;
        glObjectLabel(GL_TEXTURE,img.getId(),"Texture "+img.getId()+" "+label);
    }
}