package com.jme3.scene.plugins.ogre;

import com.jme3.asset.ModelKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.plugins.ogre.physics.OgrePhysicsBullet;
import com.jme3.scene.plugins.ogre.physics.OgrePhysicsProvider;

public class OgreSceneKey extends ModelKey {
	private OgrePhysicsProvider phyProvider;
	private Object vhacdFactory;
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
		try{
			if(v)return usePhysics(new OgrePhysicsBullet());
			else return usePhysics(null);
		}catch(Throwable e){}
		return this;
	}

	public Object getVHACDFactory(){
		return vhacdFactory;
	}
	
	public OgreSceneKey useVHACD(Object factory){
		if(factory ==null){
			vhacdFactory=null;
			return this;
		}
		try{
			if(factory instanceof com.jme3.bullet.vhacd.VHACDCollisionShapeFactory||factory instanceof Boolean){
				vhacdFactory=factory;
			}
		}catch(Throwable e){}
		return this;
	}
	
	public OgreSceneKey usePhysics(OgrePhysicsProvider phyProvider){
		this.phyProvider= phyProvider;
		return this;
	}
	
	protected OgrePhysicsProvider getPhysicsProvider(){
		return phyProvider;
	}
}
