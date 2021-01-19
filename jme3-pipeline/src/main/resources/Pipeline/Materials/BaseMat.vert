#import "Pipeline/utils/WorldParams.glsl"
#import "Pipeline/utils/Skinning.glsl"
#import "Pipeline/utils/MorphAnim.glsl"

in vec3 inPosition;
in vec2 inTexCoord;
in vec4 inColor;

out vec2 TexCoord;
out vec4 VertColor;
out vec3 WorldPos;

#ifdef HAS_POINTSIZE
    uniform float PointSize;
#endif

bindUBO(Camera,WorldCamera);
bindUBO(Timer,WorldTimer);
bindUBO(Geometry,CurrentGeometry);

void main(){
    TexCoord=inTexCoord;
    VertColor=inColor;

    #ifdef HAS_POINTSIZE
        gl_PointSize = PointSize;
    #endif

    vec4 modelSpacePos=vec4(inPosition,1.0);
     #ifdef NUM_MORPH_TARGETS
        Morph_Compute(modelSpacePos);
    #endif

    #ifdef NUM_BONES
        Skinning_Compute(modelSpacePos);
    #endif

    mat4 worldMatrix=Geometry_getWorldMatrix(CurrentGeometry);
    
    vec4 wpos=(worldMatrix*modelSpacePos);
    WorldPos=wpos.xyz;

    gl_Position=WorldCamera.viewProjectionMatrix*wpos;
    // vec4 wnormal=
    // vec4 wtang

    
}


