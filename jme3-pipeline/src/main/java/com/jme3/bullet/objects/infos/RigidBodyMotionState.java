/*
 * Copyright (c) 2009-2012 jMonkeyEngine
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
package com.jme3.bullet.objects.infos;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsVehicle;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * stores transform info of a PhysicsNode in a threadsafe manner to
 * allow multithreaded access from the jme scenegraph and the bullet physicsspace
 * @author normenhansen
 */
public class RigidBodyMotionState {
    long motionStateId = 0;
   
    private volatile PhysicsVehicle vehicle;
    private volatile boolean applyPhysicsLocal = false;
    private volatile boolean physicsLocationDirty=false;
//    protected LinkedList<PhysicsMotionStateListener> listeners = new LinkedList<PhysicsMotionStateListener>();
    private Quaternion tmp_inverseWorldRotation = new Quaternion();

    public RigidBodyMotionState() {
        this.motionStateId = createMotionState();
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Created MotionState {0}", Long.toHexString(motionStateId));
    }

    private native long createMotionState();

    private Vector3f tmp_collectedLocation=new Vector3f();
    private Quaternion tmp_collectedRotation=new Quaternion();
    private volatile float collectedLocationX,collectedLocationY,collectedLocationZ;
    private volatile float collectedRotationX,collectedRotationY,collectedRotationZ,collectedRotationW;
    private volatile boolean collectedReady=false;

    private volatile boolean wasDirty=false;

    public void collectTransform(){
        assert Thread.currentThread()==PhysicsSpace.thread;
        boolean dirty = applyTransform(motionStateId, tmp_collectedLocation, tmp_collectedRotation);
        if(collectedReady){
            wasDirty|=dirty;
            return;
        }
        physicsLocationDirty|=dirty|wasDirty;
        wasDirty=false;
        collectedLocationX=tmp_collectedLocation.x;
        collectedLocationY=tmp_collectedLocation.y;
        collectedLocationZ=tmp_collectedLocation.z;

        collectedRotationX=tmp_collectedRotation.getX();
        collectedRotationY=tmp_collectedRotation.getY();
        collectedRotationZ=tmp_collectedRotation.getZ();
        collectedRotationW=tmp_collectedRotation.getW();
        collectedReady=true;
    }

    
    private final Transform worldTransform=new Transform();

    public Transform getWorldTransform(){
        update();
        return worldTransform;
    }

// boolean first=true;
    private boolean update(){
        // assert Thread.currentThread()!=PhysicsSpace.thread;
        
        // if(true)return false;
        if(!collectedReady)return false;
        if(!physicsLocationDirty){
            collectedReady=false;
            return false;
        }

        // if(!first)return false;
        // first=false;

        worldTransform.setTranslation(  
            collectedLocationX, 
            collectedLocationY, 
            collectedLocationZ
        );

        assert Vector3f.isValidVector(worldTransform.getTranslation());
        
        worldTransform.setRotation(
            worldTransform.getRotation().set(
                collectedRotationX, 
                collectedRotationY, 
                collectedRotationZ,
                collectedRotationW
            )
        );
        

        physicsLocationDirty=false;
        collectedReady=false;

        return true;
    }

    /**
     * applies the current transform to the given jme Node if the location has been updated on the physics side
     * @param spatial
     */
    public boolean applyTransform(Spatial spatial) {
        // if(!collectedReady)return false;
        // if(!physicsLocationDirty){
        //     collectedReady=false;
        //     return false;
        // }
        // if(!update())return false;
        update();

        Vector3f localLocation = spatial.getLocalTranslation();
        Quaternion localRotationQuat = spatial.getLocalRotation();
        // localLocation.set(collectedLocationX,collectedLocationY,collectedLocationZ);
        // localRotationQuat.set(collectedRotationX,collectedRotationY,collectedRotationZ,collectedRotationW);
        localLocation.set(worldTransform.getTranslation());
        localRotationQuat.set(worldTransform.getRotation());
        
        // boolean physicsLocationDirty = applyTransform(motionStateId, localLocation, localRotationQuat);
      
        if (!applyPhysicsLocal && spatial.getParent() != null) {
            localLocation.subtractLocal(spatial.getParent().getWorldTranslation());
            localLocation.divideLocal(spatial.getParent().getWorldScale());
            tmp_inverseWorldRotation.set(spatial.getParent().getWorldRotation()).inverseLocal().multLocal(localLocation);

//            localRotationQuat.set(worldRotationQuat);
            tmp_inverseWorldRotation.mult(localRotationQuat, localRotationQuat);

            spatial.setLocalTranslation(localLocation);
            spatial.setLocalRotation(localRotationQuat);
        } else {
            spatial.setLocalTranslation(localLocation);
            spatial.setLocalRotation(localRotationQuat);
//            spatial.setLocalTranslation(worldLocation);
//            spatial.setLocalRotation(worldRotationQuat);
        }
        if (vehicle != null) {
            vehicle.updateWheels();
        }
        // physicsLocationDirty=false;
        // collectedReady=false;
        return true;
    }


    private Vector3f worldLocation = new Vector3f();
    private Matrix3f worldRotation = new Matrix3f();
    private Quaternion worldRotationQuat = new Quaternion();
    private native boolean applyTransform(long stateId, Vector3f location, Quaternion rotation);

    /**
     * @return the worldLocation
     */
    // public Vector3f getWorldLocation() {
    //     assert Thread.currentThread()==PhysicsSpace.thread;
    //     getWorldLocation(motionStateId, worldLocation);
    //     return worldLocation;
    // }

    private native void getWorldLocation(long stateId, Vector3f vec);

    /**
     * @return the worldRotation
     */
    // public Matrix3f getWorldRotation() {
    //     assert Thread.currentThread()==PhysicsSpace.thread;
    //     getWorldRotation(motionStateId, worldRotation);
    //     return worldRotation;
    // }

    private native void getWorldRotation(long stateId, Matrix3f vec);

    /**
     * @return the worldRotationQuat
     */
    // public Quaternion getWorldRotationQuat() {
    //     assert Thread.currentThread()==PhysicsSpace.thread;
    //     getWorldRotationQuat(motionStateId, worldRotationQuat);
    //     return worldRotationQuat;
    // }

    private native void getWorldRotationQuat(long stateId, Quaternion vec);

    /**
     * @param vehicle the vehicle to set
     */
    public void setVehicle(PhysicsVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public boolean isApplyPhysicsLocal() {
        return applyPhysicsLocal;
    }

    public void setApplyPhysicsLocal(boolean applyPhysicsLocal) {
        this.applyPhysicsLocal = applyPhysicsLocal;
    }
    
    public long getObjectId(){
        return motionStateId;
    }
//    public void addMotionStateListener(PhysicsMotionStateListener listener){
//        listeners.add(listener);
//    }
//
//    public void removeMotionStateListener(PhysicsMotionStateListener listener){
//        listeners.remove(listener);
//    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Finalizing MotionState {0}", Long.toHexString(motionStateId));
        finalizeNative(motionStateId);
    }

    private native void finalizeNative(long objectId);


    // @Deprecated
	// public Vector3f getWorldLocation() {
	// 	return getWorldTransform().getTranslation();
	// }

    // @Deprecated
	// public Quaternion getWorldRotationQuat() {
	// 	return getWorldTransform().getRotation();
	// }
}
