package com.jme3.rendering.pipeline.params.texture;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jme3.texture.Image;
import com.jme3.texture.TextureCubeMap;

/**
 * TextureCubeBind
 */
public class SmartTextureCube extends SmartTexture<TextureCubeMap> {
 
    protected SmartTextureCube(TextureCubeMap value){
        super(value);
    }

    @Override
    protected void rebuild(TextureCubeMap value) {
        ArrayList<ByteBuffer> allData=new ArrayList<ByteBuffer>();
        allData.add(null);
        final Image img=new Image(format,width,height,1,allData,mipMapSizes,colorSpace);
        if(numSamples > 1) img.setMultiSamples(numSamples);
       value.setImage(img);
       value.setName(name);

    }
}