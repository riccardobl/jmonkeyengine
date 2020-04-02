package com.jme3.rendering.pipeline.passes.bloom;

import java.util.ArrayList;
import java.util.List;

import com.jme3.anim.MorphTrack;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.params.primitives.MutableNumber;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture;
import com.jme3.rendering.pipeline.params.smartobj.SmartTexture2D;
import com.jme3.rendering.pipeline.passes.Effect;
import com.jme3.rendering.pipeline.passes.RenderPass;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Image.Format;

/**
 * BloomEffect
 */
public class BloomEffect extends Effect<BloomEffect> {
    public static class BloomLayer{
        public float strength=1f;
        public final Vector2f scale=new Vector2f();
        public final List<BloomPass> passes=new ArrayList<BloomPass>();
        public final List<Texture2D> layerOut=new ArrayList<Texture2D>();
        public BloomLayer(){
        }
     
    }
    
    private BloomMergePass merger;

    private final BloomLayer[] layers;

    public BloomEffect(RenderManager renderManager,AssetManager assetManager,FrameBufferFactory fbFactory, BloomLayer[] layers) {
        super(fbFactory);
        this.layers=layers;
        for (BloomLayer l : layers) {
            for(BloomPass p:l.passes){
                getEffectPipeline().add(p);
            }
        }
        getEffectPipeline().add(merger=new BloomMergePass(renderManager, fbFactory, assetManager));
        reconnect=true;
    }

    boolean reconnect=false;
    private void reconnectPasses(int n,int w,int h) {
        if(!reconnect)return;
        reconnect=false;
        // Reconnect the effect passes
        int lastW=w;
        int lastH=h;

        int BLOOM_PASS_ID = Integer.MAX_VALUE / 2;        
        
        // Layer outputs, used later for merger.
        List<Texture2D>[] layerOut=new List[n];
        for(int i=0;i<n;i++)   layerOut[i]=new ArrayList<Texture2D>();
        
        List<MutableNumber> strength=new ArrayList<MutableNumber>();


        for (int i = 0; i < layers.length; i++) { 


            BloomLayer layer=layers[i];
            
            // Scale
            lastW*=layer.scale.x;
            lastH*=layer.scale.y;

            // for each pass of the layer
            for(int k=0;k<layer.passes.size();k++){ 
                strength.add(new MutableNumber<Float>(layer.strength));
                
                BloomPass bpass = layer.passes.get(k);

                Texture2D inputs[] = new Texture2D[n];
                Texture2D outputs[] = new Texture2D[n];

                
                for (int j = 0; j < n; j++) {  


                    final int ws=lastW;
                    final  int  hs=lastH;

                    int p = BLOOM_PASS_ID + (100 * j) + bpass.getId();

                    // input from pass before
                    if(bpass.getId()!=0) // first pass gets outputs from effect
                        inputs[j] = getEffectPointerFactory().newPointer(Texture2D.class).abs().to(p -1);

                    // output to pass after
                    outputs[j] = getEffectPointerFactory().newPointer(Texture2D.class,(pp,ps,tx)->{
                        SmartTexture2D txb=SmartTexture.from(tx);
                        txb.format(Format.RGB16F);
                        txb.width(ws);
                        txb.height(hs);                       
                        return txb.get(pp,ps);
                    }).abs().to(p);

                    // If last pass of this layer, then it is a layer out
                    if(k==layer.passes.size()-1)  layerOut[j].add(outputs[j] );
                    

                }

                bpass.inColors(inputs);
                bpass.outColors(outputs);


            }

        }
        merger.inBloomLayers(layerOut);
        merger.strength(strength.toArray(new MutableNumber[0]));
    }

    public BloomEffect inColors(Texture2D... input) {
        for (int i = 0;; i++)   if (useInput("Scene" + i, null) == null)  break; // reset
        // Set in/out for the effect
        for (int i = 0; i < input.length; i++) {
            Texture tx = input[i];
            Object key = "Scene" + (i);
            useInput(0, key, tx); // input to first subpass
            useInput(RenderPass.RENDER_OUT_COLOR + (i++), key, tx); // input to last subpass (for merge)
        }
        reconnect=true;
        return this;
    }

    int nOutputs=0;
    public BloomEffect outColors(Texture2D... output) {
        for (int i = 0;; i++) if (useOutput(RenderPass.RENDER_OUT_COLOR + i, null) == null)  break; // reset
        nOutputs=0;
        // Set in/out for the effect
        for (int i = 0; i < output.length; i++) {
            Texture tx = output[i];
            Object key = RenderPass.RENDER_OUT_COLOR + (i++);
            useOutput(key, tx); // output from last subpass
            nOutputs++;
        }
        reconnect=true;
        return this;
    }

    @Override
    protected void onOutput(Pipeline pipeline, Object key, Object value) {
        if(nOutputs>0&&key.equals(RenderPass.RENDER_OUT_COLOR)){
            Texture tx=(Texture)value;
            reconnectPasses(nOutputs,tx.getImage().getWidth(),tx.getImage().getHeight());
        }
        super.onOutput(pipeline, key, value);

    }
    public BloomEffect brightPoint(MutableNumber<Float> brightPoint) {
        useInput("BrightPoint", brightPoint); // input to all subpasses
        return this;
    }
}