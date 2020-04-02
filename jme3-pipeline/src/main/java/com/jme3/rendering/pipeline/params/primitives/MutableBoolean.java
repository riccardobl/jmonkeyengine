package com.jme3.rendering.pipeline.params.primitives;

/**
 * MutableBoolean
 */
public class MutableBoolean implements MutablePrimitive<Boolean> {
    private Boolean value=false;

    public MutableBoolean(){};
    public MutableBoolean(Boolean n){
        setValue(n);
    };

    @Override
    public void setValue(Boolean v) {
        value=v;
    }

    @Override
    public Boolean getValue(){
        return value;
    }

    private static final long serialVersionUID = 1L;

    public  boolean booleanValue(){
        return value;
    }
    @Override
    public String toString(){
        return ""+getValue();
    }
}