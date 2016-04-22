package com.jme3.scene.plugins.ogre;

import com.jme3.asset.ModelKey;
import com.jme3.scene.plugins.ogre.physics.OgrePhysicsBullet;
import com.jme3.scene.plugins.ogre.physics.OgrePhysicsProvider;

public class OgreSceneKey extends ModelKey {
	private OgrePhysicsProvider phyProvider;
	public OgreSceneKey(){
		super();
	}
	
	public OgreSceneKey(String path){
		super(path);
	}
	
	public OgreSceneKey(String path,OgrePhysicsProvider physics){
		this(path);
		phyProvider=physics;
	}
	
	public OgreSceneKey usePhysics(boolean v){
		if(v)return usePhysics(new OgrePhysicsBullet());
		else return usePhysics(null);
	}

	
	public OgreSceneKey usePhysics(OgrePhysicsProvider phyProvider){
		this.phyProvider= phyProvider;
		return this;
	}
	
	protected OgrePhysicsProvider getPhysicsProvider(){
		return phyProvider;
	}
}
