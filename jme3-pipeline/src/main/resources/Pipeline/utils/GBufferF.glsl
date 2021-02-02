
#ifndef _GBUFFER_
#define _GBUFFER_
#import "Pipeline/utils/Utils.glsl"

struct GBuffer{
    vec4 color;
    vec3 emission;
    float ao;
    vec3 worldNormal;
    float roughness;
    float metallicness;
    float depth;
    vec3 worldPosition;
    bool discarded;
};

void packGBuffer(inout GBuffer gbuffer,out vec4 outData1, out vec4 outData2){
    // GBUFFER:
    // (albedo+emission).rgb, ai
    // normalx, normaly, roughness, metallic
    // (depth)
    outData1.rgb=gbuffer.color.rgb+gbuffer.emission.rgb;
    outData1.a=gbuffer.ao;

    outData2.rg=Utils_encodeNormals2C(gbuffer.worldNormal.xyz);
    outData2.b=gbuffer.roughness;
    outData2.a=gbuffer.metallicness;
    
        
    // hardware depth
}

void unpackGBuffer(in vec4 inData1, in vec4 inData2, in float depth,in vec2 uv,in mat4 viewProjectionMatrixInverse,inout GBuffer outGBuffer){
    outGBuffer.discarded=inData2.r<=0.010&&inData2.g<=0.010;
    if(outGBuffer.discarded)return;
    outGBuffer.color=vec4(inData1.rgb,1.0);
    outGBuffer.ao=inData1.a;
    outGBuffer.worldNormal=Utils_decodeNormals2C(inData2.rg);
    outGBuffer.roughness=inData2.b;
    outGBuffer.metallicness=inData2.a;
    outGBuffer.depth=depth;    

    vec3 spos=Utils_getScreenPos(uv,depth);
    outGBuffer.worldPosition=Utils_screenPosToWPos(viewProjectionMatrixInverse,spos);
}

#endif