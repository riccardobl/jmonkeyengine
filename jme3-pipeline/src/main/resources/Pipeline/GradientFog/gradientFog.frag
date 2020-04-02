#extension GL_ARB_explicit_attrib_location : enable

#import "Pipeline/utils/WorldParams.glsl"



#for i=0..6 ( #if defined(SCENE_$i)&&defined(DEPTH_$i)  $0 #endif )
    uniform sampler2D m_Scene$i;
    uniform sampler2D m_Depth$i;
   
    bindUBO(Camera,SceneCamera$i);

    #ifdef MRT 
        layout(location=$i) 
    #endif 
        out vec4 outScene$i;
#endfor 


uniform vec2 m_Density;
uniform vec3 m_FogHeight;
uniform vec3 m_FogOrigin;

uniform sampler2D m_FogGradient;

in vec2 TexCoord;
out vec4 outFragColor;

// world


#import "Pipeline/utils/FastMath.glsl"
#import "Pipeline/utils/Utils.glsl"





vec4 sampleWithFog(in sampler2D sceneTx,in sampler2D depthTx,in vec2 texCoord,
in sampler2D gradientTx,in vec2 intensity,
in Camera cam,
in vec3 fogDirection,in float fogHeight,
in vec3 fogOrigin
){
    vec4 color=texture(sceneTx,texCoord);
    float depth=texture(depthTx,texCoord).r;
    float ldepth=Utils_linearize01Depth(cam.frustumNearFar,depth);


    #ifdef NO_FOG_ON_SKY
    if(ldepth>=0.999999)    return color;
    #endif

    vec3 spos=Utils_getScreenPos(texCoord, depth);
    vec3 wpos=Utils_screenPosToWPos(cam.viewProjectionMatrixInverse,spos);
    float h=clamp(Utils_distancePointPlane(wpos,fogOrigin,fogDirection)/fogHeight,0.,1.);

    vec4 fogGradient=texture(gradientTx,clamp(vec2(ldepth,h)*intensity,0.,1.));
    color.rgb=mix(color.rgb,fogGradient.rgb,mix(fogGradient.a,fogGradient.a,.1));
    return color;

}


void main(){
    vec3 fogDirection;
    float fogHeight;
    fast_lengthAndNormalize(m_FogHeight,fogHeight,fogDirection);

    #for i=0..6 ( #if defined(SCENE_$i)&&defined(DEPTH_$i) $0 #endif )
        outScene$i=sampleWithFog(m_Scene$i,m_Depth$i,TexCoord,m_FogGradient,m_Density,
            u_SceneCamera$i,
            fogDirection,fogHeight,m_FogOrigin        
        );
    #endfor 
}