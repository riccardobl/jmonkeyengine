#extension GL_ARB_explicit_attrib_location : enable

noperspective in vec2 TexCoord;

#for i=0..6 ( #if defined(INPUT_$i)  $0 #endif )
    uniform sampler2DMS Input$i;
    #ifdef MRT 
        layout(location=$i) 
    #endif 
        out vec4 outColor$i;
#endfor 

#ifdef RESOLVE_DEPTH
    uniform sampler2DMS InputDepth;
#endif

// 
#define RESOLVE_METHOD_AVERAGE 0
#define RESOLVE_METHOD_MAX_R 1
#define RESOLVE_METHOD_MIN_R 2
#define RESOLVE_METHOD_FIRST_SAMPLE 3
//


vec4 resolveMaxR(in sampler2DMS tx, in ivec2 iTexC){
    vec4 outc = texelFetch(tx, iTexC, 0);   
    for (int i = 1; i < NUM_SAMPLES; i++)outc = max(texelFetch(tx, iTexC, i),outc);  
    return outc;
}

vec4 resolveMinR(in sampler2DMS tx, in ivec2 iTexC){
    vec4 outc = texelFetch(tx, iTexC, 0);   
    for (int i = 1; i < NUM_SAMPLES; i++)outc = min(texelFetch(tx, iTexC, i),outc);  
    return outc;
}


vec4 resolveFirst(in sampler2DMS tx, in ivec2 iTexC){
    vec4 outc = texelFetch(tx, iTexC, 0);   
    return outc;
}

vec4 resolveAverage(in sampler2DMS tx, in ivec2 iTexC){
    vec4 outc=vec4(0);
    for (int i = 0; i < NUM_SAMPLES; i++)outc += texelFetch(tx, iTexC, i);    
    outc /= float(NUM_SAMPLES);
    return outc;
}


void main(){
    ivec2 iTexC;
    #for i=0..6 ( #if defined(INPUT_$i) $0 #endif )
        iTexC = ivec2(TexCoord * vec2(textureSize(Input$i)));
        #if RESOLVE_METHOD_$i == RESOLVE_METHOD_MAX_R
            outColor$i=resolveMaxR(Input$i,iTexC);
        #elif RESOLVE_METHOD_$i == RESOLVE_METHOD_MIN_R
            outColor$i=resolveMinR(Input$i,iTexC);
        #elif RESOLVE_METHOD_$i == RESOLVE_METHOD_FIRST_SAMPLE
            outColor$i=resolveFirst(Input$i,iTexC);
        #else
            outColor$i=resolveAverage(Input$i,iTexC);
        #endif
    #endfor 
    #ifdef RESOLVE_DEPTH
        iTexC = ivec2(TexCoord * vec2(textureSize(InputDepth)));
        
        #if RESOLVE_METHOD_DEPTH == RESOLVE_METHOD_MAX_R
            gl_FragDepth=resolveMaxR(InputDepth,iTexC).r;
        #elif RESOLVE_METHOD_DEPTH == RESOLVE_METHOD_MIN_R
            gl_FragDepth=resolveMinR(InputDepth,iTexC).r;
        #elif RESOLVE_METHOD_DEPTH == RESOLVE_METHOD_FIRST_SAMPLE
            gl_FragDepth=resolveFirst(InputDepth,iTexC).r;
        #else
            gl_FragDepth=resolveAverage(InputDepth,iTexC).r;
        #endif
    #endif
}
