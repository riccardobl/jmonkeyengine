#extension GL_ARB_explicit_attrib_location : enable
#extension GL_ARB_enhanced_layouts : enable

layout(points) in;
layout(points, max_vertices = 1) out;

in mat4 Data[];
in vec3 Size[];
layout(xfb_offset = 0,xfb_buffer = 0) out mat4 outData;


uniform vec4 m_InstanceControl_Planes[6];
uniform vec3 m_InstanceControl_BoundingBoxExtents;
uniform int m_InstanceControl_LodLevel;
uniform int m_InstanceControl_MaxLodLevel;
uniform float m_InstanceControl_Lod1Distance;
uniform float m_InstanceControl_Lod2Distance;
uniform float m_InstanceControl_Lod3Distance;

uniform vec3 g_CameraPosition;


vec3 getWorldPos(in mat4 inInstanceData){
    return inInstanceData[3].xyz;
}


float planePointDistance(in vec4 plane,in vec3 point){
    return dot(plane.xyz,point)-plane.w;
}

bool isInFrustum(in vec3 point,in vec3 bboxExtents){
    for(int i=0;i<6;i++){
        vec4 plane=m_InstanceControl_Planes[i];
        vec3 planeNormal=plane.xyz;
        float planeConstant=plane.w;

        // float radius=max(bboxExtents.x,max(bboxExtents.y,bboxExtents.z));
        float radius=dot(abs(bboxExtents * planeNormal),vec3(1));

        float dist=planePointDistance(m_InstanceControl_Planes[i],point);

        if (dist < -radius) return false; // Is outside        
    }
    return true;  
}


int selectLodLevel(in float dist){
    int lodLevel=0;
    if(dist<m_InstanceControl_Lod1Distance){
        lodLevel=0;
    }else if(dist<m_InstanceControl_Lod2Distance){
        lodLevel=1;
    }else if(dist<m_InstanceControl_Lod3Distance){
        lodLevel=2;
    }else{
        lodLevel=3;
    }
    lodLevel=min(lodLevel,m_InstanceControl_MaxLodLevel);
    return lodLevel;
}

bool isLodSelected(in int lodLevel,in float dist){
    int wantedLodLevel=selectLodLevel(dist);
    return lodLevel==wantedLodLevel;
}

void main(){   
    mat4 instanceData=Data[0];
    vec3 bboxExtents=Size[0];

    vec3 wPos=getWorldPos(instanceData);
  
    bool culled=!isInFrustum(wPos,bboxExtents);
    
    // Lod check
    int lodLevel=m_InstanceControl_LodLevel;
    float dist=distance(g_CameraPosition,wPos);
    culled=culled||!isLodSelected(lodLevel,dist);

    if(!culled){    
        outData = Data[0];
        EmitVertex();
        EndPrimitive();
    }
}
