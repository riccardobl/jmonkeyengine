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
package com.jme3.scene.control;

import com.jme3.export.binary.BinaryExporter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;
import com.jme3.system.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpatialLodControlTest {

    @Test
    public void testApplyLodLevelHidesAndRestoresLevelZero() {
        Node host = createLodHost();
        Geometry high = (Geometry) host.getChild("high");
        SpatialLodControl control = host.getControl(SpatialLodControl.class);

        control.applyLodLevel(3);

        Assertions.assertEquals(1, control.getActiveLodLevel());
        Assertions.assertEquals(CullHint.Always, high.getLocalCullHint());
        Assertions.assertEquals(2, host.getQuantity());
        Assertions.assertSame(control.getLodLevelSpatial(1), host.getChild(1));

        control.applyLodLevel(0);

        Assertions.assertEquals(0, control.getActiveLodLevel());
        Assertions.assertEquals(CullHint.Inherit, high.getLocalCullHint());
        Assertions.assertEquals(1, host.getQuantity());
    }

    @Test
    public void testAutoSelectWithoutScreenCoverageKeepsSelectedLevel() {
        Node host = createLodHost();
        SpatialLodControl control = host.getControl(SpatialLodControl.class);
        control.setAutoSelect(true);
        host.updateGeometricState();

        control.render(null, new ViewPort("test", new Camera(640, 480)));

        Assertions.assertEquals(0, control.getActiveLodLevel());
        Assertions.assertEquals(1, host.getQuantity());
    }

    @Test
    public void testSaveAndLoadKeepsLodLevelsSerializable() {
        Node host = createLodHost();
        SpatialLodControl control = host.getControl(SpatialLodControl.class);

        control.applyLodLevel(1);

        Node loaded = BinaryExporter.saveAndLoad(TestUtil.createAssetManager(), host);
        Assertions.assertEquals(1, control.getActiveLodLevel());
        Assertions.assertEquals(2, host.getQuantity());

        SpatialLodControl loadedControl = loaded.getControl(SpatialLodControl.class);

        Assertions.assertNotNull(loadedControl);
        Assertions.assertEquals(2, loadedControl.getNumLodLevels());
        Assertions.assertEquals(0, loadedControl.getActiveLodLevel());
        Assertions.assertEquals(1, loaded.getQuantity());

        loadedControl.applyLodLevel(1);

        Assertions.assertEquals(1, loadedControl.getActiveLodLevel());
        Assertions.assertEquals(2, loaded.getQuantity());
        Assertions.assertSame(loadedControl.getLodLevelSpatial(1), loaded.getChild(1));
    }

    private Node createLodHost() {
        Node host = new Node("host");
        host.attachChild(new Geometry("high", new Box(1f, 1f, 1f)));

        Node low = new Node("low");
        low.attachChild(new Geometry("low-proxy", new Box(0.5f, 0.5f, 0.5f)));

        SpatialLodControl control = new SpatialLodControl();
        control.setLodLevelSpatial(0, host.clone(false));
        control.setLodLevelSpatial(1, low);
        host.addControl(control);
        return host;
    }
}
