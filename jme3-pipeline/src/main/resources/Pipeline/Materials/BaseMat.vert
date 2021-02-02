#import "Pipeline/utils/WorldParams.glsl"
#import "Pipeline/utils/Skinning.glsl"
#import "Pipeline/utils/MorphAnim.glsl"

in vec3 inPosition;
in vec2 inTexCoord;
in vec4 inColor;

out vec2 TexCoord;
out vec3 WorldNormal;
in vec3 inNormal;

#if defined(NORMALMAP) ||defined(PARALLAXMAP) 
    in vec4 inTangent;

    out vec3 WorldBinormal;
    out vec3 WorldTangent;
#endif

// out vec4 VertColor;
// out vec3 WorldPos;

#ifdef HAS_POINTSIZE
    uniform float PointSize;
#endif

bindUBO(Camera,WorldCamera);
bindUBO(Timer,WorldTimer);
bindUBO(Geometry,CurrentGeometry);

void main(){
    TexCoord=inTexCoord;
    // VertColor=inColor;

    #ifdef HAS_POINTSIZE
        gl_PointSize = PointSize;
    #endif

    vec4 modelSpacePos=vec4(inPosition,1.0);
    vec3 modelSpaceNormal=inNormal;

    #if defined(NORMALMAP) ||defined(PARALLAXMAP) 
        vec4 modelSpaceTan= inTangent;
    #endif

     #ifdef NUM_MORPH_TARGETS
       #if defined(NORMALMAP) || defined(PARALLAXMAP)
            Morph_Compute(modelSpacePos, modelSpaceNorm, modelSpaceTan);
        #else
            Morph_Compute(modelSpacePos, modelSpaceNorm);
        #endif
    #endif

    #ifdef NUM_BONES
        #if defined(NORMALMAP) || defined(PARALLAXMAP)
            Skinning_Compute(modelSpacePos, modelSpaceNorm, modelSpaceTan);
        #else
            Skinning_Compute(modelSpacePos, modelSpaceNorm);
        #endif
    #endif

    mat4 worldMatrix=Geometry_getWorldMatrix(CurrentGeometry);
    
    vec4 wpos=(worldMatrix*modelSpacePos);
    // WorldPos=wpos.xyz;

    gl_Position=WorldCamera.viewProjectionMatrix*wpos;
   
    #if defined(NORMALMAP) ||defined(PARALLAXMAP) 
        WorldNormal= normalize(Geometry_transformWorldNormal(CurrentGeometry,modelSpaceNormal.xyz));
        WorldTangent = normalize(Geometry_transformWorldNormal(CurrentGeometry,modelSpaceTan.xyz));
        WorldBinormal= normalize(cross(WorldTangent,WorldNormal))*modelSpaceTan.w;
    #else
        WorldNormal = normalize(Geometry_transformWorldNormal(CurrentGeometry,modelSpaceNormal.xyz));
    #endif
    
}


