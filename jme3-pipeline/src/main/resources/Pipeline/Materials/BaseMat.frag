#extension GL_ARB_explicit_attrib_location : enable

#import "Pipeline/utils/Utils.glsl"
#import "Pipeline/utils/GBufferF.glsl"


layout(location=0) out vec4 outData1;
layout(location=1) out vec4 outData2;

in vec2 TexCoord;
// in vec3 WorldPos;
in vec3 WorldNormal;
#if defined(NORMALMAP) ||defined(PARALLAXMAP) 
    in vec3 WorldBinormal;
    in vec3 WorldTangent;
#endif

// Base color
uniform vec4 BaseColor;
#ifdef BASECOLORMAP
    uniform sampler2D BaseColorMap;
#endif

// Surface
uniform float Metallic;
uniform float Roughness;
#ifdef AO_ROUGHNESS_METALLIC_MAP
    uniform sampler2D AORoughnessMetallicMap;
#endif
#if defined(NORMALMAP)
  uniform sampler2D NormalMap;   
#endif
#if defined(PARALLAXMAP)
  uniform sampler2D ParallaxMap;   
#endif

// Glow
#ifdef EMISSIVE
    uniform vec4 Emissive;
#endif
#ifdef EMISSIVEMAP
  uniform sampler2D EmissiveMap;
#endif
#if defined(EMISSIVE) || defined(EMISSIVEMAP)
    uniform float EmissiveIntensity;
#endif 


// Alpha
#ifdef DISCARD_ALPHA
  uniform float AlphaDiscardThreshold;
#endif


void main(){
    GBuffer gbuffer;

    #if defined(NORMALMAP) ||defined(PARALLAXMAP) 
        mat3 TBN=mat3(WorldTangent,WorldBinormal,WorldNormal);
    #endif


    // Base color
    gbuffer.color=BaseColor;
    #ifdef BASECOLORMAP
        gbuffer.color*=texture(BaseColorMap,TexCoord);
    #endif


    // Alpha
    #ifdef DISCARD_ALPHA
        if(gbuffer.color.a < AlphaDiscardThreshold){ 
            discard;
            return;
        }
    #endif


    // Glow
    #ifdef EMISSIVEMAP
        vec3 emission=texture(EmissiveMap,TexCoord).rgb;
        #ifdef EMISSIVE
            emission*=Emissive.rgb;
        #endif
        gbuffer.emission=emission * EmissiveIntensity;
    #elif defined(EMISSIVE)
        vec3 emission=Emissive;
        gbuffer.emission=emission * EmissiveIntensity;
    #else
        gbuffer.emission=vec3(0);
    #endif


    // Surface
    gbuffer.ao=1.0;
    gbuffer.roughness=max(Roughness,1e-4);
    gbuffer.metallicness=max(Metallic,0.0);
    #ifdef AO_ROUGHNESS_METALLIC_MAP
        vec4 gbufferMap=texture(AORoughnessMetallicMap,TexCoord);
        gbuffer.ao*=gbufferMap.r;
        gbuffer.roughness*=gbufferMap.g;
        gbuffer.metallicness*=gbufferMap.b;        
    #endif
    

    // Normals
    #if defined(NORMALMAP)
        gbuffer.worldNormal=texture(NormalMap,TexCoord).rgb; 
        #ifdef OPENGL_NORMALMAP
            gbuffer.worldNormal = normalize((gbuffer.worldNormal.xyz * vec3(2.,-2.,2.) - vec3(1.,-1.,1.)));
        #else
            gbuffer.worldNormal = normalize((gbuffer.worldNormal.xyz * vec3(2.,2.,2.) - vec3(1.,1.,1.)));
        #endif
        
        gbuffer.worldNormal=TBN*gbuffer.worldNormal;
    #else
        gbuffer.worldNormal=WorldNormal;
    #endif


    // Position
    // gbuffer.worldPosition=WorldPos.xyz;
    // gbuffer.depth=gl_FragCoord.z;


    // Write to gbuffer
    packGBuffer(gbuffer,outData1,  outData2);

    
}