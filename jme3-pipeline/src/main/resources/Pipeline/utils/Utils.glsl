#ifndef _UTILS_
#define _UTILS_

#ifndef PI
    #define PI 3.1415926536f
#endif

/**
* Exponential to linear depth
*/
    float Utils_linearizeDepth(in vec2 frustumNearFar,in float depth){
        float f=frustumNearFar.y;
        float n = frustumNearFar.x;
        float d=depth*2.-1.;
        return (2. * n * f) / (f + n - d * (f - n));
    }

    float Utils_linearize01Depth(in vec2 frustumNearFar,in float depth){
            float d=Utils_linearizeDepth(frustumNearFar,depth);
            float f=frustumNearFar.y;
            float n = frustumNearFar.x;
            return (d-n)/(f-n);
}


/**
* Convert screen space (UV,DEPTH) to world space
*/
    vec3 Utils_screenPosToWPos(in mat4 viewProjectionMatrixInverse,in vec3 screenPos){
        vec4 pos=vec4(screenPos,1.0)*2.0-1.0;
        pos = viewProjectionMatrixInverse * pos;
        return pos.xyz/pos.w;
    }


/**
* Convert world space to screenspace (UV,DEPTH)
*/
    vec3 Utils_wposToScreenPos(in mat4 viewProjectionMatrix,in vec3 wPos){
        vec4 ww = viewProjectionMatrix * vec4(wPos, 1.0);
        ww.xyz /= ww.w;
        ww.xyz = ww.xyz * 0.5 + 0.5;
        return ww.xyz;
}

/**
* Get screen space coordinates
*        x=(0,1) for left and right 
*        y=(0,1) for bottom and top
*        z=(0,1) for near and far
*/
vec3 Utils_getScreenPos(in vec2 texCoord,in float depth){
    vec3 screenpos= vec3(texCoord,depth);
    return screenpos;
}


 float Utils_distancePointPlane(in  vec3 v,in vec3 plane_pos,in vec3 plane_normal){
    return dot(plane_normal,v-plane_pos);
 }

 vec3 Utils_projectPointToPlane(in  vec3 v,in vec3 plane_pos,in vec3 plane_normal){
    return v+plane_normal*-Utils_distancePointPlane(v,plane_pos,plane_normal);
 }

float Utils_atan2(in float y, in float x){
    bool s = (abs(x) > abs(y));
    return mix(PI/2.0 - atan(x,y), atan(y,x), s);
}
/*
Encode vec3 normals in any space into vec2 spherical coordinates
https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/atan.xhtml
*/
vec2 Utils_encodeNormals2C(in vec3 n){
    return 
         vec2(
             Utils_atan2(n.y,n.x),
              n.z 
        );
 }



/*
Decode vec3 normals in any space into vec2 spherical coordinates
https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/atan.xhtml
*/
 vec3 Utils_decodeNormals2C(in vec2 enc){
    vec2 ang = enc;
    float v=ang.x ;

    float scthX=sin(v);
    float scthY=cos(v);
        
    vec2 scphi = vec2(sqrt(1.0 - ang.y*ang.y), ang.y);
    return normalize(vec3(scthY*scphi.x, scthX*scphi.x, scphi.y));
 }
#endif