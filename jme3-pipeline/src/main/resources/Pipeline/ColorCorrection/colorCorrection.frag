#extension GL_ARB_explicit_attrib_location : enable

 #ifdef TONEMAP==0
    #import "Pipeline/Tonemap/alg/Filmic.glsl"
 #elif TONEMAP==1
    #import "Pipeline/Tonemap/alg/HableFilmic.glsl"
 #elif TONEMAP==2
    #import "Pipeline/Tonemap/alg/Lottes2016.glsl"
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

void gammaCorrection(inout vec3 color, in float gamma){
	color = pow(color, vec3(1.0/gamma)); 
}

 
void main() {     
    vec4 color;
    float exposure;

    #for i=0..6 ( #ifdef SCENE_$i $0 #endif )


        #ifdef GAMMA_CORRECTION
            gammaCorrection(color.rgb,GAMMA_$i);
        #endif

        outScene$i = color;

    #endfor 


 
}


