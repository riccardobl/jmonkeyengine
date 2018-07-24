#ifndef PI
    #define PI 3.14159265358979323846264
#endif 

out vec4 outFragColor;
in vec2 texCoord;
in float Life;
in float Age;

#ifdef DIFFUSE_MAP
    uniform sampler2D m_DiffuseMap;
#endif

void main(){
    outFragColor=mix(vec4(1,.5,.5,1),vec4(.6),Age);
    #ifdef DIFFUSE_MAP
        outFragColor=texture(m_DiffuseMap,texCoord);
    #endif

    float alpha=abs(sin(PI*(Age/Life)));

    outFragColor.a=alpha;
}