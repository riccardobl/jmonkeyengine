package jme3test.effect;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Spatial;
import com.jme3.scene.instancing.InstancedParticle;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;

/**
 * TestParticleGroupWithPhysics
 */
public class TestParticleGroupWithPhysics  extends SimpleApplication {

    BulletAppState bulletAppState;
    public static void main(String[] args) {
        AppSettings settings=new AppSettings(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL3);
        settings.setVSync(false);
        settings.setFrameRate(400);
        TestParticleGroupWithPhysics app=new TestParticleGroupWithPhysics();
        app.setSettings(settings);;
        app.start();
    }

    // InstancedParticle ip;
    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        // bulletAppState.setDebugEnabled(true);


        flyCam.setMoveSpeed(10f);
        assetManager.registerLocator("/tmp",FileLocator.class);
        Spatial scene=assetManager.loadModel("Scenes/Bonfire/Bonfire2.blend");
        scene.addControl(new RigidBodyControl(0));
        bulletAppState.getPhysicsSpace().add(scene);

        rootNode.attachChild(scene);
        rootNode.addLight(new AmbientLight(ColorRGBA.Pink.mult(.1f)));
        
        viewPort.setBackgroundColor(ColorRGBA.Pink.mult(0.6f));
    }

    float time=0;

    @Override
    public void simpleUpdate(float tpf) {
        time+=tpf;
        if(time>.2){
            BoxCollisionShape s=new BoxCollisionShape(new Vector3f(.1f,.1f,.1f));
            RigidBodyControl rb=new RigidBodyControl(s,.1f);

            Sphere mesh=new Sphere(4,4,.5f);
            InstancedParticle ip=new InstancedParticle(assetManager,"particle",mesh,32);
            ip.getMaterial().setFloat("SpawnTime",this.getTimer().getTimeInSeconds());
            ip.setQueueBucket(Bucket.Transparent);
            ip.addControl(rb);
            rootNode.attachChild(ip);
            bulletAppState.getPhysicsSpace().add(ip);
            rb.setGravity(new Vector3f(0,5,0));
            rb.setPhysicsLocation(new Vector3f(0,.4f,0));
            rb.setAngularFactor(0);
            rb.setFriction(0);
            time=0;
        }
    }
}