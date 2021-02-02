#import "Pipeline/utils/WorldParams.glsl"
bindUBO(Camera,WorldCamera);

uniform float SubPixelShift;

in vec4 inPosition;
in vec2 inTexCoord;

noperspective out vec4 FxaaPos;
noperspective out vec2 TexCoord;

void fxaa_vert(){
    vec2 pos = inPosition.xy * 2.0 - 1.0;
    gl_Position = vec4(pos, 0.0, 1.0);    
    TexCoord=inTexCoord;
    FxaaPos.xy = inTexCoord.xy;
    FxaaPos.zw = inTexCoord.xy - (WorldCamera.resolutionInverse * vec2(0.5 + SubPixelShift));
}

void main() {
    fxaa_vert();
}
