package com.jme3.rendering.pipeline.params.texture;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;

/**
 * Texture3DMutable
 */
public class SmartTexture3D   extends SmartTexture<Texture3D> {
        protected int depth3d=0;

        public SmartTexture3D depth(int v){
            this.depth3d=v;
            this.rebuild=true;
            return this;
        }

        protected SmartTexture3D(Texture3D value){
            super(value);
        }

        @Override
        protected void rebuild(Texture3D value) {
            ArrayList<ByteBuffer> allData=new ArrayList<ByteBuffer>();
            allData.add(null);
            final Image img=new Image(format,width,height,depth3d,allData,mipMapSizes,colorSpace);
            if(numSamples > 1) img.setMultiSamples(numSamples);
            value.setImage(img);
            value.setName(name);
        }
  
    }
