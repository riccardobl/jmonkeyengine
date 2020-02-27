uniform vec2 m_ResolutionInverse;
uniform float m_SubPixelShift;

in vec4 inPosition;
in vec2 inTexCoord;

out vec4 FxaaPos;
out vec2 TexCoord;

void fxaa_vert(){
    vec2 pos = inPosition.xy * 2.0 - 1.0;
    gl_Position = vec4(pos, 0.0, 1.0);    
    TexCoord=inTexCoord;
    FxaaPos.xy = inTexCoord.xy;
    FxaaPos.zw = inTexCoord.xy - (m_ResolutionInverse * vec2(0.5 + m_SubPixelShift));
}

void main() {
    fxaa_vert();
}
