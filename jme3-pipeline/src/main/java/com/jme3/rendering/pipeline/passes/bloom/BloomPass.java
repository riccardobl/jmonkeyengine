package com.jme3.rendering.pipeline.passes.bloom;

import java.util.ArrayList;

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
public class BloomPass extends MaterialPass<BloomPass> {


    protected BloomPass(RenderManager renderManager, FrameBufferFactory fbFactory, AssetManager assetManager) {
        super(renderManager, fbFactory, null, assetManager, "Pipeline/FastBloom/FastBloom.j3md");

    }

    public BloomPass brightPoint(MutableNumber<Float> brightPoint) {
        useInput("BrightPoint", brightPoint);
        return this;
    }

    public BloomPass direction(Vector2f dir) {
        useInput("BlurDirection", dir);
        return this;
    }

    public BloomPass extract(MutableBoolean v) {
        useInput("Extract", v);
        return this;
    }



    // Vector2f outSize = new Vector2f();
    // ArrayList<Vector2f> inSizes = new ArrayList<Vector2f>();
    // int inSizesI = 0;

    public BloomPass inColors(Texture2D... colors) {
        for(int i=0;;i++)  if(useInput("Scene"+i,null)==null)break; //reset
        for(int i=0;i<colors.length;i++) useInput("Scene" + (i), colors[i]);        
        return this;
    }

    public BloomPass outColors(Texture2D... colors) {
        for(int i=0;;i++)  if(useOutput(RENDER_OUT_COLOR +i,null)==null)break; //reset
        for(int i=0;i<colors.length;i++) useOutput(RENDER_OUT_COLOR + (i), colors[i]);        
        return this;
    }

    @Override
    protected Object onMatParamInput(Pipeline pipeline, String skey, Object value) {
        if (value instanceof Texture) {
            SmartTexture stx = SmartTexture.from(value);

            stx.minFilter(MinFilter.BilinearNoMipMaps);
            stx.magFilter(MagFilter.Bilinear);
            stx.wrapAxis(WrapMode.EdgeClamp, WrapMode.EdgeClamp, WrapMode.EdgeClamp);

            value = stx.get(pipeline, this);

            // if (value instanceof Texture2D) {
            //     while (inSizesI - inSizes.size() >= 0) inSizes.add(new Vector2f());
            //     Texture2D tx = (Texture2D) value;
            //     inSizes.get(inSizesI++).set(tx.getImage().getWidth(), tx.getImage().getHeight());
            // }
            return value;
        }
        return super.onMatParamInput(pipeline, skey, value);
    }

    @Override
    protected Object onMatParamOutput(Pipeline pipeline, int skey, Object value) {
        if (value instanceof Texture) {
            SmartTexture stx = SmartTexture.from(value);

            stx.minFilter(MinFilter.BilinearNoMipMaps);
            stx.magFilter(MagFilter.Bilinear);
            stx.wrapAxis(WrapMode.EdgeClamp, WrapMode.EdgeClamp, WrapMode.EdgeClamp);

            value = stx.get(pipeline, this);

            // if (value instanceof Texture2D) {
            //     Texture2D tx = (Texture2D) value;
            //     assert tx.getImage()!=null;
            //     outSize.set(tx.getImage().getWidth(), tx.getImage().getHeight());
            // }
            return value;
        }
        return super.onMatParamOutput(pipeline, skey, value);
    }

    @Override
    protected void afterIO(Pipeline pipeline) {
        super.afterIO(pipeline);
        // boolean scale = false;

        // while (inSizes.size() > inSizesI)  inSizes.remove(inSizes.size()-1);
        // inSizes.trimToSize();
        // inSizesI=0;
        // if(inSizes.size()==0)return;

        // for (Vector2f ins : inSizes) {
        //     if (!ins.equals(outSize)) {
        //         scale = true;
        //         break;
        //     }
        // }
        // useInput("Scale", scale);
    }

}