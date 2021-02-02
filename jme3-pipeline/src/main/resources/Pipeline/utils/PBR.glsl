
#ifndef Surface
    struct PBRSurface {
        vec3 normal; // normals w/ normalmap
        vec3 position;

        float metalness;              // metallic value at the surface
        // vec3 reflectance0;            // full reflectance color (normal incidence angle)
        // vec3 reflectance90;           // reflectance color at grazing angle
        float alphaRoughness;         // roughness mapped to a more linear change in the roughness (proposed by [2])
        float roughness;
        // vec3 diffuseColor;            // color contribution from diffuse lighting
        // vec3 specularColor;           // color contribution from specular lighting
        vec3 albedo;
        vec3 f0;
        float NdotV;
        float lightMask; // inverse of shadow map (1= light 0 =shadow)
        vec3 emission;
        vec3 viewDir;
        vec3 reflectedVec;
        float ao;
    };
    #define Surface PBRSurface
#endif

#ifndef PI
    #define PI 3.14159265358979323846264
#endif


vec3 fresnelSchlick(in Surface surface,in Light light){
    float cosTheta=light.HdotV;
    vec3 F0=surface.f0;
    return F0 + (1.0 - F0) * pow(max(1.0 - cosTheta,0.), 5.0);
}

// UE4 way to optimise shlick GGX Gometry shadowing term
float ue4GGX(in Surface surface,in Light light){       
    //G Shchlick GGX Gometry shadowing term,  k = alpha/2
    float k = surface.alphaRoughness * 0.5;

    /*   
    //classic Schlick ggx
    float G_V = ndotv / (ndotv * (1.0 - k) + k);
    float G_L = ndotl / (ndotl * (1.0 - k) + k);
    float G = ( G_V * G_L );
    
    float specular =(D* fresnel * G) /(4 * ndotv);
   */
 
    //http://graphicrants.blogspot.co.uk/2013/08/specular-brdf-reference.html
    float G_V = surface.NdotV + sqrt( (surface.NdotV - surface.NdotV * k) * surface.NdotV + k );
    float G_L = light.NdotL + sqrt( (light.NdotL - light.NdotL * k) * light.NdotL + k );    
    // the max here is to avoid division by 0 that may cause some small glitches.
    return  1.0/max( G_V * G_L ,0.01); 
}

float GeometrySchlickGGX(float NdotV, float roughness){
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;
    float nom   = NdotV;
    float denom = NdotV * (1.0 - k) + k;
    return nom / denom;
}

float GeometrySmith(in Surface surface,in Light light){
    #ifdef UE4_GGX
        return ue4GGX(surface,light);
    #else
        float ggx2 = GeometrySchlickGGX(surface.NdotV, surface.roughness);
        float ggx1 = GeometrySchlickGGX(light.NdotL,  surface.roughness);
        return ggx1 * ggx2;
    #endif
}

float DistributionGGX(in Surface surface,in Light light){
    float alpha = surface.alphaRoughness;
    //D, GGX normaal Distribution function     
    float alpha2 = alpha * alpha;    
    float nom   = alpha2;
    float denom  = ((light.NdotH * light.NdotH) * (alpha2 - 1.0)) + 1.0;
    denom = PI * denom * denom;  
    denom=max(denom,1e-8);
    return nom / denom;
}

vec3 PBR_computeDirectLight(
    inout Surface surface,
    in Light light
    ){

    
    vec3 pos=surface.position;
    vec3 normal=surface.normal;
    vec3 viewDir=surface.viewDir;
    
    vec3 radiance     = light.color * light.attenuation;

    // cook-torrance brdf
    float NDF = DistributionGGX(surface,light);        
    float G   = GeometrySmith(surface,light);      
    vec3 F    = fresnelSchlick(surface,light);       
  
    vec3 nominator    = NDF * G * F;
    float denominator = 4 * surface.NdotV * light.NdotL + 0.001; // 0.001 to prevent divide by zero.
    
    vec3 specular = nominator / denominator;
    specular=max(specular,0.);
    // kS is equal to Fresnel
    vec3 kS = F;
    // for energy conservation, the diffuse and specular light can't
    // be above 1.0 (unless the surface emits light); to preserve this
    // relationship the diffuse component (kD) should equal 1.0 - kS.
    vec3 kD = vec3(1.0) - kS;
    // multiply kD by the inverse metalness such that only non-metals 
    // have diffuse lighting, or a linear blend if partly metal (pure metals
    // have no diffuse light).
    kD *= 1.0 - surface.metalness;	
    // kD=0;

            
    vec3 dlight= (kD * surface.albedo / PI + specular) * radiance * light.NdotL; 
    // dlight= vec3( (surface.albedo / PI   +specular) * radiance * light.NdotL);
    // dlight=max(dlight,0);

    #if defined( SHADOW_MAP) && !defined(NO_SHADOWS)
        float lmask=surface.lightMask;
        #ifndef POINT_LIGHT_SHADOWS
            lmask=mix(lmask,.9,light.type==1.0);
        #endif
        #ifndef SPOT_LIGHT_SHADOWS
            lmask=mix(lmask,.9,light.type==2.0);
        #endif        
                
        dlight*=lmask;
    #endif

    return dlight;
}

vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness){
    return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(1.0 - cosTheta, 5.0);
}   

// from Sebastien Lagarde https://seblagarde.files.wordpress.com/2015/07/course_notes_moving_frostbite_to_pbr_v32.pdf page 69
vec3 getSpecularDominantDir(const in vec3 N, const in vec3 R, const in float realRoughness){
    vec3 dominant;

    float smoothness = 1.0 - realRoughness;
    float lerpFactor = smoothness * (sqrt(smoothness) + realRoughness);
    // The result is not normalized as we fetch in a cubemap
    dominant = mix(N, R, lerpFactor);

    return dominant;
}



#if NB_PROBES  > 0

    vec3 PBR_computeIBLContribution(in samplerCube prefEnv,
        in samplerCube irrMap,
        in mat4 lightProbeData,
        Surface surface
    ) {
    
            vec4 probePos = lightProbeData[3];
            float invRadius = fract( probePos.w);

            vec3 rv = surface.reflectedVec;
            vec3 direction = surface.position - probePos.xyz;
            rv = invRadius * direction + rv;


            // ambient lighting (we now use IBL as the ambient term)
            vec3 F = fresnelSchlickRoughness(surface.NdotV, surface.f0,surface.roughness);
            
            vec3 kS = F;
            vec3 kD = 1.0 - kS;
            kD *= 1.0 - surface.metalness;	  

            float mipCount = probePos.w - invRadius;
            float lod = (surface.roughness * (mipCount));
            // vec3 dominantR ;//= getSpecularDominantDir( surface.normal, rv.xyz, surface.alphaRoughness);
            // dominantR=rv.xyz;
            vec3 dominantR =getSpecularDominantDir( surface.normal, rv.xyz, surface.alphaRoughness);

            #ifdef BRDF
                vec2 brdfcoord=vec2(surface.NdotV, 1.0 - surface.roughness);
                vec3 brdf = texture(g_BrdfLUT,brdfcoord).xyz;
            #else
                vec3 brdf=vec3(1,1,1);
            #endif
            
            vec3 irradiance = textureCube(irrMap, surface.normal).rgb;
            vec3 prefilteredColor = textureCubeLod(prefEnv, dominantR, lod).rgb;
       

            vec3 diffuse = irradiance * surface.albedo;  
            vec3 specular = prefilteredColor  * (F * brdf.x + brdf.y);
   
            vec3 ambient= (kD * diffuse + specular) * surface.ao ;
                    
            return ambient;
        }
    #endif
