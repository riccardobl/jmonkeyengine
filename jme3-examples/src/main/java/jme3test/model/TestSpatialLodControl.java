/*
 * Copyright (c) 2009-2026 jMonkeyEngine
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
package jme3test.model;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.SpatialLodControl;
import com.jme3.scene.shape.Box;

public class TestSpatialLodControl extends SimpleApplication {

    private SpatialLodControl lodControl;

    public static void main(String[] args) {
        TestSpatialLodControl app = new TestSpatialLodControl();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        flyCam.setMoveSpeed(10f);

        DirectionalLight light = new DirectionalLight();
        light.setDirection(new Vector3f(-1f, -2f, -1f).normalizeLocal());
        rootNode.addLight(light);

        Node lodHost = createHighDetailNode();
        Node lowDetail = createLowDetailNode();

        Node levelZero = lodHost.clone(false);
        levelZero.setUserData(SpatialLodControl.SCREEN_COVERAGE_USER_DATA, 0.08f);
        lowDetail.setUserData(SpatialLodControl.SCREEN_COVERAGE_USER_DATA, 0.02f);

        lodControl = new SpatialLodControl();
        lodControl.setLodLevelSpatial(0, levelZero);
        lodControl.setLodLevelSpatial(1, lowDetail);
        lodHost.addControl(lodControl);

        rootNode.attachChild(lodHost);
        cam.setLocation(new Vector3f(0f, 3f, 12f));
        cam.lookAt(lodHost.getWorldTranslation(), Vector3f.UNIT_Y);

        inputManager.addMapping("lod0", new KeyTrigger(KeyInput.KEY_0));
        inputManager.addMapping("lod1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("lodAuto", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addListener(actionListener, "lod0", "lod1", "lodAuto");
    }

    private Node createHighDetailNode() {
        Node node = new Node("high-detail");
        node.attachChild(createBox("body", new Vector3f(0f, 0f, 0f), new Vector3f(1.5f, 0.8f, 0.8f),
                ColorRGBA.Blue));
        node.attachChild(createBox("top", new Vector3f(0f, 0.9f, 0f), new Vector3f(0.8f, 0.25f, 0.8f),
                ColorRGBA.Cyan));
        node.attachChild(createBox("antenna", new Vector3f(0f, 1.45f, 0f), new Vector3f(0.08f, 0.45f, 0.08f),
                ColorRGBA.White));
        return node;
    }

    private Node createLowDetailNode() {
        Node node = new Node("low-detail");
        node.attachChild(createBox("proxy", new Vector3f(0f, 0f, 0f), new Vector3f(1.5f, 1f, 0.8f),
                ColorRGBA.Orange));
        return node;
    }

    private Geometry createBox(String name, Vector3f location, Vector3f extents, ColorRGBA color) {
        Geometry geometry = new Geometry(name, new Box(extents.x, extents.y, extents.z));
        geometry.setLocalTranslation(location);
        geometry.setMaterial(createMaterial(color));
        return geometry;
    }

    private Material createMaterial(ColorRGBA color) {
        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setBoolean("UseMaterialColors", true);
        material.setColor("Diffuse", color);
        material.setColor("Specular", ColorRGBA.White);
        material.setFloat("Shininess", 12f);
        return material;
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed) {
                return;
            }
            if ("lod0".equals(name)) {
                lodControl.setSelectedLodLevel(0);
                lodControl.applyLodLevel(0);
            } else if ("lod1".equals(name)) {
                lodControl.setSelectedLodLevel(1);
                lodControl.applyLodLevel(1);
            } else if ("lodAuto".equals(name)) {
                lodControl.setAutoSelect(true);
            }
        }
    };
}
