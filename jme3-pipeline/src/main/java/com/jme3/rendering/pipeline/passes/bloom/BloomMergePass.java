package com.jme3.rendering.pipeline.passes.bloom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.params.primitives.MutableBoolean;
import com.jme3.rendering.pipeline.params.primitives.MutableNumber;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture;
import com.jme3.rendering.pipeline.passes.MaterialPass;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;

/**
 * BloomPass
 */
public class BloomMergePass extends MaterialPass<BloomMergePass> {


    protected BloomMergePass(RenderManager renderManager, FrameBufferFactory fbFactory, AssetManager assetManager) {
        super(renderManager, fbFactory, null, assetManager, "Pipeline/FastBloom/BloomMerger.j3md");
    }

   
    public BloomMergePass inColors(Texture2D... colors) {
        for(int i=0;;i++)  if(useInput("Scene"+i,null)==null)break; //reset
        for(int i=0;i<colors.length;i++) useInput("Scene" + (i), colors[i]);        
        return this;
    }


    public BloomMergePass strength(MutableNumber<Float> ... strength) {
        for(int i=0;i<strength.length;i++){
            useInput("Intensity"+i, strength[i]);
        }
        return this;
    }

    public BloomMergePass inBloomLayers(List<Texture2D>... layers) {
        for(int i=0;i<layers.length;i++){
            List<Texture2D> ll=layers[i];
            for(int j=0;j<ll.size();j++){
                Texture2D layer=ll.get(j);
                useInput("BloomLayer"+i+"_"+j, layer);
            }
        }
        return this;
    }



    public BloomMergePass outColors(Texture2D... colors) {
        for(int i=0;;i++)  if(useOutput(RENDER_OUT_COLOR +i,null)==null)break; //reset
        for(int i=0;i<colors.length;i++) useOutput(RENDER_OUT_COLOR + (i), colors[i]);        
        return this;
    }


}