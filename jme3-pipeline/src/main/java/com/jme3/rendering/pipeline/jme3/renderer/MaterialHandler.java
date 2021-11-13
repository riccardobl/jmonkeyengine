package com.jme3.rendering.pipeline.jme3.renderer;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.CloneableSmartAsset;
import com.jme3.export.*;
import com.jme3.light.LightList;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamOverride;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.material.TechniqueDef.LightMode;
import com.jme3.math.*;
import com.jme3.opencl.Buffer;
import com.jme3.renderer.Caps;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.TextureUnitException;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.shader.*;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureArray;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.ListMap;
import com.jme3.util.SafeArrayList;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaterialHandler {

    public static MatParamOverride createMpo(String key,Object value){
        VarType type=null;
        for(VarType t:VarType.values()){
            if(t.isOfType(value)){
                type=t;
                break;
            }
        }
        return new MatParamOverride(type,key,value);
    }

    public static void clearUniformsSetByCurrent(Shader shader) {
        ListMap<String, Uniform> uniforms = shader.getUniformMap();
        int size = uniforms.size();
        for (int i = 0; i < size; i++) {
            Uniform u = uniforms.getValue(i);
            u.clearSetByCurrentMaterial();
        }
    }

    public static void resetUniformsNotSetByCurrent(Shader shader) {
        ListMap<String, Uniform> uniforms = shader.getUniformMap();
        int size = uniforms.size();
        for (int i = 0; i < size; i++) {
            Uniform u = uniforms.getValue(i);
            if (!u.isSetByCurrentMaterial()) {

                u.clearValue();

            }
        }
    }

    public static void updateShaderMaterialParameter(Renderer renderer, VarType type, Shader shader, MatParam param, BindUnits unit, boolean override) {
        if (type == VarType.UniformBufferObject || type == VarType.ShaderStorageBufferObject) {

            ShaderBufferBlock bufferBlock = shader.getBufferBlock(param.getPrefixedName());
            BufferObject bufferObject = (BufferObject) param.getValue();

            ShaderBufferBlock.BufferType btype;
            if (type == VarType.ShaderStorageBufferObject) {
                btype = ShaderBufferBlock.BufferType.ShaderStorageBufferObject;
                bufferBlock.setBufferObject(btype, bufferObject);
                renderer.setShaderStorageBufferObject(unit.bufferUnit++, bufferObject);
            } else {
                btype = ShaderBufferBlock.BufferType.UniformBufferObject;
                bufferBlock.setBufferObject(btype, bufferObject);
                renderer.setUniformBufferObject(unit.bufferUnit++, bufferObject);
            }

        } else {
            Uniform uniform = shader.getUniform(param.getPrefixedName());
            if (!override && uniform.isSetByCurrentMaterial()) return;

            if (type.isTextureType()) {
                try{
                    renderer.setTexture(unit.textureUnit, (Texture) param.getValue());
                } catch (TextureUnitException exception) {
                    int numTexParams = unit.textureUnit + 1;
                    String message = "Too many texture parameters ("
                            + numTexParams + ") assigned";
                    throw new IllegalStateException(message);
                }
                uniform.setValue(VarType.Int, unit.textureUnit);

                unit.textureUnit++;
            } else {
                uniform.setValue(type, param.getValue());
            }
        }
    }

    public static void applyOverrides(MaterialDef def, Renderer renderer, Shader shader, SafeArrayList<? extends MatParam> overrides, BindUnits bindUnits) {
        for (MatParam override : overrides.getArray()) {
            VarType type = override.getVarType();

            MatParam paramDef = def.getMaterialParam(override.getName());

            boolean enabled=true;
            if(override instanceof MatParamOverride)enabled=((MatParamOverride)override).isEnabled();

            if (paramDef == null || paramDef.getVarType() != type || !enabled) {
                continue;
            }

            Uniform uniform = shader.getUniform(override.getPrefixedName());

            if (override.getValue() != null) {
                updateShaderMaterialParameter(renderer, type, shader, override, bindUnits, true);
            } else {
                uniform.clearValue();
            }
        }
    }


    public static BindUnits updateShaderMaterialParameters(
        Material mat, Renderer renderer, 
        Shader shader, 
        SafeArrayList<MatParam> defaultParams,
        SafeArrayList<MatParamOverride> geometryOverrides,
        SafeArrayList<MatParamOverride> worldOverides,
        BindUnits bindUnits
    ) {

        MaterialDef def = mat.getMaterialDef();
        ListMap<String, MatParam> paramValues = mat.getParamsMap();
        bindUnits.textureUnit = 0;
        bindUnits.bufferUnit = 0;

        // Default params
        applyOverrides( def,  renderer,  shader, defaultParams, bindUnits);

        // Mat params
        for (int i = 0; i < paramValues.size(); i++) {
            MatParam param = paramValues.getValue(i);
            VarType type = param.getVarType();
            System.out.println("Set "+param);
            updateShaderMaterialParameter(renderer, type, shader, param, bindUnits, true);
        }

        // Geometry overrides 
        applyOverrides( def,  renderer,  shader, geometryOverrides, bindUnits);

        // Global overrides 
        applyOverrides( def,  renderer,  shader, worldOverides, bindUnits);


        return bindUnits;
    }


}