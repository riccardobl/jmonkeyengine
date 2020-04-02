package com.jme3.rendering.pipeline.params.primitives;

/**
 * MutablePrimitive
 */
public interface MutablePrimitive<T> extends java.io.Serializable {

    public void setValue(T v) ;

    public T getValue();


}