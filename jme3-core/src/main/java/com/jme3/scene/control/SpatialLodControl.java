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

import com.jme3.bounding.BoundingVolume;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.util.clone.Cloner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Switches between spatial LOD subtrees while keeping LOD 0 as the public
 * scene graph attached to the controlled node.
 */
public class SpatialLodControl extends AbstractControl {

    public static final String SCREEN_COVERAGE_USER_DATA = "MSFT_screencoverage";

    private List<Spatial> lodLevels = new ArrayList<>();
    private int selectedLodLevel = 0;
    private int activeLodLevel = 0;
    private boolean autoSelect = true;

    private transient Spatial attachedLod;
    private transient List<Spatial> levelZeroChildren = new ArrayList<>();
    private transient Map<Spatial, CullHint> hiddenCullHints = new IdentityHashMap<>();

    public SpatialLodControl() {
    }

    public void setLodLevelSpatial(int level, Spatial spatial) {
        if (level < 0) {
            throw new IllegalArgumentException("LOD level cannot be < 0");
        }
        while (lodLevels.size() <= level) {
            lodLevels.add(null);
        }
        lodLevels.set(level, spatial);
    }

    public Spatial getLodLevelSpatial(int level) {
        if (level < 0 || level >= lodLevels.size()) {
            return null;
        }
        return lodLevels.get(level);
    }

    public int getNumLodLevels() {
        return lodLevels.size();
    }

    public void setSelectedLodLevel(int lodLevel) {
        selectedLodLevel = Math.max(0, lodLevel);
        autoSelect = false;
    }

    public int getSelectedLodLevel() {
        return selectedLodLevel;
    }

    public int getActiveLodLevel() {
        return activeLodLevel;
    }

    public boolean isAutoSelect() {
        return autoSelect;
    }

