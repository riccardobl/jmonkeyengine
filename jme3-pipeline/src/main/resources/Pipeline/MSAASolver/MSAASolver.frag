#extension GL_ARB_explicit_attrib_location : enable

noperspective in vec2 TexCoord;

#for i=0..6 ( #if defined(INPUT_$i)  $0 #endif )
    uniform sampler2DMS m_Input$i;
    #ifdef MRT 
        layout(location=$i) 
    #endif 
        out vec4 outColor$i;
#endfor 


// 
#define RESOLVE_METHOD_AVERAGE 0
#define RESOLVE_METHOD_MAX_R 1
#define RESOLVE_METHOD_MIN_R 2
#define RESOLVE_METHOD_FIRST_SAMPLE 3
//


void main(){
    ivec2 iTexC;
    #for i=0..6 ( #if defined(INPUT_$i) $0 #endif )
        iTexC = ivec2(TexCoord * vec2(textureSize(m_Input$i)));
        #if RESOLVE_METHOD_$i == RESOLVE_METHOD_MAX_R
            outColor$i = texelFetch(m_Input$i, iTexC, 0);   
            for (int i = 1; i < NUM_SAMPLES; i++)outColor = max(texelFetch(m_Input$i, iTexC, i),outColor$i);    
        #elif RESOLVE_METHOD_$i == RESOLVE_METHOD_MIN_R
            outColor$i = texelFetch(m_Input$i, iTexC, 0);    
            for (int i = 1; i < NUM_SAMPLES; i++)outColor$i = min(texelFetch(m_Input$i, iTexC, i),outColor$i);    
        #elif RESOLVE_METHOD_$i == RESOLVE_METHOD_FIRST_SAMPLE
            outColor$i=texelFetch(m_Input$i, iTexC, 0);    
        #else
            outColor$i=vec4(0);
            for (int i = 0; i < NUM_SAMPLES; i++)outColor$i += texelFetch(m_Input$i, iTexC, i);    
            outColor$i /= float(NUM_SAMPLES);
        #endif
    #endfor 
}
