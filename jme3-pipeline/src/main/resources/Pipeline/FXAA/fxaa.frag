#extension GL_ARB_explicit_attrib_location : enable

#import "Pipeline/utils/WorldParams.glsl"
#import "Pipeline/FXAA/fxaa.glsl"


#for i=0..6 ( #ifdef SCENE_$i $0 #endif )
    uniform sampler2D Input$i;
    #ifdef MRT 
        layout(location=$i) 
    #endif 
        out vec4 outScene$i;
#endfor 

#ifdef DEPTH
    uniform sampler2D InputDepth;
#endif


uniform float SpanMax;
uniform float ReduceMul;

bindUBO(Camera,WorldCamera);

noperspective in vec4 FxaaPos;
noperspective in vec2 TexCoord;



void main(){
    #for i=0..6 ( #ifdef SCENE_$i $0 #endif )
        outScene$i = FxaaPixelShader(FxaaPos, Input$i, WorldCamera.resolutionInverse,ReduceMul,SpanMax);
    #endfor 

    #ifdef DEPTH
        gl_FragDepth = FxaaPixelShader(FxaaPos, InputDepth$i, WorldCamera.resolutionInverse,ReduceMul,SpanMax).r;
    #endif
}