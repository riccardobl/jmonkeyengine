// #define USE_APPROXIMATED_SQRT 1

#ifdef GCN
    #ifndef _GCN_FAST_MATH_
    #define _GCN_FAST_MATH_

        #if __VERSION__>=330
            #if defined(USE_APPROXIMATED_SQRT)
            
                #ifndef INVSQRT_MAGIC_NUMBER
                    // [0,1] range
                    #define INVSQRT_MAGIC_NUMBER 0x5F341A43   
                    // [0,1000] range
                    // #define INVSQRT_MAGIC_NUMBER 0x5F33E79F
                #endif


                #ifndef SQRT_MAGIC_NUMBER
                    // [0,1] range
                    #define SQRT_MAGIC_NUMBER 0x1FBD1DF5   
                    // [0,1000] range 
                    // #define SQRT_MAGIC_NUMBER 0x1FBD22DF
                #endif


                // invsqrt
                float _gcn_fast_inversesqrt(in float v){
                    return intBitsToFloat(INVSQRT_MAGIC_NUMBER - (floatBitsToInt(v) >> 1));
                }
                float _gcn_fast_inversesqrt(in vec2 v){
                    return intBitsToFloat(INVSQRT_MAGIC_NUMBER - (floatBitsToInt(v) >> 1));
                }
                float _gcn_fast_inversesqrt(in vec3 v){
                    return intBitsToFloat(INVSQRT_MAGIC_NUMBER - (floatBitsToInt(v) >> 1));
                }
                #ifndef fast_inversesqrt
                    #define fast_inversesqrt(x) _gcn_fast_inversesqrt(x)
                #endif



                //sqrt
                float _gcn_fast_sqrt(in float v){
                    return intBitsToFloat(SQRT_MAGIC_NUMBER + (floatBitsToInt(v) >> 1));
                }        
                float _gcn_fast_sqrt(in vec2 v){
                    return intBitsToFloat(SQRT_MAGIC_NUMBER + (floatBitsToInt(v) >> 1));
                }
                float _gcn_fast_sqrt(in vec3 v){
                    return intBitsToFloat(SQRT_MAGIC_NUMBER + (floatBitsToInt(v) >> 1));
                }
                #ifndef fast_sqrt
                    #define fast_sqrt(x) _gcn_fast_sqrt(x)
                #endif

            #endif
        #endif

    #endif
#endif