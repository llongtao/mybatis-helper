package top.aexp.mybatishelper.core.utils;

import java.util.Collection;

/**
 * @author LILONGTAO
 * @date 2020-04-22
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection collection){
        return collection==null||collection.isEmpty();
    }


    public static void main(String[] args) {
        System.out.println(String.format("%010d",12));
    }
}
