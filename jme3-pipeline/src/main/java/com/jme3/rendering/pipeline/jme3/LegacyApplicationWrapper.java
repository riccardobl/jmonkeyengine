package com.jme3.rendering.pipeline.jme3;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import com.jme3.app.AppTask;
import com.jme3.app.Application;
import com.jme3.app.LostFocusBehavior;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.Listener;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.rendering.pipeline.jme3.context.Jme3Context;
import com.jme3.rendering.pipeline.jme3.context.Jme3ContextCreator;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.system.Timer;
import com.jme3.texture.FrameBuffer;

public class LegacyApplicationWrapper implements Application {


    AppSettings settings;
    Timer timer;
    AssetManager assetManager;
    InputManager inputManager;
    AppStateManager stateManager;
    Jme3ContextCreator contextFactory;
    Camera cam;
    FrameBuffer outFb;
    Node rootNode;

    public void setRootNode(Node n){
        rootNode=n;
    }

    public void setOutputFramebuffer(FrameBuffer fb){
        outFb=fb;
    }

    public void setCamera(Camera c){
        cam=c;
    }

    public void setContextFactory(Jme3ContextCreator cf){
        contextFactory=cf;
    }

    public void setStateManager(AppStateManager sm){
        stateManager=sm;
    }

    public void setAssetManager(AssetManager am){
        assetManager=am;
    }

    public void setInputManager(InputManager im){
        inputManager =im;
    }



    private final ConcurrentLinkedQueue<AppTask<?>> taskQueue = new ConcurrentLinkedQueue<AppTask<?>>();

    public LostFocusBehavior getLostFocusBehavior() {
        return LostFocusBehavior.Disabled;
    }

    @Override
    public void setLostFocusBehavior(LostFocusBehavior lostFocusBehavior) {

    }

    @Override
    public boolean isPauseOnLostFocus() {
        return false;
    }

    @Override
    public void setPauseOnLostFocus(boolean pauseOnLostFocus) {

    }

    @Override
    public void setSettings(AppSettings settings) {
        this.settings=settings;
    }

    @Override
    public void setTimer(Timer timer) {
        this.timer=timer;
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public AssetManager getAssetManager() {
        return this.assetManager;
    }

    @Override
    public InputManager getInputManager() {
        return inputManager;
    }

    @Override
    public AppStateManager getStateManager() {
        return stateManager;
    }

    @Override
    public RenderManager getRenderManager() {
        return  contextFactory.getContext().getRenderManager();
    }

    @Override
    public Renderer getRenderer() {
        return  contextFactory.getContext().get().getRenderer();
    }

    @Override
    public AudioRenderer getAudioRenderer() {
        return contextFactory.getContext(true).getAudio();
    }

    @Override
    public Listener getListener() {
        return  contextFactory.getContext(true).getAudio().getListener();
    }

    @Override
    public JmeContext getContext() {
        return contextFactory.getContext().get();
    }

    @Override
    public Camera getCamera() {
        return cam;
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub

    }

    @Override
    public void start(boolean waitFor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setAppProfiler(AppProfiler prof) {

    }

    @Override
    public AppProfiler getAppProfiler() {
        return null;
    }

    @Override
    public void restart() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void stop(boolean waitFor) {

    }

    @Override
    public <V> Future<V> enqueue(Callable<V> callable) {
        AppTask<V> task = new AppTask<V>(callable);
        taskQueue.add(task);
        return task;
    }

    @Override
    public void enqueue(Runnable runnable) {
        enqueue(new RunnableWrapper(runnable));

    }

    private class RunnableWrapper implements Callable{
        private final Runnable runnable;

        public RunnableWrapper(Runnable runnable){
            this.runnable = runnable;
        }

        @Override
        public Object call(){
            runnable.run();
            return null;
        }

    }


    @Override
    public ViewPort getGuiViewPort() {
        return null;
    }

    ViewPort virtualViewPort;
    @Override
    public ViewPort getViewPort() {
        if(virtualViewPort==null||virtualViewPort.getCamera()!=cam){
            virtualViewPort=new ViewPort("MainViewport",cam);
        }
        virtualViewPort.clearScenes();
        virtualViewPort.attachScene(rootNode);
        virtualViewPort.setOutputFrameBuffer(outFb);
        virtualViewPort.setClearColor(false);
        virtualViewPort.setClearDepth(false);
        virtualViewPort.setClearStencil(false);
        virtualViewPort.setBackgroundColor(ColorRGBA.BlackNoAlpha);
        return virtualViewPort;
    }
    
}
