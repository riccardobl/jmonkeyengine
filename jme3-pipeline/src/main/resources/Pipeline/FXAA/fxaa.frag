#extension GL_ARB_explicit_attrib_location : enable

#for i=0..10 ( #ifdef SCENE_$i $0 #endif )
    uniform sampler2D m_Scene$i;
    #ifdef MRT 
        layout(location=$i) 
    #endif 
        out vec4 outScene$i;
#endfor 

#import "Pipeline/FXAA/fxaa.glsl"

uniform vec2 m_ResolutionInverse;
uniform float m_SpanMax;
uniform float m_ReduceMul;

in vec4 FxaaPos;
in vec2 TexCoord;

void fxaa_frag(){
    #for i=0..10 ( #ifdef SCENE_$i $0 #endif )
        outScene$i = FxaaPixelShader(FxaaPos, m_Scene$i, m_ResolutionInverse,m_ReduceMul,m_SpanMax);
    #endfor 
}

void main(){
    fxaa_frag();
}