    public void setAutoSelect(boolean autoSelect) {
        this.autoSelect = autoSelect;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null && !(spatial instanceof Node)) {
            throw new IllegalArgumentException("SpatialLodControl can only be attached to Node.");
        }
        if (this.spatial != null && spatial == null) {
            restoreLevelZeroCullHints();
            detachAttachedLod();
        }
        super.setSpatial(spatial);
        refreshLevelZeroChildren();
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        if (spatial == null) {
            return;
        }
        int level = autoSelect ? selectLodLevel(vp.getCamera()) : selectedLodLevel;
        applyLodLevel(level);
    }

    public void applyLodLevel(int lodLevel) {
        if (spatial == null) {
            selectedLodLevel = Math.max(0, lodLevel);
            return;
        }

        int clampedLevel = clampLodLevel(lodLevel);
        if (clampedLevel == activeLodLevel && isAttachedLodCurrent(clampedLevel)) {
            return;
        }

        detachAttachedLod();
        if (clampedLevel == 0) {
            restoreLevelZeroCullHints();
        } else {
            hideLevelZero();
            Spatial lod = lodLevels.get(clampedLevel);
            ((Node) spatial).attachChild(lod);
            attachedLod = lod;
        }
        activeLodLevel = clampedLevel;
    }

    /**
     * Temporarily restores the controlled node to its public LOD 0 shape before
     * serialization.
     *
     * @return the active LOD level to pass to {@link #restoreAfterSerialization(int)}
     */
    public int prepareForSerialization() {
        int previousActiveLodLevel = activeLodLevel;
        restoreLevelZeroCullHints();
        detachAttachedLod();
        activeLodLevel = 0;
        return previousActiveLodLevel;
    }

    /**
     * Restores the runtime LOD state after {@link #prepareForSerialization()}.
     *
     * @param previousActiveLodLevel the value returned by {@code prepareForSerialization()}
     */
    public void restoreAfterSerialization(int previousActiveLodLevel) {
        if (previousActiveLodLevel != 0) {
            applyLodLevel(previousActiveLodLevel);
        }
    }

    private int selectLodLevel(Camera camera) {
        if (camera == null || lodLevels.isEmpty()) {
            return selectedLodLevel;
        }

        float coverage = estimateScreenCoverage(camera);
        boolean foundThreshold = false;
        int lastThresholdLevel = selectedLodLevel;
        for (int i = 0; i < lodLevels.size(); i++) {
            Spatial level = lodLevels.get(i);
            Float threshold = level != null ? level.getUserData(SCREEN_COVERAGE_USER_DATA) : null;
            if (threshold != null && coverage >= threshold) {
                return i;
            }
            if (threshold != null) {
                foundThreshold = true;
                lastThresholdLevel = i;
            }
        }
        return foundThreshold ? lastThresholdLevel : selectedLodLevel;
    }

    private float estimateScreenCoverage(Camera camera) {
        BoundingVolume bound = spatial.getWorldBound();
        if (bound == null) {
            return 1f;
        }
        float distance = bound.distanceTo(camera.getLocation());
        float area = com.jme3.util.AreaUtils.calcScreenArea(bound, distance, camera.getWidth());
        return area / Math.max(1f, camera.getWidth() * camera.getHeight());
    }

    private int clampLodLevel(int lodLevel) {
        if (lodLevels.isEmpty()) {
            return 0;
        }
        int requested = Math.max(0, lodLevel);
        for (int i = requested; i >= 0; i--) {
            if (i < lodLevels.size() && lodLevels.get(i) != null) {
                return i;
            }
        }
        for (int i = requested + 1; i < lodLevels.size(); i++) {
            if (lodLevels.get(i) != null) {
                return i;
            }
        }
        return 0;
    }

    private boolean isAttachedLodCurrent(int level) {
        if (level == 0) {
            return attachedLod == null;
        }
        return level < lodLevels.size() && attachedLod == lodLevels.get(level)
                && attachedLod.getParent() == spatial;
    }

    private void hideLevelZero() {
        refreshLevelZeroChildren();
        for (Spatial child : levelZeroChildren) {
            if (!hiddenCullHints.containsKey(child)) {
                hiddenCullHints.put(child, child.getLocalCullHint());
            }
            child.setCullHint(CullHint.Always);
        }
    }

    private void restoreLevelZeroCullHints() {
        for (Map.Entry<Spatial, CullHint> entry : hiddenCullHints.entrySet()) {
            entry.getKey().setCullHint(entry.getValue());
        }
        hiddenCullHints.clear();
    }

    private void detachAttachedLod() {
        if (attachedLod != null && attachedLod.getParent() == spatial) {
            ((Node) spatial).detachChild(attachedLod);
        }
        attachedLod = null;
    }

    private void refreshLevelZeroChildren() {
        levelZeroChildren = new ArrayList<>();
        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                if (child != attachedLod) {
                    levelZeroChildren.add(child);
                }
            }
        }
    }

    @Override
    public void cloneFields(Cloner cloner, Object original) {
        super.cloneFields(cloner, original);
        SpatialLodControl originalControl = (SpatialLodControl) original;
        lodLevels = new ArrayList<>();
        for (Spatial lodLevel : originalControl.lodLevels) {
            lodLevels.add(cloner.clone(lodLevel));
        }
        attachedLod = null;
        levelZeroChildren = new ArrayList<>();
        hiddenCullHints = new IdentityHashMap<>();
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        int previousActiveLodLevel = prepareForSerialization();

        try {
            super.write(ex);
            OutputCapsule oc = ex.getCapsule(this);
            oc.writeSavableArrayList(new ArrayList<>(lodLevels), "lodLevels", null);
            oc.write(selectedLodLevel, "selectedLodLevel", 0);
            oc.write(0, "activeLodLevel", 0);
            oc.write(autoSelect, "autoSelect", true);
        } finally {
            restoreAfterSerialization(previousActiveLodLevel);
        }
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        lodLevels.clear();
        ArrayList<?> loadedLevels = ic.readSavableArrayList("lodLevels", null);
        if (loadedLevels != null) {
            for (Object loadedLevel : loadedLevels) {
                lodLevels.add((Spatial) loadedLevel);
            }
        }
        selectedLodLevel = ic.readInt("selectedLodLevel", 0);
        activeLodLevel = ic.readInt("activeLodLevel", 0);
        autoSelect = ic.readBoolean("autoSelect", true);
        attachedLod = null;
        levelZeroChildren = new ArrayList<>();
        hiddenCullHints = new IdentityHashMap<>();
    }
}
