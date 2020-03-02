package com.jme3.rendering.pipeline.params.texture;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;

/**
 * Texture2DMutable
 */
public class SmartTexture2D extends SmartTexture<Texture2D> {
    protected SmartTexture2D(Texture2D value){
        super(value);
    }

    @Override
    protected void rebuild(Texture2D value) {
        ArrayList<ByteBuffer> allData=new ArrayList<ByteBuffer>();
        allData.add(null);
        final Image img=new Image(format,width,height,1,allData,mipMapSizes,colorSpace);
        if(numSamples > 1) img.setMultiSamples(numSamples);
        value.setImage(img);
        value.setName(name);

    }

}