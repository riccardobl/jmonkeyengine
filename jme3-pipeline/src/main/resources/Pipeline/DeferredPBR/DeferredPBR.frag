#extension GL_ARB_explicit_attrib_location : enable

#import "Pipeline/utils/Utils.glsl"
#import "Pipeline/utils/GBufferF.glsl"
#import "Pipeline/utils/WorldParams.glsl"

uniform sampler2D Data1;
uniform sampler2D Data2;
uniform sampler2D Depth;

noperspective in vec2 TexCoord;

bindUBO(Camera,WorldCamera);


#define DEBUG_SHOW DEBUG_SHOW_POSITIONS
#define DEBUG_SHOW_COLOR 1
// #define DEBUG_SHOW_EMISSION 2
#define DEBUG_SHOW_AO 2
#define DEBUG_SHOW_NORMALS 3
#define DEBUG_SHOW_ROUGHNESS 4
#define DEBUG_SHOW_METALLICNESS 5
#define DEBUG_SHOW_DEPTH 6
#define DEBUG_SHOW_POSITIONS 7



layout(location=0) out vec4 outColor;


void main(){
    vec4 data1=texture(Data1,TexCoord);
    vec4 data2=texture(Data2,TexCoord);
    vec4 depth=texture(Depth,TexCoord);
    mat4 viewProjectionMatrixInverse=WorldCamera.viewProjectionMatrixInverse;
    
    GBuffer gbuffer;
    unpackGBuffer(data1,data2,depth.r,TexCoord,viewProjectionMatrixInverse,gbuffer);
    if(gbuffer.discarded){
        discard;
        return;
    }

    #if defined(DEBUG_SHOW)&&DEBUG_SHOW>0
        
        #if DEBUG_SHOW==DEBUG_SHOW_COLOR
            outColor=gbuffer.color;
        #elif DEBUG_SHOW==DEBUG_SHOW_AO
            outColor=vec4(gbuffer.ao);
        #elif DEBUG_SHOW==DEBUG_SHOW_NORMALS
            outColor=vec4(gbuffer.worldNormal,1.0);
        #elif DEBUG_SHOW==DEBUG_SHOW_METALLICNESS
            outColor=vec4(gbuffer.metallicness);    
        #elif DEBUG_SHOW==DEBUG_SHOW_DEPTH
            outColor=vec4(gbuffer.depth);    
        #elif DEBUG_SHOW==DEBUG_SHOW_POSITIONS
            outColor=vec4(gbuffer.worldPosition,1.0);    
        #endif
        return;
    #endif
}