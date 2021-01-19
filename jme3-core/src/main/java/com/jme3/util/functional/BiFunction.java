package com.jme3.util.functional;


public interface BiFunction<R,A,B> {
    R eval(A a,B b);
}