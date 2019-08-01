package com.llt.mybatishelper.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LILONGTAO
 */
public class FileUtils {

    public static String readFileToString(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String thisLine;
            while ((thisLine = in.readLine()) != null) {
                stringBuilder.append(thisLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }


    public static List<String> getAllFilePath(String path){
        ArrayList<String> listFileName = new ArrayList<>();
        getAllFileName(path,listFileName);
        return listFileName;
    }
    /**
     * 获取一个文件夹下的所有文件全路径
     *
     */
    private static void getAllFileName(String path, ArrayList<String> listFileName) {
        File file = new File(path);
        File[] files = file.listFiles();
        String[] names = file.list();
        if (names != null) {
            String[] completeNames = new String[names.length];
            for (int i = 0; i < names.length; i++) {
                completeNames[i] = path +"\\"+ names[i];
            }
            listFileName.addAll(Arrays.asList(completeNames));
        }
        if (files != null) {
            for (File a : files) {
                if (a.isDirectory()) {
                    //如果文件夹下有子文件夹，获取子文件夹下的所有文件全路径。
                    getAllFileName(a.getAbsolutePath() + "\\", listFileName);
                }
            }
        }

    }

}
