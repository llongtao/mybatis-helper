package com.llt.mybatishelper.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    public static String readFileToString(String path) {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "UTF-8"));
            String thisLine;
            while ((thisLine = in.readLine()) != null) {
                stringBuilder.append(thisLine).append("\n");
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
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
     * @param path
     * @param listFileName
     */
    private static void getAllFileName(String path, ArrayList<String> listFileName) {
        File file = new File(path);
        File[] files = file.listFiles();
        String[] names = file.list();
        if (names != null) {
            String[] completNames = new String[names.length];
            for (int i = 0; i < names.length; i++) {
                completNames[i] = path +"\\"+ names[i];
            }
            listFileName.addAll(Arrays.asList(completNames));
        }
        for (File a : files) {
            if (a.isDirectory()) {
                //如果文件夹下有子文件夹，获取子文件夹下的所有文件全路径。
                getAllFileName(a.getAbsolutePath() + "\\", listFileName);
            }
        }
    }

}
