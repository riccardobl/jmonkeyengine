    
uniform vec2 m_FrustumNearFar;

uniform sampler2D m_Depth;
uniform sampler2D m_FogGradient;
uniform sampler2D m_Scene;

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



void fog_main(){      
    outFragColor=texture(m_Scene,TexCoord);

    float depth=texture(m_Depth,TexCoord).r;
    depth=linearize01Depth(depth);

    vec4 fog=texture(m_FogGradient,TexCoord);
    vec4 fogGradient=texture(m_FogGradient,vec2(depth,0));

    outFragColor.rgb=mix(outFragColor.rgb,fogGradient.rgb,fogGradient.a);
}

void main(){
    fog_main();
}