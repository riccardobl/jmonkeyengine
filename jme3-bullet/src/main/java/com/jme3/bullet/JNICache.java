package com.jme3.bullet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * JNICache
 */
public class JNICache{
    public static boolean enabled=true;
    private final Map<Object,Collection<Entry>> cached=new WeakHashMap<Object,Collection<Entry>>();

    public static class Entry<T> {
        private T val;
        private boolean valid=false;
        protected Entry(){

        }

        public boolean isValid() {
            return valid&&val!=null;
        }
        
        public void invalidate() {
            valid=false;
            val=null;
        }
        
        public void set(T t) {
            val=t;
            valid=true;
        }

        public T get() {
            return val;
        }

        
    }
    
    public <T> Entry<T> cacheFor(Object o) {
        Entry<T> e=new Entry<T>();
        // e.set(val);
        
        // Collection<Entry> ne=new LinkedList<Entry>();
        Collection<Entry> ce=cached.get(o);
        if(ce==null){
            ce=new LinkedList<Entry>();
            cached.put(o,ce);
        }
        // if(ne==ce) 
        ce.add(e);
        return e;
    }

    // public <T> void remove(Entry<T> e) {
    //     cached.remove(e);
    // }




    public void update() {
        for(Collection<Entry >v:cached.values()){
            for(Entry e:v){
                e.invalidate();
            }
        }
    }
    
}