package com.jme3.rendering.pipeline.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.rendering.pipeline.FrameBufferFactory;
import com.jme3.rendering.pipeline.PipelineRunner;
import com.jme3.rendering.pipeline.RenderPipeline;
import com.jme3.rendering.pipeline.params.PipelineParam;
import com.jme3.rendering.pipeline.params.literalpointers.PipelineLiteralPointers;
import com.jme3.rendering.pipeline.passes.RenderViewPortPass;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.ColorSpace;


public class TestPipeline{



    public static void main(String[] args) {
        PipelineLiteralPointers store=new PipelineLiteralPointers();
        FrameBufferFactory fbFactory=new FrameBufferFactory();
        
        RenderPipeline pipeline=new RenderPipeline();
       
        ViewPort viewPort=null;
        RenderManager renderManager=null;
        
        int width=1024;
        int height=768;
        Format colorFormat=null;
        Format depthFormat=null;
        
        pipeline.add(
            new RenderViewPortPass(renderManager,fbFactory)
            .viewPort(viewPort)
            .outColors(Arrays.asList(
                store.newTexturePointer("-scene"),
                store.newTexturePointer("-normal")
            )    )
            .outDepth(
                store.newTexturePointer("-depth").format(Format.Depth)
            )
        ) ;

        pipeline.add(
            new NumberTransformPass()
            .action((n)->{return n.doubleValue()+10.;})
            .inValue(store.newPointer(Number.class,"-valueA").value(10.f) )
            .outValue(store.newPointer(Number.class,">valueA") )
        );

        pipeline.add(
            new NumberTransformPass()
            .action((n)->{return n.doubleValue()+2.;})
            .inValue(store.newPointer(Number.class,"-valueB").value(2.) )
            .outValue(store.newPointer(Number.class,">valueB") )
        );

        pipeline.add(
            new NumberTransformPass()
            .action((n)->{return n.doubleValue()*2.;})
            .inValue(store.newPointer(Number.class,"<valueA") )
            .outValue(store.newPointer(Number.class,">valueA") )
        );

        pipeline.add(
            new PrintPass()
            .desc("Value A: ")
            .value(store.newPointer(Number.class,"<valueA") )
        );

        pipeline.add(
            new PrintPass()
            .desc("Value B: ")
            .value(store.newPointer(Number.class,"<valueB") )
        );



        PipelineRunner runner=new PipelineRunner();
        runner.addPipeline(pipeline);
        runner.run(0);
        // pipeline.initialize();
        
        // float tpf=1f;
        // pipeline.run(tpf);

        // pipeline.cleanup();
    }
}