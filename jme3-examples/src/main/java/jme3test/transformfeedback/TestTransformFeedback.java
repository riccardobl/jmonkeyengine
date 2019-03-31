package jme3test.transformfeedback;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingSphere;
import com.jme3.light.LightList;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Caps;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.VertexBuffer.Format;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.scene.shape.Sphere;
import com.jme3.shader.BufferObject;
import com.jme3.shader.Shader;
import com.jme3.system.AppSettings;
import com.jme3.util.BufferUtils;

import org.lwjgl.opengl.GL30;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * TestTransformFeedBack
 */
public class TestTransformFeedback extends SimpleApplication {

    VertexBuffer buf1, buf2;
    Geometry transformGeom;
    Geometry particle;
    int n = 1000000;


    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(0, 0, -20));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

        // It's not safe to read and write on the same buffer
        // So we are going to swap between two buffers
        buf1 = new VertexBuffer(Type.InstanceData);
        buf2 = new VertexBuffer(Type.InstanceData);

        // They contains positions (xyz)
        buf1.setupData(Usage.Static, 3, Format.Float, BufferUtils.createFloatBuffer(n * 3));
        buf2.setupData(Usage.Static, 3, Format.Float, BufferUtils.createFloatBuffer(n * 3));

        // One position per instance
        buf1.setInstanceSpan(1);
        buf2.setInstanceSpan(1);

        // Force the renderer to allocate them immediately
        renderManager.getRenderer().updateBufferData(buf1);
        renderManager.getRenderer().updateBufferData(buf2);


        // ### PARTICLE SETUP ###
        // Build the particle
        particle = new Geometry("Particle", new Sphere(4, 4, .1f));

        // Never cull
        particle.setCullHint(CullHint.Never);
        particle.setModelBound(new BoundingSphere(Float.POSITIVE_INFINITY, Vector3f.ZERO));

        // Set the number of instances we want (how many particles)
        particle.setNumInstances(n);

        // Rendering material
        particle.setMaterial(new Material(assetManager, "Materials/TrFeedback/render.j3md"));

        rootNode.attachChild(particle);


        // ### TRANSFORM SETUP ###
        // Transform feedback will be used to move the particles

        // Transform mesh is just a point cloud, where one point equals to one particle.
        // We use it as a way to set the "work group" like if this was a compute shader
        Mesh transformMesh = new Mesh();
        transformMesh.setMode(Mode.Points);

        // Our position buffer will be a pointer to either buf1 or buf2
        transformMesh.setBuffer(buf1.makePointer(Type.Position, Usage.Static, 3, Format.Float, 0));

        // We want to count the emitted primitives emitted by transform feedback
        transformMesh.setCountFeedbackPrimitives(true);

        // The Geometry
        transformGeom = new Geometry("TransformMesh", transformMesh);

        // The material that updates the positions
        transformGeom.setMaterial(new Material(assetManager, "Materials/TrFeedback/transform.j3md"));



    }

    boolean swapFlag = false;

    @Override
    public void simpleRender(RenderManager rm) {

        VertexBuffer trInput;
        VertexBuffer trOutput;
        if (!swapFlag) {
            trInput = buf1;
            trOutput = buf2;
        } else {
            trInput = buf2;
            trOutput = buf1;
        }

        VertexBuffer positionBuffer = transformGeom.getMesh().getBuffer(Type.Position);

        // We use trInput as our position buffer for the transform pass
        trInput.makePointer(positionBuffer); // Turn positionBuffer into a pointer to trInput

        // The number of elements in the position buffer might have changed, so ensure it's refreshed
        transformGeom.getMesh().updateCounts();

        // trOutput is our transform output
        transformGeom.getMesh().setFeedbackOutput(0, trOutput);

        // trOutput is also what we want to use to update the instance positions on our render pass
        particle.getMesh().setOrReplaceBuffer(trOutput);

        renderManager.renderGeometry(transformGeom);

        int primitives = transformGeom.getMesh().getCountFeedbackPrimitivesQuery().getInt();
        System.out.println(primitives + " feedback primitives");

        swapFlag = !swapFlag;
    }

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL3);

        TestTransformFeedback app = new TestTransformFeedback();
        app.setSettings(settings);
        
        app.setPauseOnLostFocus(false);
        
        app.start();
    }
}