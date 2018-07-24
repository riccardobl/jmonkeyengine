package jme3test.effect;

import com.bulletphysics.collision.shapes.SphereShape;
import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.SimpleApplication;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.Control;
import com.jme3.scene.instancing.InstancedParticle;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

/**
 * TestParticleGroup
 */
public class TestParticleGroup extends SimpleApplication {
    AnimControl anim;
    SkeletonControl ske;
    Mesh mesh;
    Texture tx;

    public static void main(String[] args) {
        AppSettings settings=new AppSettings(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL3);
        settings.setVSync(false);
        settings.setFrameRate(400);
        TestParticleGroup app=new TestParticleGroup();
        app.setSettings(settings);;
        app.start();
    }


    public void loadJaime() {

        assetManager.loadModel("Models/Jaime/Jaime.j3o").depthFirstTraversal(new SceneGraphVisitor(){

            @Override
            public void visit(Spatial s) {
                if(s instanceof Geometry){
                    System.out.println("Found geometry");
                    mesh=((Geometry)s).getMesh();
                    tx=((Geometry)s).getMaterial().getTextureParam("DiffuseMap").getTextureValue();
                }
                Control c=s.getControl(AnimControl.class);
                if(c!=null){
                    System.out.println("Found animcontrol");
                    anim=(AnimControl)c;
                    s.removeControl(anim);
                }
                c=s.getControl(SkeletonControl.class);
                if(c!=null){
                    System.out.println("Found skeleton");
                    ske=(SkeletonControl)c;
                    s.removeControl(ske);
                }
            }
        });

        ske.setHardwareSkinningPreferred(false);

        ip=new InstancedParticle(assetManager,"Test1",mesh,1800);
        ip.addControl(ske);
        ip.addControl(anim);
        ip.setQueueBucket(Bucket.Transparent);
        ip.getMaterial().setTexture("DiffuseMap",tx);

        anim.createChannel().setAnim("Punches");
        ip.setCullHint(CullHint.Never);
    }
    


    public void loadSimple(){
         mesh=new Sphere(4,4,.1f);

        ip=new InstancedParticle(assetManager,"Test1",mesh,180000);
        ip.setQueueBucket(Bucket.Transparent);
        
        ip.setCullHint(CullHint.Never);
    }
    InstancedParticle ip;
    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10f);
        // loadJaime();
        loadSimple();
        rootNode.attachChild(ip);
    }
    float time=0;
    @Override
    public void simpleUpdate(float tpf){
        time+=tpf;
        ip.setLocalTranslation(0,FastMath.sin(time),0);
    }
    
}