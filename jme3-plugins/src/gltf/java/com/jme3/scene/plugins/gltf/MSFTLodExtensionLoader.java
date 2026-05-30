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
package com.jme3.scene.plugins.gltf;

import com.jme3.asset.AssetLoadException;
import com.jme3.plugins.json.JsonArray;
import com.jme3.plugins.json.JsonElement;
import com.jme3.plugins.json.JsonObject;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.SpatialLodControl;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the MSFT_lod glTF extension for node-level LODs.
 */
public class MSFTLodExtensionLoader implements ExtensionLoader {

    public static final String EXTENSION_NAME = "MSFT_lod";
    private static final Logger logger = Logger.getLogger(MSFTLodExtensionLoader.class.getName());

    @Override
    public Object handleExtension(GltfLoader loader, String parentName, JsonElement parent,
            JsonElement extension, Object input) throws IOException {
        if ("node".equals(parentName)) {
            if (!(input instanceof Spatial)) {
                throw new AssetLoadException(EXTENSION_NAME + " node extension applied to a non-spatial input.");
            }
            return handleNodeLod(loader, parent, extension, (Spatial) input);
        } else if ("material".equals(parentName)) {
            if (input instanceof GltfMaterialData) {
                ((GltfMaterialData) input).addGltfExtension(EXTENSION_NAME);
            }
        } else {
            logger.log(Level.WARNING, "{0} extension added on unsupported element {1}.",
                    new Object[]{EXTENSION_NAME, parentName});
        }
        return input;
    }

    private Spatial handleNodeLod(GltfLoader loader, JsonElement parent, JsonElement extension, Spatial highLod)
            throws IOException {
        JsonObject extensionObject = extension.getAsJsonObject();
        JsonArray ids = extensionObject.getAsJsonArray("ids");
        if (ids == null || ids.size() == 0) {
            return highLod;
        }

        if (!(highLod instanceof Node)) {
            throw new AssetLoadException(EXTENSION_NAME + " node LODs require a Node spatial.");
        }

        SpatialLodControl control = new SpatialLodControl();
        Spatial highLodClone = highLod.clone(false);
        control.setLodLevelSpatial(0, highLodClone);
        setScreenCoverage(parent, highLodClone);

        for (int i = 0; i < ids.size(); i++) {
            int nodeIndex = ids.get(i).getAsInt();
            Spatial lodSpatial = loader.readNodeWithChildren(nodeIndex);
            if (lodSpatial == null) {
                throw new AssetLoadException(EXTENSION_NAME + " referenced non-spatial node " + nodeIndex);
            }
            control.setLodLevelSpatial(i + 1, lodSpatial);
            setScreenCoverage(loader.getNode(nodeIndex), lodSpatial);
        }

        highLod.addControl(control);
        return highLod;
    }

    private void setScreenCoverage(JsonElement nodeElement, Spatial spatial) {
        if (nodeElement == null) {
            return;
        }
        JsonObject nodeObject = nodeElement.getAsJsonObject();
        JsonObject extras = nodeObject.getAsJsonObject("extras");
        if (extras != null && extras.has(SpatialLodControl.SCREEN_COVERAGE_USER_DATA)) {
            spatial.setUserData(SpatialLodControl.SCREEN_COVERAGE_USER_DATA,
                    extras.get(SpatialLodControl.SCREEN_COVERAGE_USER_DATA).getAsFloat());
        }
    }
}
