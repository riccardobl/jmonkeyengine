package com.jme3.scene.occlusion;

import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;

public interface OcclusionLogic {
    public Geometry getOcclusionGeometry(RenderManager rm, Geometry g);

    public default void cleanup(RenderManager rm) {
    };

    public default OcclusionLogic chain(OcclusionLogic logic2) {
        OcclusionLogic logic1 = this;
        return new OcclusionLogic() {

            @Override
            public Geometry getOcclusionGeometry(RenderManager rm,Geometry g) {
                g = logic1.getOcclusionGeometry(rm,g);
                return g == null ? null : logic2.getOcclusionGeometry(rm,g);
            }

            @Override
            public void cleanup(RenderManager rm) {
                logic1.cleanup(rm);
                logic2.cleanup(rm);
            }

        };
    }
}