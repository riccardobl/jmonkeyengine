#extension GL_ARB_explicit_attrib_location : enable

#for i=0..10 ( #if defined(SCENE_$i)&&defined(DEPTH_$i)  $0 #endif )
    uniform sampler2D m_Scene$i;
    uniform sampler2D m_Depth$i;
    #ifdef MRT 
        layout(location=$i) 
    #endif 
        out vec4 outScene$i;
#endfor 

uniform vec2 m_FrustumNearFar;

uniform sampler2D m_FogGradient;

in vec2 TexCoord;
out vec4 outFragColor;


float linearizeDepth(in float depth){
    float f=m_FrustumNearFar.y;
    float n = m_FrustumNearFar.x;
    float d=depth*2.-1.;
    return (2. * n * f) / (f + n - d * (f - n));
}

float linearize01Depth(in float depth){
        float d=linearizeDepth(depth);
        float f=m_FrustumNearFar.y;
        float n = m_FrustumNearFar.x;
        return (d-n)/(f-n);
}

vec4 sampleWithFog(in sampler2D sceneTx,in sampler2D depthTx,in sampler2D gradientTx){
    float depth=linearize01Depth(texture(depthTx,TexCoord).r);
    vec4 fogGradient=texture(gradientTx,vec2(depth,0));
    vec4 color=texture(sceneTx,TexCoord);
    color.rgb=mix(color.rgb,fogGradient.rgb,fogGradient.a);
    return color;
}

void fog_main(){      
    #for i=0..10 ( #if defined(SCENE_$i)&&defined(DEPTH_$i) $0 #endif )
        outScene$i=sampleWithFog(m_Scene$i,m_Depth$i,m_FogGradient);
    #endfor 
}

void main(){
    fog_main();
}