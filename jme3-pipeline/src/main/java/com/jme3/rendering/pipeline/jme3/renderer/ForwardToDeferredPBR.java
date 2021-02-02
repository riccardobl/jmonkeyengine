package com.jme3.rendering.pipeline.jme3.renderer;

import java.util.List;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import com.jme3.asset.AssetManager;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.shader.VarType;
import com.jme3.util.ListMap;

public class ForwardToDeferredPBR {
    private static  WeakHashMap<Material,Material> migrationCache=new WeakHashMap<Material,Material> ();
    public static Material migrate(AssetManager am, Material mat) {
        Material dpbr=migrationCache.get(mat);
        if(dpbr!=null)return dpbr;
        
        dpbr = new Material(am, "Pipeline/Materials/BaseMat.j3md");
        migrationCache.put(mat,dpbr);

        ListMap<String, MatParam> params = mat.getParamsMap();
        for (Entry<String, MatParam> p : params.entrySet()) {
            String key=p.getKey();
            Object value=p.getValue().getValue();
            VarType type=p.getValue().getVarType();

            MatParam dparam=dpbr.getMaterialDef().getMaterialParam(key);
            if(dparam!=null&&dparam.getVarType()==type){
                dpbr.setParam(key,value);
            }
        }
        dpbr.getAdditionalRenderState().copyMergedTo( mat.getAdditionalRenderState(), dpbr.getAdditionalRenderState());
        return dpbr;
    }

    public static void migrate(AssetManager am, Spatial sp) {
        sp.depthFirstTraversal(sx->{
            if(sx instanceof Geometry){
                Material migratedMat=migrate(am,((Geometry)sx).getMaterial());
                sx.setMaterial(migratedMat);
            }
        });
    }

}
