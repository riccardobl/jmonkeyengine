package com.jme3.rendering.pipeline.params.smartobj;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

import com.jme3.asset.AssetProcessor;
import com.jme3.asset.TextureKey;
import com.jme3.asset.cache.AssetCache;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.Type;
import com.jme3.texture.Texture.WrapAxis;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureArray;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.image.ColorSpace;

/**
 * A SmartObject used to manipulate Texture
 * @author Riccardo Balbo
 */
public abstract class SmartTexture<T extends Texture> extends SmartObject<T>{

    static class SmartTextureKey extends TextureKey{
        TextureKey key;
        public SmartTextureKey(final TextureKey txKey){
            key=txKey;
        }
            
        @Override
        public String toString() {
            return key.toString();
        }
        
        @Override
        public Class<? extends AssetCache> getCacheType(){
            return key.getCacheType();
        }
    
        @Override
        public Class<? extends AssetProcessor> getProcessorType(){
            return key.getProcessorType();
        }
        
        @Override
        public boolean isFlipY() {
            return key.isFlipY();
        }

        @Override
        public void setFlipY(final boolean flipY) {
            key.setFlipY(flipY);
        }

        @Override
        public int getAnisotropy() {
            return key.getAnisotropy();
        }

        @Override
        public void setAnisotropy(final int anisotropy) {
           key.setAnisotropy(anisotropy);
        }
    
        @Override
        public boolean isGenerateMips() {
            return key.isGenerateMips();
        }

        @Override
        public void setGenerateMips(final boolean generateMips) {
            key.setGenerateMips(generateMips);
        }
    
        @Override
        public Type getTextureTypeHint() {
            return key.getTextureTypeHint();
        }
    
        @Override
        public void setTextureTypeHint(final Type textureTypeHint) {
            key.setTextureTypeHint(textureTypeHint);
        }
        
        @Override
        public boolean equals(final Object obj) {
          return key.equals(obj);
        }
    
        @Override
        public int hashCode() {
           return key.hashCode();
        }
        
        @Override
        public void write(final JmeExporter ex) throws IOException {
           key.write(ex);
        }
    
        @Override
        public void read(final JmeImporter im) throws IOException {
         key.read(im);
        }

        SmartTexture smartTx;

        void setSmartTexture(SmartTexture tx){
            smartTx=tx;
        }

        public <T extends SmartTexture> T getSmartTexture(){
            return (T)smartTx;
        }
    }

    private static SmartTextureKey getSmartTextureKey(final Texture tx){
        TextureKey txk=(TextureKey)tx.getKey();
        if(txk==null){
            txk=new TextureKey(tx.getName()!=null?tx.getName():"Texture"+tx.hashCode());
        }
        SmartTextureKey stxk;
        if(!(txk instanceof SmartTextureKey)){
            stxk=new SmartTextureKey(txk);
            tx.setKey(stxk);
        }else{
            stxk=(SmartTextureKey)txk;
        }
        return stxk;
    }


    public static SmartTexture2D from(final Texture2D p) {
        SmartTextureKey key=getSmartTextureKey(p);
        SmartTexture2D bind=key.getSmartTexture();
        if(bind == null)key.setSmartTexture(bind=new SmartTexture2D(p));
        return bind;
    }

    public static SmartTexture3D from(final Texture3D p) {
        SmartTextureKey key=getSmartTextureKey(p);
        SmartTexture3D bind=key.getSmartTexture();
        if(bind == null) key.setSmartTexture(bind=new SmartTexture3D(p));
        return bind;
    }

    public static SmartTextureArray from(final TextureArray p) {
        SmartTextureKey key=getSmartTextureKey(p);
        SmartTextureArray bind=key.getSmartTexture();
        if(bind == null) key.setSmartTexture(bind=new SmartTextureArray(p));
        return bind;
    }

    public static SmartTextureCube from(final TextureCubeMap p) {
        SmartTextureKey key=getSmartTextureKey(p);
        SmartTextureCube bind=key.getSmartTexture();
        if(bind == null) key.setSmartTexture(bind=new SmartTextureCube(p));
        return bind;
    }
 
 

