#import "Pipeline/FXAA/fxaa.glsl"

uniform vec2 m_ResolutionInverse;
uniform sampler2D m_Scene;

uniform float m_SpanMax;
uniform float m_ReduceMul;

in vec4 FxaaPos;

out vec4 outFragColor;
in vec2 TexCoord;

void fxaa_frag(){
    outFragColor = FxaaPixelShader(FxaaPos, m_Scene, m_ResolutionInverse,m_ReduceMul,m_SpanMax);

}

void main(){
    fxaa_frag();
}