package com.llt.mybatishelper.core.log;

import com.llt.mybatishelper.core.model.Log;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResultLog {
    private static List<Log>  logList = new ArrayList<>();

    public static void clear(){
        logList=new ArrayList<>();
    }

    public static void info(String msg){
        logList.add(new Log(LocalDateTime.now(), Log.Level.INFO,msg));
    }
    public static void warn(String msg){
        logList.add(new Log(LocalDateTime.now(), Log.Level.WARN,msg));
    }
    public static void error(String msg){
        logList.add(new Log(LocalDateTime.now(), Log.Level.ERROR,msg));
    }

    public static List<Log> getLogList() {
        return logList;
    }
}
