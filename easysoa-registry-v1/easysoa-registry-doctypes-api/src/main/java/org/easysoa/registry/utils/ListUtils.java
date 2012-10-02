package org.easysoa.registry.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    public static List<String> toStringList(Serializable[] array) {
        List<String> list = new ArrayList<String>();
        for (Serializable element : array) {
            list.add(element.toString());
        }
        return list;
    }
    
}
