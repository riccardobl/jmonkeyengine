package com.jme3.util;


public interface Function<R,T> {
    public static interface NoArgFunction<R> {
        R eval();
    }
    public static interface VoidFunction<T> {
        void eval(T t);
    }
    public static interface NoArgVoidFunction {
        void eval();
    }
    R eval(T t);
}