package com.jme3.rendering.pipeline.params.primitives;

/**
 * MutableNumber
 */
public  class MutableNumber<T> implements MutablePrimitive<Number> {
    private Number value=0;
    public MutableNumber(Number n){
        setValue(n);
    }

    public MutableNumber(){}
    @Override
    public void setValue(Number v) {
        value=v;
    }

    @Override
    public Number getValue(){
        return value;
    }

    private static final long serialVersionUID = 1L;

    public  int intValue(){
        return value.intValue();
    }

    public long longValue(){
        return value.longValue();
    }


    public  float floatValue(){
        return value.floatValue();
    }


    public  double doubleValue(){
        return value.doubleValue();

    }

  
    public byte byteValue() {
        return value.byteValue();
    }


    public short shortValue() {
        return  value.shortValue();
    }

    @Override
    public String toString(){
        return ""+getValue();
    }
}