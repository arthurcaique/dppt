/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author arthur
 */
public class HashMapVectors extends HashMap<Object[], Object> {

    public boolean contains(Object[] anArray) {
        boolean contains = false;
        Set<Object[]> setArrays = this.keySet();
        for (Object[] setArray : setArrays) {
            if (Arrays.deepEquals(setArray, anArray)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    public Object get(Object[] anArray) {
        Object object = null;
        Set<Entry<Object[], Object>> entries = this.entrySet();
        for (Entry<Object[], Object> entry : entries) {
            Object[] array = entry.getKey();
            if (Arrays.deepEquals(array, anArray)) {
                object = entry.getValue();
                break;
            }
        }
        return object;
    }

    @Override
    public Object putIfAbsent(Object[] key, Object value) {
        Object oldValue = null;
        if (this.contains(key)) {
            oldValue = get(key);
        } else {
            this.put(key, value);
        }
        return oldValue;
    }

    @Override
    public Object replace(Object[] key, Object newValue){
        Object oldValue = null;
        if(this.contains(key)){
            oldValue = get(key);
            this.remove(key);
            this.put(key, newValue);
        }
        return oldValue;
    }
    
    public Object remove(Object[] key){
        Object oldValue = null;
        if(this.contains(key)){
            Iterator<Entry<Object[], Object>> iterator = this.entrySet().iterator();
            while(iterator.hasNext()){
                Entry<Object[], Object> next = iterator.next();
                if(Arrays.deepEquals(key, next.getKey())){
                    oldValue = next.getValue();
                    iterator.remove();
                    break;
                }
            }
        }
        return oldValue;
    }
}
