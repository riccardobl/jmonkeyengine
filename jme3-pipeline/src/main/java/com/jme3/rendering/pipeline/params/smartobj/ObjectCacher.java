package com.jme3.rendering.pipeline.params.smartobj;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CacheQueue
 */
public class ObjectCacher<K, V> {

    protected Map<CacheEntry<K>, V> objCache = new ConcurrentHashMap<CacheEntry<K>, V>();
    protected ReferenceQueue<K> objCacheRefQ = new ReferenceQueue<K>();

    static class CacheEntry<K> {
        final WeakReference<K> ref;

        public CacheEntry(K obj, ReferenceQueue<K> refQ) {
            ref = new WeakReference<K>(obj, refQ);
        }

        public boolean isAlive() {
            return ref.get() != null;
        }

        Integer hashCode = null;

        @Override
        public int hashCode() {
            if (hashCode != null)
                return hashCode;
            Object obj = ref.get();
            if (obj == null)
                return hashCode = System.identityHashCode(this);
            return hashCode = System.identityHashCode(obj);
        }

        @Override
        public boolean equals(Object b) {
            if (b == null||!isAlive())
                return false;

            Object obj = ref.get();

            if (obj != null && b instanceof CacheEntry) {
                CacheEntry<K> bc = (CacheEntry) b;
                if(!bc.isAlive())return false;
                Object bobj = bc.ref.get();
                if (bobj != null && bobj.equals(obj))
                    return true;
            }

            return b == this || (obj != null && obj.equals(b));

        }

    }

    public ObjectCacher(String name) {
        Thread cacheCleaner = new Thread(() -> {
            while (true) {

                try {
                    objCacheRefQ.remove();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int n = 0;
                while (objCacheRefQ.poll() != null) {
                    n++;
                    if (n > 1000)
                        break;
                }
                Iterator<Entry<CacheEntry<K>, V>> objCache_i = objCache.entrySet().iterator();
                while (objCache_i.hasNext()) {
                    if (!objCache_i.next().getKey().isAlive()) {
                        objCache_i.remove();
                    }
                }
            }
        });
        cacheCleaner.setName(name + " :: Cache Cleaner");
        cacheCleaner.setDaemon(true);
        cacheCleaner.start();

    }

    public V get(K key) {
        CacheEntry<K> ref = new CacheEntry<K>(key, objCacheRefQ);
        return objCache.get(ref);
    }

    public void put(K key, V obj) {
        CacheEntry<K> ref = new CacheEntry<K>(key, objCacheRefQ);
        objCache.put(ref, obj);
    }

    public int size(){
        return objCache.size();
    }



}