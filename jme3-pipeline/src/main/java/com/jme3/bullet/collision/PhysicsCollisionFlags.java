package com.jme3.bullet.collision;

/**
 * PhysicsCollisionFlags
 */
public class PhysicsCollisionFlags {
    public static final int CF_STATIC_OBJECT=1;
    public static final int CF_KINEMATIC_OBJECT=2;
    public static final int CF_NO_CONTACT_RESPONSE=4;
    public static final int CF_CUSTOM_MATERIAL_CALLBACK=8; //this allows per-triangle material (friction/restitution)
    public static final int CF_CHARACTER_OBJECT=16;
    public static final int CF_DISABLE_VISUALIZE_OBJECT=32;          //disable debug drawing
    public static final int CF_DISABLE_SPU_COLLISION_PROCESSING=64;  //disable parallel/SPU processing
    public static final int CF_HAS_CONTACT_STIFFNESS_DAMPING=128;
    public static final int CF_HAS_CUSTOM_DEBUG_RENDERING_COLOR=256;
    public static final int CF_HAS_FRICTION_ANCHOR=512;
    public static final int CF_HAS_COLLISION_SOUND_TRIGGER=1024;
    
}