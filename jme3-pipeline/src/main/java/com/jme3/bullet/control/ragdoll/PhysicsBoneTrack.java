package com.jme3.bullet.control.ragdoll;

import java.io.IOException;
import java.util.BitSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.jme3.animation.*;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Transform;
import com.jme3.util.TempVars;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

/**
 * PhysicsBone
 */
public class PhysicsBoneTrack implements Track, JmeCloneable {
    private int targetBoneIndex;
    private PhysicsRigidBody rb;

    public PhysicsBoneTrack(){
    }

    public PhysicsBoneTrack(int targetBone,PhysicsRigidBody rb){
        this.targetBoneIndex=targetBone;
        this.rb=rb;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(targetBoneIndex, "boneIndex", 0);
        oc.write(rb, "rigidBody", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        targetBoneIndex = ic.readInt("boneIndex", 0);
        rb=(PhysicsRigidBody)ic.readSavable("rigidBody",null);
    }
    
    @Override
    public void setTime(float time, float weight, AnimControl control, AnimChannel channel, TempVars vars) {

        if(rb==null)return;
        
        BitSet affectedBones = channel.getAffectedBones();
        
        if (affectedBones != null && !affectedBones.get(targetBoneIndex)) {
            return;
        }

        Bone target = control.getSkeleton().getBone(targetBoneIndex);

        Transform worldTr=rb.getMotionState().getWorldTransform();
        Transform localTr;
        // TODO
        // target.getParent()

        // target.setLocalTransform(localTr, weight);
        // rb.getMotionState().collectTransform();

    }

    @Override
    public float getLength() {
        return 1f;
    }

    @Override
    public void cloneFields( Cloner cloner, Object original ) { 
        this.rb = cloner.clone(rb);
    }

	@Override
	public float[] getKeyFrameTimes() {
		return new float[]{0};
	}

    @Override
    public Object jmeClone() {
        try{
            return (Track)super.clone();
        }catch(Exception e){
            assert false;
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Track clone() {
        return Cloner.deepClone(this);
    }




    
}