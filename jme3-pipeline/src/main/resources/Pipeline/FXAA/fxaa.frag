#extension GL_ARB_explicit_attrib_location : enable
#extension GL_ARB_explicit_attrib_location : enable

#import "Pipeline/utils/WorldParams.glsl"
#import "Pipeline/FXAA/fxaa.glsl"


#for i=0..6 ( #ifdef SCENE_$i $0 #endif )
    uniform sampler2D m_Scene$i;
    #ifdef MRT 
        layout(location=$i) 
    #endif 
        out vec4 outScene$i;
#endfor 


uniform float m_SpanMax;
uniform float m_ReduceMul;

bindUBO(Camera,WorldCamera);

noperspective in vec4 FxaaPos;
noperspective in vec2 TexCoord;



void main(){
    #for i=0..6 ( #ifdef SCENE_$i $0 #endif )
        outScene$i = FxaaPixelShader(FxaaPos, m_Scene$i, u_WorldCamera.resolutionInverse,m_ReduceMul,m_SpanMax);
    #endfor 
}