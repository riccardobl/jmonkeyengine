package com.jme3.rendering.pipeline;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.jme3.rendering.pipeline.params.smartobj.SmartObject;

/**
 * Pointers factory This is used to create and resolve pointers. Note: pointers
 * should not be manipulated directly unless first resolved with
 * SmartObject.from(pointer).get(..)
 * 
 * @author Riccardo Balbo
 */
public class PipelinePointerFactory implements PipelinePointerResolver {
    private  static final java.util.logging.Logger logger =  java.util.logging.Logger.getLogger( PipelinePointerFactory.class.getName());
    private final Map<Class, PipelinePointerConstructor> constructors = new HashMap<Class, PipelinePointerConstructor>();
    private final Map<Object, Object> globalStorage = new HashMap<Object, Object>();
    private final Map<PipelinePass, PassStorage> passStorage = new WeakHashMap<PipelinePass, PassStorage>();

    private static final class PassStorage {
        Map<Object, Object> outputStorage = new HashMap<Object, Object>();
        void reset(){
            outputStorage.clear();
        }
    }

    public <T> void setDefaultConstructor(Class<T> cl, PipelinePointerConstructor<T> f) {
        constructors.put(cl, f);
    }
    public <T> PipelinePointerConstructor<T> getDefaultConstructor(Class<T> cl) {
        return constructors.get(cl);
    }


    public <T> PointerBuilder<T> newPointer(Class<T> type) {
        return newPointer(type, null);
    }

    public <T> PointerBuilder<T> newPointer(Class<T> type, PipelinePointerConstructor<T> constructor) {
        return new PointerBuilder<T>(this, type, constructor);
    }

    public final class PointerBuilder<T> {
        SmartObject<T> sobj;
        T obj;

        PointerBuilder() {

        }

        public final class AbsolutePointerBuilder {
            AbsolutePointerBuilder() {

            }

            public T to(Object key) {
                sobj.setPointer(key);
                return obj;
            }
        }

        public final class RelativePointerBuilder {
            int dir = 0;

            RelativePointerBuilder() {

            }

            public T previous(Object key) {
                dir = -1;
                sobj.setRelativePointer(dir, key);
                return obj;
            }

            public T next(Object key) {
                dir = 1;
                sobj.setRelativePointer(dir, key);
                return obj;
            }

            public T previous(Object key, int skip) {
                dir = -skip;
                sobj.setRelativePointer(dir, key);
                return obj;
            }

        }

        protected PointerBuilder(PipelinePointerResolver res, Class<T> type, PipelinePointerConstructor<T> constructor) {
            try {
                // ArrayList<Constructor<T>> constr = new ArrayList<Constructor<T>>();
                // int nparams = 0;

                // for (Constructor<?> c : type.getConstructors()) {
                // int n = c.getParameterCount();
                // if(n<nparams){
                // constr.clear();
                // nparams=n;
                // }
                // if (constr == null || n == nparams) {
                // constr.add((Constructor<T>) c);
                // }
                // }

                // constr.sort((a,b)->{

                // };

                obj = newInstance(type);

            } catch (Exception e) {
                e.printStackTrace();
            }
            sobj = SmartObject.from(obj);
            sobj.setConstructor(constructor);
            sobj.setPointerResolver(res);
        }

        public AbsolutePointerBuilder abs() {
            return new AbsolutePointerBuilder();
        }

        public RelativePointerBuilder rel() {
            return new RelativePointerBuilder();
        }

    }

    private PassStorage getPassStorage(PipelinePass pass) {
        PassStorage st = passStorage.get(pass);
        if (st == null) {
            st = new PassStorage();
            passStorage.put(pass, st);
        }
        return st;
    }

    @Override
    public void reset(Pipeline pipeline, PipelinePass pass) {
        PassStorage st = getPassStorage(pass);
        st.reset();
    }

    @Override
    public <T> T resolve(Class type, Pipeline pipeline, PipelinePass pass, T ref, PipelinePointerConstructor<T> init) {
        SmartObject<T> sref = SmartObject.from(ref);
        if (!sref.isPointer())
            throw new RuntimeException("Cannot resolve a non pointer..");

        Map<Object, Object> storage = null;
        if (!sref.isRelativePointer()) {
            storage = globalStorage;
        } else {
            if (sref.getRelativePointerDir() > 0) {
                PassStorage st = getPassStorage(pass);
                storage = st.outputStorage;
            } else {
                int n = -sref.getRelativePointerDir();
                Pipeline pp = pipeline;
                for (int i = pass.getId() - 1; i >= 0; i--) {
                    PassStorage st = getPassStorage(pp.get(i));
                    if (st.outputStorage.containsKey(sref.getPointerAddr())) {
                        n--;
                        if (n == 0)
                            storage = st.outputStorage;
                    }
                }
                if (storage == null)
                    storage = globalStorage;
            }
        }
        T obj = (T) storage.get(sref.getPointerAddr());
        if (obj == null) {
            try {
                obj = (T) newInstance(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PipelinePointerConstructor<T> defaultC = constructors.get(type);
            if (defaultC != null)
                obj = defaultC.construct(pipeline, pass, obj);
            if (init != null)
                obj = init.construct(pipeline, pass, obj);
            storage.put(sref.getPointerAddr(), obj);
        }
        return obj;
    }

    private static Constructor<?> selectConstructor(Class type) {
        Constructor<?>[] constructors = type.getConstructors();
        Arrays.sort(constructors, (a, b) -> {
            if (a.isAccessible() && !b.isAccessible())
                return -1;
            else if (a.getParameterCount() < b.getParameterCount())
                return -1;
            else if (a.getParameterCount() == b.getParameterCount()) {
                int aNprim = 0;
                int bNprim = 0;
                for (int i = 0; i < 2; i++) {
                    for (Class t : (i == 0 ? a : b).getParameterTypes()) {
                        if (t.isPrimitive() || t == Boolean.class || t == Character.class || Number.class.isAssignableFrom(t)) {
                            if (i == 0)
                                aNprim++;
                            else
                                bNprim++;
                        }
                    }
                }
                System.out.println("A with " + aNprim + " numb B with " + bNprim + " numb");
                return bNprim - aNprim;
            }
            return 1;
        });
        constructors[0].setAccessible(true);
        if(logger.isLoggable(java.util.logging.Level.  FINE  ))logger.log(java.util.logging.Level.FINE,
            "Select constructor {0}" , constructors[0]
        );
        return constructors[0];

    }

    private static <T> T newInstance(Class type) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor<?> constr=selectConstructor(type);
            
        List<Object> params = new ArrayList<Object>();
        for (Class<?> t : constr.getParameterTypes()) {
            Object v = null;
            if (t == boolean.class || t == Boolean.class) {
                v = false;
            } else if (t == char.class || t == Character.class) {
                v = '0';
            } else if (t == byte.class || t == Byte.class) {
                v = (byte) 0;
            } else if (t == short.class || t == Short.class) {
                v = (short) 0;
            } else if (t == int.class || t == Integer.class) {
                v = 0;
            } else if (t == long.class || t == Long.class) {
                v = 0l;
            } else if (t == float.class || t == Float.class) {
                v = 0f;
            } else if (t == double.class || t == Double.class) {
                v = 0.;
            }
            params.add(v);
        }
        return (T) constr.newInstance(params.toArray());
    }

   
    

}