package com.jme3.rendering.pipeline.passes.bloom;

import java.util.ArrayList;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.PipelinePointerFactory;
import com.jme3.rendering.pipeline.PipelineRunner;
import com.jme3.rendering.pipeline.params.primitives.MutableBoolean;
import com.jme3.rendering.pipeline.params.primitives.MutableNumber;
import com.jme3.rendering.pipeline.params.smartobj.SmartObject;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture;
import com.jme3.rendering.pipeline.passes.Effect;
import com.jme3.rendering.pipeline.passes.RenderPass;
import com.jme3.rendering.pipeline.passes.bloom.BloomEffect.BloomLayer;
import com.jme3.system.Timer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;

/**
 * BloomEffectBuilder
 */
public class BloomEffectBuilder {

    public static BloomEffectBuilder newBuilder(RenderManager renderManager, FrameBufferFactory fbFactory, AssetManager assetManager) {
        return new BloomEffectBuilder(renderManager, fbFactory, assetManager);
    }

    public static class BloomLayerBuilder {
        private final int nLayer;
        private final BloomEffectBuilder effectBuilder;
        private final RenderManager renderManager;
        private final FrameBufferFactory fbFactory;
        private final AssetManager assetManager;

        private final BloomLayer layer=new BloomLayer();

        private BloomLayerBuilder(RenderManager renderManager, FrameBufferFactory fbFactory, AssetManager assetManager, BloomEffectBuilder effectBuilder, int nLayer) {
            this.renderManager = renderManager;
            this.fbFactory = fbFactory;
            this.assetManager = assetManager;
            this.nLayer = nLayer;
            this.effectBuilder = effectBuilder;
        }

        public BloomLayerBuilder downscale(float wd, float hd) {
            layer.scale.set(wd,hd);
            return this;
        }

        public BloomLayerBuilder intensity(float v) {
            layer.strength=v;
            return this;
        }
        
        public BloomLayerBuilder newBlurPass(float x,float y) {
            return newBlurPass(new Vector2f(x,y));
        }

        public BloomLayerBuilder newBlurPass(Vector2f directionStrength) {
            BloomPass pass = new BloomPass(renderManager, fbFactory, assetManager);
            pass.direction(directionStrength);
            layer.passes.add(pass);
            return this;
        }

        /**
         * Build this layer
         */
        private void buildLayer() {
            boolean downscale =   layer.scale.x != 1 ||   layer.scale.y != 1;

            boolean extract = nLayer == 0;

            if (downscale) {
                // Add one pass for downscale
                BloomPass pass = new BloomPass(renderManager, fbFactory, assetManager);
                // we will use this pass also to extract
                pass.extract(new MutableBoolean(extract));
                extract = false;
                layer.passes.add(0,pass);
            }

            for (int i = downscale?1:0; i < layer.passes.size(); i++) {
                layer.passes.get(i).extract(new MutableBoolean(extract));
                extract = false; // only first pass extracts.
            }




            effectBuilder.addLayer(layer);
            
        }

        /**
         * Build this layer and create next.
         */
        public BloomLayerBuilder newLayer() {
            buildLayer();
            return effectBuilder.newLayer();
        }

        /**
         * Build the entire effect
         */
        public BloomEffect buildEffect() {
            return effectBuilder.buildEffect();
        }
    }


    private final RenderManager renderManager;
    private final FrameBufferFactory fbFactory;
    private final AssetManager assetManager;
    private final ArrayList<BloomLayer> layers = new ArrayList<BloomLayer>();

    private BloomLayerBuilder lastLayer;

    private BloomEffectBuilder(RenderManager renderManager, FrameBufferFactory fbFactory, AssetManager assetManager) {
        this.renderManager = renderManager;
        this.fbFactory = fbFactory;
        this.assetManager = assetManager;
    }

    private void addLayer(BloomLayer layer) {
        layers.add(layer);
    }

    public BloomLayerBuilder newLayer() {
        return lastLayer = new BloomLayerBuilder(renderManager, fbFactory, assetManager, this, layers.size());
    }

    public BloomEffect buildEffect() {
        if (lastLayer != null) {
            lastLayer.buildLayer();
        }
        return new BloomEffect(renderManager,assetManager,fbFactory, layers.toArray(new BloomLayer[0]));
    }

}