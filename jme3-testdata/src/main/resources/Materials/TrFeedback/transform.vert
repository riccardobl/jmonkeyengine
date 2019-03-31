#extension GL_ARB_explicit_attrib_location : enable
#extension GL_ARB_enhanced_layouts : enable

in vec3 inPosition;
layout(xfb_offset = 0,xfb_buffer = 0) out vec3 outPos;

uniform float g_Time;

void main(){
    int particleId=gl_VertexID;
    vec3 pos=inPosition.xyz;   
    pos.y+=sin(particleId+g_Time*1.)*0.4;
    pos.x+=cos(particleId+g_Time*2.)*0.4;
    pos.z+=tan(particleId+g_Time*.2)*0.2;        
    outPos=pos;
}