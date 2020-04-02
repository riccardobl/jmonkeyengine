#extension GL_ARB_explicit_attrib_location : enable

 #if TONEMAP==0
    #import "Pipeline/ToneMap/alg/Filmic.glsl"
 #elif TONEMAP==1
    #import "Pipeline/ToneMap/alg/HableFilmic.glsl"
 #elif TONEMAP==2
    #import "Pipeline/ToneMap/alg/Lottes2016.glsl"
 #endif

#for i=0..6 ( #ifdef SCENE_$i $0 #endif )
    uniform sampler2D m_Scene$i;
    
    #ifdef EXPOSURE_TEXTURE_$i
        uniform sampler2D m_ExposureTexture$i;
    #else
        uniform float m_Exposure$i;
    #endif

    #ifdef MRT 
        layout(location=$i) 
    #endif 
        out vec4 outScene$i;
#endfor 

noperspective in vec2 TexCoord;
 
void main() {     
    vec4 color;
    float exposure;

    #for i=0..6 ( #ifdef SCENE_$i $0 #endif )

        #ifdef EXPOSURE_TEXTURE_$i
            exposure=texelFetch(m_ExposureTexture$i,ivec2(0,0),0).r;
        #else
            exposure=m_Exposure$i;
        #endif

        color =  texture(m_Scene$i, TexCoord ); 
        tonemap(color.rgb, exposure);

        outScene$i = color;

    #endfor 


 
}


