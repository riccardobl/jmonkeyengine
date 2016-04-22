package com.jme3.scene.plugins.ogre.physics;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;

public final class OgrePhysicsHelpers{

	public static BoundingBox getBoundingBox(Node n) {
		final Collection<VertexBuffer> meshes=new ArrayList<VertexBuffer>();
		n.depthFirstTraversal(new SceneGraphVisitor(){
			@Override
			public void visit(Spatial spatial) {
				if(spatial instanceof Geometry){
					VertexBuffer vb=((Geometry)spatial).getMesh().getBuffer(Type.Position).clone();
					Vector3f scale=spatial.getWorldScale();
					for(int i=0;i<vb.getNumElements();i++){
						vb.setElementComponent(i,0,(float)vb.getElementComponent(i,0)*scale.x);
						vb.setElementComponent(i,1,(float)vb.getElementComponent(i,1)*scale.y);
						vb.setElementComponent(i,2,(float)vb.getElementComponent(i,2)*scale.z);
					}
					meshes.add(vb);
				}
			}
		});

		Vector3f min=null;
		Vector3f max=null;
		for(VertexBuffer vertices:meshes){
			for(int i=0;i<vertices.getNumElements();i++){
				Vector3f v=getVector3f(vertices,i);
				boolean c=false;
				if(min==null){
					c=true;
					min=v.clone();
				}
				if(max==null){
					c=true;
					max=v.clone();
				}
				if(c) continue;
				if(v.x<min.x) min.x=v.x;
				if(v.y<min.y) min.y=v.y;
				if(v.z<min.z) min.z=v.z;
				if(v.x>max.x) max.x=v.x;
				if(v.y>max.y) max.y=v.y;
				if(v.z>max.z) max.z=v.z;
			}
		}
		return new BoundingBox(min,max);
	}

	public static Vector3f getVector3f(VertexBuffer v, int id) {
		if(v==null||v.getNumElements()<=id) return new Vector3f();
		return new Vector3f((float)v.getElementComponent(id,0),(float)v.getElementComponent(id,1),(float)v.getElementComponent(id,2));
	}

	public static Collection<Float> getPoints(Mesh mesh, Vector3f scale) {
		FloatBuffer vertices=mesh.getFloatBuffer(Type.Position);
		vertices.rewind();
		int components=mesh.getVertexCount()*3;
		ArrayList<Float> pointsArray=new ArrayList<Float>();
		for(int i=0;i<components;i+=3){
			pointsArray.add(vertices.get()*scale.x);
			pointsArray.add(vertices.get()*scale.y);
			pointsArray.add(vertices.get()*scale.z);
		}
		return pointsArray;
	}

}
