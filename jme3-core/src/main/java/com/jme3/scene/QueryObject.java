package com.jme3.scene;

import com.jme3.renderer.Renderer;
import com.jme3.util.NativeObject;

/**
 * Query Objects are OpenGL Objects that are used for asynchronous queries of certain kinds of information.
 * https://www.khronos.org/opengl/wiki/Query_Object
 * 
 * @author Riccardo Balbo
 */
public class QueryObject extends NativeObject {
    public enum Type{
        FeedBackPrimitivesCount
    }
    protected Type type;
    protected Renderer renderer;

    public QueryObject(Type type){
        super();
        this.type = type;
    }

    public QueryObject(Type type,int id){
        super(id);
        this.type = type;
    }
    
    public Type getType(){
        return this.type;
    }

    @Override
    public void resetObject() {
        this.id = -1;
        setUpdateNeeded();
  
    }

    @Override
    public void deleteObject(Object rendererObject) {
        ((Renderer)rendererObject).deleteQuery(this);
    }

    public int getInt() {
        if(id==-1||renderer == null)return -1;
        return renderer.collectQueryOutputInt(this);
    }

    public long getLong() {
        if(id==-1||renderer == null)return -1;
        return renderer.collectQueryOutputLong(this);
    }


    @Override
    public NativeObject createDestructableClone() {
        return new QueryObject(type,id);
    }

    @Override
    public long getUniqueId() {
        return ((long)OBJTYPE_QUERY << 32) | ((long)id);
    }

    public void setRenderer(Renderer r){
        renderer=r;
    }

    public Renderer getRenderer(){
        return renderer;
    }
    
}