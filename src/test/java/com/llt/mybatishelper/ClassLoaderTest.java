package com.llt.mybatishelper;


import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * @author LILONGTAO
 * @date 2019-12-13
 */
public class ClassLoaderTest{

    public static void main(String[] args) {
        String javaAbsolutePath = "C:\\Users\\DELL\\IdeaProjects\\mybatis-helper\\src\\test\\java\\com\\llt\\mybatishelper\\ClassLoaderTest.java";
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int status = compiler.run(null, null, null, "-encoding", "UTF-8", "-classpath", javaAbsolutePath, javaAbsolutePath);
        if(status!=0){
            System.out.println("没有编译成功！");





        ClassLoader classLoader = new MyClassLoader();
        try {

            Class<?> classLoaderTest = classLoader.loadClass("ClassLoaderTest");
            System.out.println(classLoaderTest);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
}
