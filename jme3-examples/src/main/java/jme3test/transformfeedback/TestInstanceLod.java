/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jme3test.transformfeedback;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LodControl;
import com.jme3.scene.Node;
import com.jme3.scene.instancing.InstancedGeometry;
import com.jme3.scene.instancing.InstancedNode;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;

import jme3tools.optimize.LodGenerator;

public class TestInstanceLod extends SimpleApplication  {

    private Mesh mesh1;
    private Mesh mesh2;
    private final Material[] materials = new Material[6];
    private Node instancedNode;
    private float time = 0;
    private boolean INSTANCING = true;
    

    public static void main(String[] args) {
        AppSettings settings=new AppSettings(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL3);
    
        TestInstanceLod app=new TestInstanceLod();
        app.setSettings(settings);
        
        app.setPauseOnLostFocus(false);
        
        app.start();
    }

    public void simpleInitApp() {
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-1,-1,-1).normalizeLocal());
        rootNode.addLight(dl);
        boolean useInstancing=true;
        boolean useLod=true;

        Node teapotNode = (Node) assetManager.loadModel("Models/Teapot/Teapot.mesh.xml");
        Geometry teapot = (Geometry) teapotNode.getChild(0);
     
//        Sphere sph = new Sphere(16, 16, 4);
//        Geometry teapot = new Geometry("teapot", sph);

        Material mat = new Material(assetManager,"Materials/InstancingControl/UnshadedIC.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        // mat.setFloat("Shininess", 16f);
        // mat.setBoolean("VertexLighting", true);
        teapot.setMaterial(mat);

        if(useLod){
            LodGenerator lod = new LodGenerator(teapot);
            lod.bakeLods(LodGenerator.TriangleReductionMethod.PROPORTIONAL,0.4f, 0.86f, 0.99f);
        }
        if(useInstancing){
            teapot.getMaterial().setBoolean("UseInstancing",true);
        }
        Node potRoot=useInstancing?new InstancedNode("PotRoot"):new Node("PotRoot");
        rootNode.attachChild(potRoot);

       // show normals as material
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        int n=100000;
        int l= (int) FastMath.sqrt(n);
        for (int x = 0; x < l; x++) {
            for (int y = 0; y < l; y++) {

                Geometry clonePot = teapot.clone(false);
                
                //clonePot.setMaterial(mat);
                clonePot.setLocalTranslation(x * .5f, 0, y * .5f);
                clonePot.setLocalScale(.15f);
                
               
                if(!useInstancing){
                    if(useLod){
                        LodControl control = new LodControl();
                        clonePot.addControl(control);
                    }
                }
                potRoot.attachChild(clonePot);
            }
        }

        if(useInstancing){
            InstancedNode potRootI=(InstancedNode)potRoot;
            potRootI.instance();
        }

        cam.setLocation(new Vector3f(8.378951f, 5.4324f, 8.795956f));
        cam.setRotation(new Quaternion(-0.083419204f, 0.90370524f, -0.20599906f, -0.36595422f));
}
}
