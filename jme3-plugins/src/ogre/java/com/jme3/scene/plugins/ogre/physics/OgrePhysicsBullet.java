package com.jme3.scene.plugins.ogre.physics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.GImpactCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.plugins.ogre.SceneLoader;

public class OgrePhysicsBullet implements OgrePhysicsProvider{
	private static final Logger logger=Logger.getLogger(SceneLoader.class.getName());

	public void apply(final Node entityNode,final Attributes attribs) {
		final String collisionShapeName=attribs.getValue("collisionPrim");
		if(collisionShapeName==null) return;

		final boolean isGhost=Objects.equals(attribs.getValue("ghost"),"True");
		String physics_type=attribs.getValue("physics_type");if(physics_type==null) physics_type="STATIC";
		
		final boolean isStatic=physics_type.equals("STATIC");
		
		CollisionShape collisionShape=null;
		switch(collisionShapeName){
			case "triangle_mesh":
				final CompoundCollisionShape csh=new CompoundCollisionShape();
				entityNode.depthFirstTraversal(new SceneGraphVisitor(){
					@Override
					public void visit(Spatial s) {
						if(s instanceof Geometry){
							Geometry g=(Geometry)s;
							Mesh mesh=g.getMesh();
							CollisionShape shape=isStatic?new MeshCollisionShape(mesh):new GImpactCollisionShape(mesh);
							shape.setScale(g.getWorldScale());
							csh.addChildShape(shape,g.getWorldTranslation().subtract(entityNode.getWorldTranslation()));
						}
					}
				});
				collisionShape=csh;
				break;

			case "sphere":
				Vector3f xtendsphere=OgrePhysics.getBoundingBox(entityNode).getExtent(null);
				float radius=xtendsphere.x;
				if(xtendsphere.y>radius) radius=xtendsphere.y;
				if(xtendsphere.z>radius) radius=xtendsphere.z;
				collisionShape=new SphereCollisionShape(radius);
				break;

			case "convex_hull":
				final Collection<Float> points=new ArrayList<Float>();
				entityNode.depthFirstTraversal(new SceneGraphVisitor(){
					@Override
					public void visit(Spatial s) {
						if(s instanceof Geometry){
							Geometry g=(Geometry)s;
							points.addAll(OgrePhysics.getPoints(g.getMesh(),g.getWorldScale()));
						}
					}
				});
				float primitive_arr[]=new float[points.size()];
				int i=0;
				for(Float point:points)
					primitive_arr[i++]=(float)point;
				collisionShape=new HullCollisionShape(primitive_arr);
				break;

			case "box":
				BoundingBox bbox=OgrePhysics.getBoundingBox(entityNode);
				collisionShape=new BoxCollisionShape(bbox.getExtent(null));
				break;

			case "capsule":
				BoundingBox cbox=OgrePhysics.getBoundingBox(entityNode);
				Vector3f xtendcapsule=cbox.getExtent(null);
				collisionShape=new CapsuleCollisionShape((xtendcapsule.x>xtendcapsule.z?xtendcapsule.x:xtendcapsule.z),xtendcapsule.y);
				break;

			case "cylinder":
				BoundingBox cybox=OgrePhysics.getBoundingBox(entityNode);
				Vector3f xtendcylinder=cybox.getExtent(null);
				collisionShape=new CylinderCollisionShape(xtendcylinder);
				break;

			case "cone":
				BoundingBox cobox=OgrePhysics.getBoundingBox(entityNode);
				Vector3f xtendcone=cobox.getExtent(null);
				collisionShape=new ConeCollisionShape((xtendcone.x>xtendcone.z?xtendcone.x:xtendcone.z),xtendcone.y,PhysicsSpace.AXIS_Y);
				break;
			default:
				logger.log(Level.WARNING,"{0} unsupported",collisionShapeName);
		}
		if(collisionShape==null) return;
		
		if(isGhost){
			GhostControl ghost=new GhostControl(collisionShape);
			entityNode.addControl(ghost);
		}else{
			float mass=isStatic?0:Float.parseFloat(attribs.getValue("mass"));
			RigidBodyControl rigidbody=new RigidBodyControl(collisionShape,mass);
			float damping_rot=Float.parseFloat(attribs.getValue("damping_rot"));
			float damping_trans=Float.parseFloat(attribs.getValue("damping_trans"));
			Vector3f friction=new Vector3f(Float.parseFloat(attribs.getValue("friction_x")),Float.parseFloat(attribs.getValue("friction_y")),Float.parseFloat(attribs.getValue("friction_z")));
			rigidbody.setFriction((friction.x+friction.y+friction.z)/3f);
			rigidbody.setAngularDamping(damping_rot);
			rigidbody.setLinearDamping(damping_trans);
			entityNode.addControl(rigidbody);
		}

	}

	@Override
	public boolean isAvailable() {
		try{
			Class.forName(BulletAppState.class.getName());
			return true;
		}catch(ClassNotFoundException e){
			return false;
		}
	}
}