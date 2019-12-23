package com.llt.mybatishelper;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author LILONGTAO
 * @date 2019-12-13
 */
public class MyClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        String classPath =  name + ".class";

        try (InputStream in = new FileInputStream(classPath)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int i = 0;
            while ((i = in.read()) != -1) {
                out.write(i);
            }
            byte[] byteArray = out.toByteArray();
            return defineClass("",byteArray, 0, byteArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
