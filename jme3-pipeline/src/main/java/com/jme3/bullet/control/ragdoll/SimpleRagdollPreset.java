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
package com.jme3.bullet.control.ragdoll;


import java.util.logging.Level;

import com.jme3.bullet.control.ragdoll.RagdollPreset;
import com.jme3.bullet.joints.SixDofJoint;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;


public class SimpleRagdollPreset extends RagdollPreset {

    @Override
    protected void initBoneMap() {
      
    }

    @Override
    protected void initLexicon() {
      
    }
    
    public void setupJointForBone(String boneName, SixDofJoint joint) {

//    	new JointPreset(FastMath.QUARTER_PI, -FastMath.QUARTER_PI,
//    			FastMath.QUARTER_PI, -FastMath.QUARTER_PI,
//    			FastMath.QUARTER_PI, -FastMath.QUARTER_PI)
//    	
    	setupJoint(joint);
    	
    }
    
//    protected class JointPreset {

//        private float maxX, minX, maxY, minY, maxZ, minZ;
//
//        public JointPreset() {
//        }

//        public JointPreset(float maxX, float minX, float maxY, float minY, float maxZ, float minZ) {
//            this.maxX = maxX;
//            this.minX = minX;
//            this.maxY = maxY;
//            this.minY = minY;
//            this.maxZ = maxZ;
//            this.minZ = minZ;
//        }

        public void setupJoint(SixDofJoint joint) {
//            joint.getRotationalLimitMotor(0).setHiLimit(maxX);
//            joint.getRotationalLimitMotor(0).setLoLimit(minX);
//            joint.getRotationalLimitMotor(1).setHiLimit(maxY);
//            joint.getRotationalLimitMotor(1).setLoLimit(minY);
//            joint.getRotationalLimitMotor(2).setHiLimit(maxZ);
//            joint.getRotationalLimitMotor(2).setLoLimit(minZ);
        	Vector3f ll=Vector3f.NEGATIVE_INFINITY.clone();
        	Vector3f ul=Vector3f.POSITIVE_INFINITY.clone();
        	ll.x=-.5f;
        	ul.x=.5f;
        	ll.z=-.5f;
        	ul.z=.5f;
        	ll.y=-.2f;
        	ul.y=.2f;
        	
        	joint.setAngularLowerLimit(ll);
    		joint.setAngularUpperLimit(ul);
//    		joint.setAngularLowerLimit(new Vector3f(0,0,0));
//    		joint.setAngularUpperLimit(new Vector3f(0,0,0));
//    		joint.setLinearLowerLimit(new Vector3f(1,1,1));
//    		joint.setLinearUpperLimit(new Vector3f(1,1,1));
            for(int i=0;i<2;i++){
//            	joint.getRotationalLimitMotor(i).setMaxLimitForce(.02f);
//            	joint.getRotationalLimitMotor(i).setMaxMotorForce(.01f);
//            	joint.getRotationalLimitMotor(i).setDamping(.9f);
            	joint.getRotationalLimitMotor(i).setBounce(0);
            	
            	
//            	joint.getRotationalLimitMotor(i).setMaxMotorForce(.1f);
            }
//            joint.getRotationalLimitMotor(0).set
        }
//    }
}
