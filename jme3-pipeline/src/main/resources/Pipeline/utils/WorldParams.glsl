

#extension GL_ARB_explicit_attrib_location : enable

#define bindUBO(type,name)  layout (std140) uniform bo_##name { \
    type name;\
} 

#define bindSSBO(type,name)  layout (std140) buffer  bo_##name { \
    type name;\
} 


struct Camera{
    vec2 frustumNearFar; 
    vec2 resolutionInverse; 
    vec2 resolution; 
    mat4 viewMatrix; 
    mat4 projectionMatrix; 
    mat4 viewProjectionMatrix; 

    mat4 viewMatrixInverse; 
    mat4 projectionMatrixInverse; 
    mat4 viewProjectionMatrixInverse; 

    vec4 viewPort; 
    float aspect; 
    
    vec3 cameraPosition; 
    vec3 cameraDirection; 
    vec3 cameraLeft; 
    vec3 cameraUp;  
};


struct Geometry{
    mat4 worldMatrix;
    mat4 worldViewMatrix;
    mat3 normalMatrix;
    mat3 worldNormalMatrix;
    mat4 worldViewProjMatrix;
    mat4 worldMatrixInv;
    mat3 worldMatrixInvTrsp;
    mat4 worldViewMatrixInv;
    mat4 worldViewProjMatrixInv;
    mat3 normalMatrixInv;
};
#if defined INSTANCING
    in mat4 inInstanceData;
#endif

mat4 Geometry_getWorldMatrix(in Geometry geo){
    #if defined INSTANCING
        mat4 worldMatrix = mat4(vec4(inInstanceData[0].xyz, 0.0),
        vec4(inInstanceData[1].xyz, 0.0),
        vec4(inInstanceData[2].xyz, 0.0),
        vec4(inInstanceData[3].xyz, 1.0));
        return worldMatrix;
    #else
        return geo.worldMatrix;
    #endif
}







struct Timer{ 
    float speed;
    float time;
    int intTime;
    float tpf;
    float frameRate;
    vec2 deltaTime;
};

