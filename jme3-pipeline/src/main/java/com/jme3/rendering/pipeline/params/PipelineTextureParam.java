package com.jme3.rendering.pipeline.params;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureArray;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapAxis;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.BufferUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.params.PipelineParam;
import com.jme3.rendering.pipeline.params.PipelineTextureParam;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureArray;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapAxis;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.BufferUtils;
/**
 * SimplePipelineParam
 */
public class PipelineTextureParam extends PipelineParam<Texture>{


    public static   PipelineTextureParam from(PipelineParam p){
        if(p instanceof PipelineTextureParam)return (PipelineTextureParam)p;
        return new PipelineTextureParam((Texture)p.getValue());
    }

    public static   PipelineTextureParam from(Texture p){
        return new PipelineTextureParam(p);
    }

    public static   PipelineTextureParam newEmpty(){
        return new PipelineTextureParam();
    }

    private Texture value;

    protected MinFilter minFilter=MinFilter.NearestNoMipMaps;
    protected MagFilter magFilter=MagFilter.Nearest;
    protected int anisotropicFilter=0;
    protected WrapMode wrapAxisS=WrapMode.Repeat;
    protected WrapMode wrapAxisT=WrapMode.Repeat;
    protected WrapMode wrapAxisR=WrapMode.Repeat;

    protected int width=2,height=2;
    protected int numSamples=1;
    protected int mipMapSizes[]=null;


    protected boolean rebuild=true;
    protected Format format=Format.RGBA8;
    protected ColorSpace colorSpace=ColorSpace.Linear;

    protected int depth3d=0;
    protected int arrayElements=0;
    protected boolean  isCube=false;
    protected boolean initialized=false;



    protected PipelineTextureParam(Texture value){
        super(value);
        this.rebuild=false;
        this.initialized=true;
    }

    protected PipelineTextureParam(){}

    // Consumer<List<ByteBuffer>> memoryTransform=null;

    protected Texture rebuild(){
        ArrayList<ByteBuffer> allData=new ArrayList<ByteBuffer>();
        allData.add(null);
        // if(memoryTransform!=null){
        //     allData=new ArrayList<ByteBuffer>();

        //     int size=0;
        //     final int Bpp=format.getBitsPerPixel()/8;       

            
        //     size+=width*height*Bpp*(depth3d>0?depth3d:1);
            

        //     if(mipMapSizes!=null){
        //         for(final int s:mipMapSizes){
        //             size+=s*s*Bpp;
        //         }
        //     }

        //     assert size>0:"Image data size cannot be <= 0";

        //     if(isCube){
        //         for(int i=0;i<6;i++)allData.add(BufferUtils.createByteBuffer(size));
        //     }else{
        //         allData.add(BufferUtils.createByteBuffer(size));
        //     }
            
        //     memoryTransform.accept(allData);
        // }
        this.initialized=true;

        if(depth3d>0){
            final Image img=new Image(format, width, height, (depth3d>0?depth3d:1),  allData,    mipMapSizes, colorSpace);
            if(numSamples>1)img.setMultiSamples(numSamples);
            return new Texture3D(img);
        }else if(isCube){
            final Image img=new Image(format, width, height, (depth3d>0?depth3d:1),  allData,    mipMapSizes, colorSpace);
            if(numSamples>1) img.setMultiSamples(numSamples);
            return new TextureCubeMap(img);
        }else if(arrayElements>0){
            final ArrayList<Image> imgs=new ArrayList<Image>();
            for(int i=0;i<arrayElements;i++){
                final Image img=new Image(format, width, height, (depth3d>0?depth3d:1),  allData,    mipMapSizes, colorSpace);
                if(numSamples>1)img.setMultiSamples(numSamples);
                imgs.add(img);
            }
            return new TextureArray(imgs);
        }else{
            final Image img=new Image(format, width, height, (depth3d>0?depth3d:1),  allData,    mipMapSizes, colorSpace);
           if(numSamples>1) img.setMultiSamples(numSamples);
            return new Texture2D(img);
        }
    }

    public boolean isInitialized(){
        return         this.initialized;
    }

    public void setInitialized(){
              this.initialized=true;
    }

