package com.jme3.scene.plugins.ogre.physics;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class OgreRigidBodyControl extends RigidBodyControl{

	public OgreRigidBodyControl(float mass){
		super(mass);
	}

	public OgreRigidBodyControl(CollisionShape shape){
		super(shape);
	}

	public OgreRigidBodyControl(CollisionShape shape,float mass){
		super(shape,mass);
	}

	@Override
	public OgreRigidBodyControl jmeClone() {
		OgreRigidBodyControl control=new OgreRigidBodyControl(collisionShape,mass);
		control.spatial=this.spatial;
		control.setAngularFactor(getAngularFactor());
		control.setAngularSleepingThreshold(getAngularSleepingThreshold());
		control.setCcdMotionThreshold(getCcdMotionThreshold());
		control.setCcdSweptSphereRadius(getCcdSweptSphereRadius());
		control.setCollideWithGroups(getCollideWithGroups());
		control.setCollisionGroup(getCollisionGroup());
		control.setDamping(getLinearDamping(),getAngularDamping());
		control.setFriction(getFriction());
		control.setGravity(getGravity());
		control.setKinematic(isKinematic());
		control.setKinematicSpatial(isKinematicSpatial());
		control.setLinearSleepingThreshold(getLinearSleepingThreshold());
		control.setPhysicsLocation(getPhysicsLocation(null));
		control.setPhysicsRotation(getPhysicsRotationMatrix(null));
		control.setRestitution(getRestitution());
		if(mass>0){
			control.setAngularVelocity(getAngularVelocity());
			control.setLinearVelocity(getLinearVelocity());
		}
		control.setApplyPhysicsLocal(isApplyPhysicsLocal());
		return control;
	}

	@Override
	public void setPhysicsSpace(PhysicsSpace space) {
		super.setPhysicsSpace(space);
		if(space!=null&&spatial!=null){
			setPhysicsLocation(getSpatialTranslation());
			setPhysicsRotation(getSpatialRotation());
		}
	}

	private Vector3f getSpatialTranslation() {
		if(motionState.isApplyPhysicsLocal()){ return spatial.getLocalTranslation(); }
		return spatial.getWorldTranslation();
	}

	private Quaternion getSpatialRotation() {
		if(motionState.isApplyPhysicsLocal()){ return spatial.getLocalRotation(); }
		return spatial.getWorldRotation();
	}

	@Override
	public void setPhysicsLocation(Vector3f v) {
		super.setPhysicsLocation(v);
		if(space==null&&spatial!=null){
			Quaternion old_rot=spatial.getLocalRotation().clone();
			getMotionState().applyTransform(spatial);
			spatial.setLocalRotation(old_rot);
		}
	}

	@Override
	public void setPhysicsRotation(Quaternion r) {
		super.setPhysicsRotation(r);
		 if(space==null&&spatial!=null){
			Vector3f old_pos=spatial.getLocalTranslation().clone();
			getMotionState().applyTransform(spatial);
			spatial.setLocalTranslation(old_pos);
		}
	}

	@Override
	public Vector3f getPhysicsLocation(Vector3f trans) {
		if(space==null&&spatial!=null){
			if(trans==null){
				trans=new Vector3f();
			}
			return trans.set(spatial.getWorldTranslation());
		}else{
			return super.getPhysicsLocation(trans);
		}
	}
	
	@Override
	public Quaternion getPhysicsRotation(Quaternion rot){
		if(space==null&&spatial!=null){
			if (rot == null) {
	            rot = new Quaternion();
	        }
			return rot.set(spatial.getWorldRotation());
		}else return super.getPhysicsRotation(rot);		
	}

	@Override
	public void update(float tpf) {
		if(space==null) return;
		super.update(tpf);
	}
}
