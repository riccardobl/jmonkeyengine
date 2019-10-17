package com.jme3.scene.occlusion;

import com.jme3.renderer.QueryObject;
import com.jme3.renderer.RenderManager;

class OcclusionQuery {
    private QueryObject obj;
    private boolean cachedResult = true;

    OcclusionQuery(RenderManager renderManager) {
        obj = new QueryObject(renderManager.getRenderer(), QueryObject.Type.AnySamplesPassed);
    }

    public boolean isReady() {
        return obj.isResultReady();
    }

    public boolean get() {
        return get(false);
    }

    public void begin(){
        obj.begin();
    }

    public void end(){
        obj.end();
    }

    public boolean get(boolean wait) {
        boolean result = cachedResult;
        if (obj.isResultReady() || wait) {
            result = obj.getAndWait() > 0;
            cachedResult = result;
        }
        return result;
    }

}