    @Override
    public Texture getValue() {
        if(rebuild){
             value=rebuild();
            rebuild=false;
        }

        value.setMinFilter(minFilter);
        value.setMagFilter(magFilter);
        value.setAnisotropicFilter(anisotropicFilter);
        value.setWrap(WrapAxis.S, wrapAxisS);
        value.setWrap(WrapAxis.T, wrapAxisT);
        assert width!=2:"Invalid width "+this;
        if(!(value instanceof Texture2D))value.setWrap(WrapAxis.R, wrapAxisR);
        return value;
    }

    @Override
    public void setValue(Texture v) {
        this.value=(Texture)v;
        rebuild=false;
    }


    public PipelineTextureParam  minFilter(final MinFilter f){
        minFilter=f;
        return this;
    }

    public PipelineTextureParam  magFilter(final MagFilter f){
        magFilter=f;
        return this;
    }

    public PipelineTextureParam  anisotropicFilter(final int f){
        anisotropicFilter=f;
        return this;
    }

    public PipelineTextureParam wrapAxis(final WrapMode s, final WrapMode t, final WrapMode r){
        wrapAxisS=s;
        wrapAxisT=t;
        wrapAxisR=r;
        return this;
    }
    

    public PipelineTextureParam width(int v){
        this.width=v;
        this.rebuild=true;
        return this;
    }


    public PipelineTextureParam height(int v){
        this.height=v;
        this.rebuild=true;
        return this;
    }


    public PipelineTextureParam numSamples(int v){
        this.numSamples=v;
        this.rebuild=true;
        return this;
    }

    public PipelineTextureParam format(Format v){
        this.format=v;
        this.rebuild=true;
        return this;
    }

    public PipelineTextureParam colorSpace(ColorSpace v){
        this.colorSpace=v;
        this.rebuild=true;
        return this;
    }

    public PipelineTextureParam depth(int v){
        this.depth3d=v;
        this.rebuild=true;
        return this;
    }

    public PipelineTextureParam length(int v){
        this.arrayElements=v;
        this.rebuild=true;
        return this;
    }

    public PipelineTextureParam cubeMap(boolean v){
        this.isCube=v;
        this.rebuild=true;
        return this;
    }

    public PipelineTextureParam alloc2D(final int width,final int height,final Format format,final ColorSpace colorSpace,final int numSamples){
        this.width=width;
        this.height=height;
        this.format=format;
        this.colorSpace=colorSpace;
        this.numSamples=numSamples;        
        this.mipMapSizes=null;
        this.rebuild=true;
        this.initialized=true;
        return this;

    }


    public PipelineTextureParam alloc3D(final int width,final int height,final int depth,final Format format,final ColorSpace colorSpace,final int numSamples){
        this.width=width;
        this.height=height;
        this.format=format;
        this.colorSpace=colorSpace;
        this.numSamples=numSamples;        
        this.depth3d=depth;
        this.mipMapSizes=null;
        this.rebuild=true;
        this.initialized=true;
        return this;
    }

  

    public PipelineTextureParam allocCube(final int width,final int height,final Format format,final ColorSpace colorSpace,final int numSamples){
        this.width=width;
        this.height=height;
        this.format=format;
        this.colorSpace=colorSpace;
        this.numSamples=numSamples;        
        this.isCube=true;
        this.mipMapSizes=null;
        this.rebuild=true;
        this.initialized=true;
        return this;

    }

    public PipelineTextureParam allocArray(final int width,final int height,final int length,final Format format,final ColorSpace colorSpace,final int numSamples){
        this.width=width;
        this.height=height;
        this.format=format;
        this.colorSpace=colorSpace;
        this.numSamples=numSamples;        
        this.arrayElements=length;
        this.mipMapSizes=null;
        this.rebuild=true;
        this.initialized=true;
        return this;

    }


    public PipelineTextureParam allocMipMaps(int sizes[]){
        if(sizes.length==0)sizes=null;
        if(sizes!=null&&this.numSamples>1)throw new IllegalArgumentException("Multisample textures do not support mipmaps");
        this.mipMapSizes=sizes;
        this.rebuild=true;
        return this;

    }


 
}