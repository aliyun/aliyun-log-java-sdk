package com.aliyun.openservices.log.http.utils;

import static com.aliyun.openservices.log.http.utils.CodingUtils.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * 大小写不敏感map，http header
 * */
public class CaseInsensitiveMap<V> implements Map<String, V>{

    private Map<String, V> wrappedMap;
    
    public CaseInsensitiveMap() {
        this(new HashMap<String, V>());
    }

    public CaseInsensitiveMap(Map<String, V> wrappedMap) {
        assertParameterNotNull(wrappedMap, "wrappedMap");

        this.wrappedMap = wrappedMap;
    }

    public int size() {
        return wrappedMap.size();
    }

    public boolean isEmpty() {
        return wrappedMap.isEmpty();
    }

    public boolean containsKey(Object key) {
        return wrappedMap.containsKey(key.toString().toLowerCase());
    }

    public boolean containsValue(Object value) {
        return wrappedMap.containsValue(value);
    }

    public V get(Object key) {
        return wrappedMap.get(key.toString().toLowerCase());
    }

    public V put(String key, V value) {
        return wrappedMap.put(key.toLowerCase(), value);
    }

    public V remove(Object key) {
        return wrappedMap.remove(key.toString().toLowerCase());
    }

    public void putAll(Map<? extends String, ? extends V> m) {
        for(java.util.Map.Entry<? extends String, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        wrappedMap.clear();
    }

    public Set<String> keySet() {
        return wrappedMap.keySet();
    }

    public Collection<V> values() {
        return wrappedMap.values();
    }

    public Set<java.util.Map.Entry<String, V>> entrySet() {
        return wrappedMap.entrySet();
    }

}
