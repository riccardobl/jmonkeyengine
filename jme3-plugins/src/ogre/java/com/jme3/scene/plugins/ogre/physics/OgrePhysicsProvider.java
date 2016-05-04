package com.jme3.scene.plugins.ogre.physics;

import org.xml.sax.Attributes;

import com.jme3.scene.Node;
import com.jme3.scene.plugins.ogre.OgreSceneKey;

public interface OgrePhysicsProvider{
	public void apply(OgreSceneKey key,Node entityNoze,Attributes attribs);
}
