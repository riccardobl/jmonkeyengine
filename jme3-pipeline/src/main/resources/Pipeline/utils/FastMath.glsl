#import "Pipeline/utils/GCNFastMath.glsl"

#ifndef _FAST_MATH_
#define _FAST_MATH_
    #ifndef PI
        #define PI 3.141592653589793
    #endif

    #ifndef PI2
        #define PI2 6.283185307179586
    #endif

    #ifndef PI0_5
        #define PI0_5 1.5707963267948966
    #endif

    #ifndef fast_inversesqrt 
        #define fast_inversesqrt(x) inversesqrt(x)
    #endif

    #ifndef fast_sqrt
        #define fast_sqrt(x) sqrt(x)
    #endif

    #ifndef fast_lengthAndNormalize
        void _generic_fast_lengthAndNormalize(in vec3 vec,out float outLength,out vec3 outNormal){
            float dotv=dot(vec,vec);
            float invl=fast_inversesqrt(dotv);
            outNormal=vec*invl;
            outLength=invl*dotv;
        }
        #define fast_lengthAndNormalize(vec,l,n) _generic_fast_lengthAndNormalize(vec,l,n)
    #endif

    /* [Eberly2014] GPGPU Programming for Games and Science */
    #ifndef fast_acos
        float _generic_fast_acos(float v){
            float res = -0.156583 * abs(v) + PI0_5;
            res *= fast_sqrt(1.0 - abs(v));
            return (v >= 0) ? res : PI - res;
        }
        vec2 _generic_fast_acos(vec2 v){
            vec2 res = -0.156583 * abs(v) + PI0_5;
            res *= fast_sqrt(1.0 - abs(v));
            v.x = (v.x >= 0) ? res.x : PI - res.x;
            v.y = (v.y >= 0) ? res.y : PI - res.y;
            return v;
        }
        #define fast_acos(v) _generic_fast_acos(v)
    #endif
    
#endif


