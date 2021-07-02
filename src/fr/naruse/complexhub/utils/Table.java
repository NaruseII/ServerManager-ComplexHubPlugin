package fr.naruse.complexhub.utils;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class Table<K, V, E> extends HashMap<K, V> {

    private HashMap<K, E> map = Maps.newHashMap();

    @Override
    public V put(K key, V value) {
        return value;
    }

    public V put(K key, V value, E third) {
        map.put(key, third);
        return super.put(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        map.remove(key);
        return super.remove(key, value);
    }

    public E getThird(K key){
        return map.get(key);
    }

}
