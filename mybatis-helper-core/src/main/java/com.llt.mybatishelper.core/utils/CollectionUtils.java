package com.llt.mybatishelper.core.utils;

import java.util.Collection;

/**
 * @author LILONGTAO
 * @date 2020-04-22
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection collection){
        return collection==null||collection.isEmpty();
    }
}
