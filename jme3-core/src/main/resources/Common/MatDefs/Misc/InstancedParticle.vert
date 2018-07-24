#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Skinning.glsllib"
#import "Common/ShaderLib/MorphAnim.glsllib"

#ifndef PI
    #define PI 3.14159265358979323846264
#endif 

in vec3 inPosition;
uniform mat4 g_ViewProjectionMatrix;
uniform mat4 g_WorldMatrix;
uniform mat4 g_WorldViewProjectionMatrix;

uniform float g_Time;
uniform float m_SpawnTime;
in vec2 inTexCoord;
out vec2 texCoord;

out float Age;
out float Life;

mat3 aToM3(in vec3 ang) {
  float cosX = cos(ang.x);
  float sinX = sin(ang.x);
  float cosY = cos(ang.y);
  float sinY = sin(ang.y);
  float cosZ = cos(ang.z);
  float sinZ = sin(ang.z);

  mat3 m;

  float m00 = cosY * cosZ + sinX * sinY * sinZ; 
  float m01 = cosY * sinZ - sinX * sinY * cosZ; 
  float m02 = cosX * sinY;
  
  float m04 = -cosX * sinZ; 
  float m05 = cosX * cosZ; 
  float m06 = sinX;
  
  float m08 = sinX * cosY * sinZ - sinY * cosZ;
  float m09 = -sinY * sinZ - sinX * cosY * cosZ;
  float m10 = cosX * cosY;
  
  m[0][0] = m00; 
  m[0][1] = m01; 
  m[0][2] = m02;
  
  m[1][0] = m04; 
  m[1][1] = m05; 
  m[1][2] = m06;

  m[2][0] = m08; 
  m[2][1] = m09; 
  m[2][2] = m10;

  return m;
}

const vec3 RAND_SEED= vec3(99.,12.,100000.0);
float rand(float x){
    return  fract(sin(RAND_SEED.x+x+gl_InstanceID*RAND_SEED.y)*RAND_SEED.z);
}

void main(){
    texCoord=inTexCoord;
    vec4 modelSpacePos = vec4(inPosition, 1.0);
    
    #ifdef NUM_MORPH_TARGETS
        Morph_Compute(modelSpacePos);
    #endif

    #ifdef NUM_BONES
        Skinning_Compute(modelSpacePos);
    #endif
    // Rotation
  

    float time=g_Time;
    // Life=abs(sin(time));
    // Min life
    // int m_MinSpawnDelay=1;
    // int m_MaxSpawnDelay=3;
    // int spawn_delay=int(rand(0)*(m_MaxSpawnDelay-m_MinSpawnDelay)+m_MinSpawnDelay);

    // float m_MinLifeSpan=
    // float max_life= rand(gl_InstanceID)*;

    // Life*=step(PI*spawn_delay,time);
    // Max life
    // Life*=1.-step(PI*(spawn_delay+1),time);
    float rnd=rand(0);

    float m_MaxLife=10.;
    float m_MinLife=1.;
    float m_MaxEmissionDelay=6.;
    float m_MinEmissionDelay=0.3;
    m_MaxEmissionDelay=1.;
    m_MinEmissionDelay=0.;

    // Emission delay
    float emission_delay=rnd*(m_MaxEmissionDelay-m_MinEmissionDelay)+m_MinEmissionDelay;
    rnd=rand(230);
    ///

    // Max life & current age 
    float life=rnd*(m_MaxLife-m_MinLife)+m_MinLife;
    float age=g_Time-emission_delay-m_SpawnTime;    
    float alive_status=age/life; // x<0 = not spawned ,  0<=x<1 spawned and alive, x>=1 spawned and dead
    alive_status=step(0.,alive_status)*(1.-step(1.,alive_status));   // ,  0<=x<1 ? 1 : 0
    Age=mod(age,life)*alive_status;
    Life=life;
    ///

    // Random rotation
    modelSpacePos.xyz=aToM3(vec3(gl_InstanceID*rnd+time,gl_InstanceID*rnd+time,gl_InstanceID*rnd+time))*modelSpacePos.xyz;

    // Position  
    // modelSpacePos.y+=gl_InstanceID;
      vec3 instance_pos=vec3(0);
    instance_pos.x=rnd*2.-1.;
    rnd=rand(999.);
    instance_pos.y=rnd*2.-1.;
        rnd=rand(929.);
    instance_pos.z=rnd*2.-1.;

    // Sphere emitter
   instance_pos=normalize(instance_pos);
   instance_pos*=.1;
    vec3 target_pos=normalize(instance_pos)*.5;//vec3(rand(119.)*1.5,rand(21.)*1.5,rand(79.)*1.5);

    instance_pos=mix(instance_pos,target_pos,Age);
        instance_pos.y*=1.-step(0.,instance_pos.y);


//wtf
    // vec3 target_pos=vec3(rand(119.)*6.5,rand(21.)*6.5,rand(79.)*6.5);
    // target_pos.z=target_pos.x*target_pos.x - target_pos.y*target_pos.y;
    // instance_pos=mix(instance_pos,target_pos,Age);


        // vec3 target_pos=vec3(rand(99.)*.3,0,rand(79.)*.3);
        // vec3 target_pos=vec3(rand(99.)*.05,0,rand(79.)*.05);

        // target_pos.z=pow(target_pos.x,2)+pow(target_pos.y,2);//*target_pos.x - target_pos.y*target_pos.y;
        // target_pos.z=target_pos.x*target_pos.x - target_pos.y*target_pos.y;

    // vec3 target_pos=instance_pos+vec3(sin(gl_InstanceID),10,cos(gl_InstanceID));


    modelSpacePos.xyz+=instance_pos;
 


//    Life*=step(PI*gl_InstanceID,time);
//    Life*=1.-step(PI*(gl_InstanceID+1),g_Time);
//    Life*=.5;
//    Life+=.5;



    // modelSpacePos+=g_WorldMatrix[3];
    modelSpacePos=g_WorldMatrix*modelSpacePos;
    // modelSpacePos.x+=sin(gl_InstanceID+g_Time);
    //     modelSpacePos.z+=sin(gl_InstanceID+g_Time)*2.;
    gl_Position = g_ViewProjectionMatrix * modelSpacePos;
}