    protected MinFilter minFilter=MinFilter.NearestNoMipMaps;
    protected MagFilter magFilter=MagFilter.Nearest;
    protected int anisotropicFilter=0;
    protected WrapMode wrapAxisS=WrapMode.Repeat;
    protected WrapMode wrapAxisT=WrapMode.Repeat;
    protected WrapMode wrapAxisR=WrapMode.Repeat;

    protected int width=2,height=2;
    protected int numSamples=1;
    protected int mipMapSizes[]=null;


    protected boolean rebuild=false;
    protected Format format=Format.RGBA8;
    protected ColorSpace colorSpace=ColorSpace.Linear;
    protected String name=null;







    protected SmartTexture(final T value){
        super(value);
    }



    protected abstract void rebuild(T value);


    private MinFilter ominFilter;
    private MagFilter omagFilter;
    private int oanisotropicFilter;
    private WrapMode owrapAxisS;
    private WrapMode owrapAxisT;
    private WrapMode owrapAxisR;
    
    public T prepareValue(final T value) {
        if(rebuild){
            rebuild(value);
            rebuild=false;
        }

        ominFilter=value.getMinFilter();
        omagFilter=value.getMagFilter();
        oanisotropicFilter=value.getAnisotropicFilter();
        owrapAxisS=value.getWrap(WrapAxis.S);
        owrapAxisT=value.getWrap(WrapAxis.T);
        

        value.setMinFilter(minFilter);
        value.setMagFilter(magFilter);
        value.setAnisotropicFilter(anisotropicFilter);
        value.setWrap(WrapAxis.S, wrapAxisS);
        value.setWrap(WrapAxis.T, wrapAxisT);
        if(!(value instanceof Texture2D)){
            value.setWrap(WrapAxis.R, wrapAxisR);
            owrapAxisR=value.getWrap(WrapAxis.R);
        }
        return value;
    }

    public T releaseValue(final T value){
        value.setMinFilter(ominFilter);
        value.setMagFilter(omagFilter);
        value.setAnisotropicFilter(oanisotropicFilter);
        value.setWrap(WrapAxis.S, owrapAxisS);
        value.setWrap(WrapAxis.T, owrapAxisT);
        if(!(value instanceof Texture2D))value.setWrap(WrapAxis.R, owrapAxisR);
        return value;
    }

   

    public SmartTexture  minFilter(final MinFilter f){
        minFilter=f;
        return this;
    }

    public SmartTexture  magFilter(final MagFilter f){
        magFilter=f;
        return this;
    }

    public SmartTexture  anisotropicFilter(final int f){
        anisotropicFilter=f;
        return this;
    }

    public SmartTexture wrapAxis(final WrapMode s, final WrapMode t, final WrapMode r){
        wrapAxisS=s;
        wrapAxisT=t;
        wrapAxisR=r;
        return this;
    }
    

    public SmartTexture width(final int v){
        this.width=v;
        this.rebuild=true;
        return this;
    }


    public SmartTexture height(final int v){
        this.height=v;
        this.rebuild=true;
        return this;
    }


    public SmartTexture numSamples(final int v){
        this.numSamples=v;
        this.rebuild=true;
        return this;
    }

    public SmartTexture format(final Format v){
        this.format=v;
        this.rebuild=true;
        return this;
    }

    public SmartTexture colorSpace(final ColorSpace v){
        this.colorSpace=v;
        this.rebuild=true;
        return this;
    }


    public SmartTexture name(String v){
        this.name=v;
        this.rebuild=true;
        return this;
    }




    public SmartTexture allocMipMaps(int sizes[]){
        if(sizes.length==0)sizes=null;
        if(sizes!=null&&this.numSamples>1)throw new IllegalArgumentException("Multisample textures do not support mipmaps");
        this.mipMapSizes=sizes;
        this.rebuild=true;
        return this;

    }


 
}