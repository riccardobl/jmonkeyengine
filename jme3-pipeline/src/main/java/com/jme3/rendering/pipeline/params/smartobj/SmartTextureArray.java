package com.jme3.rendering.pipeline.params.smartobj;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jme3.texture.Image;
import com.jme3.texture.TextureArray;
import com.jme3.texture.image.ColorSpace;

/**
 * A SmartObject used to manipulate TextureArray
 * @author Riccardo Balbo
 */
public class SmartTextureArray extends SmartTexture<TextureArray> {
    protected int length=0;


    public SmartTextureArray length(int v){
        this.length=v;
        this.rebuild=true;
        return this;
    }

    protected SmartTextureArray(TextureArray value){
        super(value);
    }

    @Override
    protected void rebuild(TextureArray value) {
        ArrayList<ByteBuffer> allData=new ArrayList<ByteBuffer>();
        allData.add(null);
        for(int i=0;i < length;i++){
            allData.add(null);
        }

        final Image img=new Image(format,width,height,1,allData,mipMapSizes,colorSpace);
        if(numSamples > 1) img.setMultiSamples(numSamples);

        value.setImage(img);
        value.setName(name);

    }
}
    
