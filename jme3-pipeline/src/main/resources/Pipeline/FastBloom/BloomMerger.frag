#extension GL_ARB_explicit_attrib_location : enable

noperspective in vec2 TexCoord;

#for i=0..6 ( #ifdef SCENE_$i $0 #endif )
    uniform sampler2D m_Scene$i;
    
    #for j=0..8 ( #ifdef  BLOOM_LAYER_$i_$j $0 #endif )
        uniform sampler2D m_BloomLayer$i_$j;
    #endfor

    #ifdef MRT 
        layout(location=$i) 
    #endif 
        out vec4 outScene$i;

#endfor 

#for j=0..8 ( $0 )
    uniform float m_Intensity$j;
#endfor


void main(){   
    vec4 layerc;
    vec4 color;

    #for i=0..6 ( #ifdef SCENE_$i $0 #endif )
        color = texture(m_Scene$i,TexCoord);
        #for j=0..8 ( #ifdef  BLOOM_LAYER_$i_$j $0 #endif )
            layerc = texture(m_BloomLayer$i_$j,TexCoord);
            color.rgb += mix(vec3(0),layerc.rgb,m_Intensity$j);
        #endfor
        outScene$i = color;
    #endfor 
}