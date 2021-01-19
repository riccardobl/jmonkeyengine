#ifdef MRT 
    layout(location=0) out vec4 outColor;
#else
    out vec4 outColor;
#endif 

in vec2 TexCoord;
in vec4 VertColor;
in vec3 WorldPos;

void main(){
    outColor=vec4(1);
}