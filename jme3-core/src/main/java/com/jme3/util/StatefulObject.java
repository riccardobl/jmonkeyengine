package com.jme3.util;

import java.util.WeakHashMap;

import com.jme3.util.Function.NoArgFunction;
import com.jme3.util.Function.VoidFunction;

/**
 * StatefulObject
 */
public class StatefulObject implements Cloneable{
    private static ThreadLocal<Long> globalId = new ThreadLocal<Long>() {
        @Override
        protected Long initialValue() {
            return Long.MIN_VALUE;
        }
    };

    private static long getGlobalId(){
        long n=globalId.get();
        long nn=n+1;
        if(nn==-1||n==0)nn=1;
        globalId.set(nn);
        return n;
    }

    public static class State{
        private boolean updateNeeded=true;
        private long stateId=0;

        public long getStateId(){
            return stateId;
        }

        public boolean isStateUpdateNeeded(){
            return updateNeeded;
        }
        
        public void setStateUpdateNeeded(){
             updateNeeded=true;
        }

        public State cloneStateFor(StatefulObject obj){
            return null;
        }

        public void clearStateUpdateNeeded(){
            updateNeeded=false;
            stateId=getGlobalId();
        }
    }

    private transient ThreadLocal<WeakHashMap<Object, State>> states;
    
    {
        states = new ThreadLocal<WeakHashMap<Object, State>>() {
            @Override
            protected WeakHashMap<Object, State> initialValue() {
                return new WeakHashMap<Object, State>();
            }
        };
    }

    public <T extends State> T  getState(Object id, NoArgFunction<T> constructor) {
        
        State state = states.get().get(id);
        if (state == null && constructor != null) {
            state = constructor.eval();
            states.get().put(id, state);
        }
        return (T)state;
    }

    public Object removeState(Object id) {
        return states.get().remove(id);
    }

    protected void setStateUpdateNeeded(){
        WeakHashMap<Object,State> m=states.get();
        for(State s:m.values()){
            s.setStateUpdateNeeded();
        }

    }

    protected void forEachState(VoidFunction<State> f){
        WeakHashMap<Object,State> m=states.get();
        for(State s:m.values()){
            f.eval(s);
        }
    }



    protected WeakHashMap<Object,State> getStates(){
        WeakHashMap<Object,State> m=states.get();
        return m;
    }

    @Override
    protected StatefulObject clone() throws CloneNotSupportedException {
        StatefulObject clone=(StatefulObject)super.clone();
        WeakHashMap<Object,State> states=this.states.get();
        WeakHashMap<Object,State> clonedStates=clone.states.get();
        assert states!=clonedStates;        
        for(Object k:states.keySet()){
            State s=states.get(k);
            s=s.cloneStateFor(clone);
            if(s!=null)clonedStates.put(k,s);
        }
        return clone;
    }

   
}