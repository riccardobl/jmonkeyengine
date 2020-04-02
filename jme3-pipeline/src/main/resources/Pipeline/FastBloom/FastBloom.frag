
#extension GL_ARB_explicit_attrib_location : enable



noperspective in vec2 TexCoord;

#for i=0..6 ( #ifdef SCENE_$i $0 #endif )
    uniform sampler2D m_Scene$i;
    #ifdef MRT 
        layout(location=$i) 
    #endif 
        out vec4 outScene$i;
#endfor 

uniform vec2 m_Resolution;
uniform vec2 m_BlurDirection;

#if defined(EXTRACT)
    uniform float m_BrightPoint;
#endif

// Sample (and extract) glow
vec4 sample(in sampler2D inTexture,in vec2 coord){
    vec4 color=texture(inTexture,coord);
    #ifdef EXTRACT
        float v=step(m_BrightPoint,max(max(color.r, color.g), color.b));
        color*=v;
    #endif
    color=max(vec4(0),color);
    return color;
}

// Blur in one direction
// Adaptation from http://rastergrid.com/blog/2010/09/efficient-gaussian-blur-with-linear-sampling/
const float offset[3] = float[](0.0, 1.3846153846, 3.2307692308);
const float weight[3] = float[](0.2270270270, 0.3162162162, 0.0702702703);
vec4 blur(in sampler2D inTexture,in vec2 texCoord,in vec2 blurDirection){   
    vec2 coord;
    vec2 bdir=blurDirection;

    coord=( vec2(gl_FragCoord) )/m_Resolution;

    vec4 sum = sample(inTexture, texCoord) * weight[0];

    for(int i=1;i<3;i++){        
        coord=( vec2(gl_FragCoord)+(vec2(offset[i])*bdir) )/m_Resolution;
        sum += sample(inTexture, coord)*weight[i];
        coord=( vec2(gl_FragCoord)-(vec2(offset[i])*bdir) )/m_Resolution;
        sum += sample(inTexture, coord)*weight[i];
    }
    return max(vec4(0),sum);
}


void main(){   
    #for i=0..6 ( #ifdef SCENE_$i $0 #endif )
        #ifndef BLUR
            outScene$i = sample(m_Scene$i,TexCoord);
        #else
            outScene$i = blur(m_Scene$i,TexCoord,m_BlurDirection);
        #endif
    #endfor 
}