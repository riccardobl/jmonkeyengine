// package com.jme3.bullet.control;

// import java.util.List;
// import java.util.Map;

// import wf.frk.f3banimation.AnimControl;
// import wf.frk.f3banimation.Bone;
// import wf.frk.f3banimation.Skeleton;
// import com.jme3.bullet.PhysicsSpace;
// import com.jme3.bullet.PhysicsTickListener;
// import com.jme3.bullet.collision.PhysicsCollisionEvent;
// import com.jme3.bullet.collision.PhysicsCollisionListener;
// import com.jme3.bullet.collision.shapes.HullCollisionShape;
// import com.jme3.bullet.control.ragdoll.RagdollUtils;
// import com.jme3.bullet.objects.PhysicsRigidBody;
// import com.jme3.math.Quaternion;
// import com.jme3.math.Vector3f;
// import com.jme3.scene.Spatial;
// import com.jme3.util.clone.Cloner;
// import com.jme3.util.clone.JmeCloneable;

// /**
//  * RagdollControl
//  */
// public class RigidRagdollControl  extends RigidBodyControl implements PhysicsTickListener, JmeCloneable {
//     protected Skeleton skeleton;
//     protected float weightThreshold = -1.0f;

//     protected void scanSpatial(Spatial model) {
//         AnimControl animControl = model.getControl(AnimControl.class);
//         Map<Integer, List<Float>> pointsMap = null;
//         if (weightThreshold == -1.0f) {
//             pointsMap = RagdollUtils.buildPointMap(model);
//         }

//         skeleton = animControl.getSkeleton();
//         skeleton.resetAndUpdate();
//         for (int i = 0; i < skeleton.getRoots().length; i++) {
//             Bone childBone = skeleton.getRoots()[i];
//             if (childBone.getParent() == null) {
//                 boneRecursion(model, childBone, baseRigidBody, 1, pointsMap);
//             }
//         }
//     }


//     protected void boneRecursion(Spatial model, Bone bone, PhysicsRigidBody parent, int reccount, Map<Integer, List<Float>> pointsMap) {
//         PhysicsRigidBody parentShape = parent;
//         if (boneList.isEmpty() || boneList.contains(bone.getName())) {

//             PhysicsBoneLink link = new PhysicsBoneLink();
//             link.bone = bone;

//             //creating the collision shape 
//             HullCollisionShape shape = null;
//             // if (pointsMap != null) {
//                 //build a shape for the bone, using the vertices that are most influenced by this bone
//                 shape = RagdollUtils.makeShapeFromPointMap(pointsMap, RagdollUtils.getBoneIndices(link.bone, skeleton, boneList), initScale, link.bone.getModelSpacePosition());
//             // } 
//             // else {
//             //     //build a shape for the bone, using the vertices associated with this bone with a weight above the threshold
//             //     shape = RagdollUtils.makeShapeFromVerticeWeights(model, RagdollUtils.getBoneIndices(link.bone, skeleton, boneList), initScale, link.bone.getModelSpacePosition(), weightThreshold);
//             // }

//             PhysicsRigidBody shapeNode = new PhysicsRigidBody(shape, rootMass / (float) reccount);

//             shapeNode.setKinematic(mode == Mode.Kinematic);
//             totalMass += rootMass / (float) reccount;

//             link.rigidBody = shapeNode;
//             link.initalWorldRotation = bone.getModelSpaceRotation().clone();

//             if (parent != null) {
//                 //get joint position for parent
//                 Vector3f posToParent = new Vector3f();
//                 if (bone.getParent() != null) {
//                     bone.getModelSpacePosition().subtract(bone.getParent().getModelSpacePosition(), posToParent).multLocal(initScale);
//                 }

//                 SixDofJoint joint = new SixDofJoint(parent, shapeNode, posToParent, new Vector3f(0, 0, 0f), true);
//                 preset.setupJointForBone(bone.getName(), joint);

//                 link.joint = joint;
//                 joint.setCollisionBetweenLinkedBodys(false);
//             }
//             boneLinks.put(bone.getName(), link);
//             shapeNode.setUserObject(link);
//             parentShape = shapeNode;
//         }

//         for (Iterator<Bone> it = bone.getChildren().iterator(); it.hasNext();) {
//             Bone childBone = it.next();
//             boneRecursion(model, childBone, parentShape, reccount + 1, pointsMap);
//         }
//     }

//     public void setPhysicsSpace(PhysicsSpace space) {
//         if(space!=null){
//             space.addTickListener(this);
//         }else{
//             super.space.removeTickListener(this);
//         }
//         super.setPhysicsSpace(space);
//     }
   

//     @Override
//     public void physicsTick(PhysicsSpace space, float tpf) {

//     }
    

//     @Override
//     public void prePhysicsTick(PhysicsSpace space, float tpf) {

//     }

   
	

// 	@Override
// 	public Object jmeClone() {
// 		return null;
// 	}




//     @Override
//     public void cloneFields(Cloner cloner, Object original) {

// 	}

   

//     // @Override
//     // protected void addPhysics(PhysicsSpace space) {

//     // }

//     // @Override
//     // protected void removePhysics(PhysicsSpace space) {

//     // }


    
// }