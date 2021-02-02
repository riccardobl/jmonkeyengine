package com.jme3.util;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.jme3.util.functional.NoArgFunction;
import com.jme3.util.functional.VoidFunction;

/**
 * StatefulObject
 */
public class StatefulObject implements Cloneable{
//     private static ThreadLocal<Long> globalId = new ThreadLocal<Long>() {
//         @Override
//         protected Long initialValue() {
//             return Long.MIN_VALUE;
//         }
//     };


    private static AtomicLong globalId=new AtomicLong(Long.MIN_VALUE);

    private static long getGlobalId(){
        return  globalId.getAndUpdate(n->{
            long nn=n+1;
            if(nn==-1||n==0)nn=1;    
            return nn;
        });
    }

    public static class State{
        private volatile boolean updateNeeded=true;
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

    // private transient ThreadLocal<WeakHashMap<Object, State>> states;
    private transient Map<Object, State> states=null;
    
    protected Map<Object, State> getStates(){
        if(states==null){
            states=(Map<Object, State>) Collections.synchronizedMap(new WeakHashMap<Object, State>());
            // states = new ThreadLocal<WeakHashMap<Object, State>>() {
            //     @Override
            //     protected WeakHashMap<Object, State> initialValue() {
            //         return new WeakHashMap<Object, State>();
            //     }
            // };
        }
        return states;
    }

    public <T extends State> T  getState(Object id, NoArgFunction<T> constructor) {
        
        State state = getStates().get(id);
        if (state == null && constructor != null) {
            state = constructor.eval();
            getStates().put(id, state);
        }
        return (T)state;
    }

    public Object removeState(Object id) {
        return  getStates().remove(id);
    }

    protected void setStateUpdateNeeded(){
        Map<Object,State> m= getStates();
        for(State s:m.values()){
            s.setStateUpdateNeeded();
        }

    }

    protected void forEachState(VoidFunction<State> f){
        Map<Object,State> m= getStates();
        for(State s:m.values()){
            f.eval(s);
        }
    }




    @Override
    protected StatefulObject clone() throws CloneNotSupportedException {
        StatefulObject clone=(StatefulObject)super.clone();
        clone.states=null;
        Map<Object,State> states=getStates();
        Map<Object,State> clonedStates=clone.getStates();
        assert states!=clonedStates;        
        for(Object k:states.keySet()){
            State s=states.get(k);
            s=s.cloneStateFor(clone);
            if(s!=null)clonedStates.put(k,s);
        }
        return clone;
    }

   
}