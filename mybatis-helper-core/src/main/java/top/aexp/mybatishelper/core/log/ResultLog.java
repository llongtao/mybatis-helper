package top.aexp.mybatishelper.core.log;

import top.aexp.mybatishelper.core.model.Log;
import top.aexp.mybatishelper.core.utils.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResultLog {
    private static List<Log> logList = new ArrayList<>();
    private static List<String> sqlList = new ArrayList<>();

    public static void clear() {
        logList = new ArrayList<>();
        sqlList = new ArrayList<>();
    }

    public static void info(String msg) {
        logList.add(new Log(LocalDateTime.now(), Log.Level.INFO, msg));
    }

    public static void warn(String msg) {
        logList.add(new Log(LocalDateTime.now(), Log.Level.WARN, msg));
    }

    public static void error(String msg) {
        logList.add(new Log(LocalDateTime.now(), Log.Level.ERROR, msg));
    }

    public static void sql(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return;
        }
        sqlList.add(sql);
        info("sql:"+sql);
    }

    public static List<Log> getLogList() {
        return logList;
    }

    public static List<String> getSqlList() {
        return sqlList;
    }

    public static List<String> pollSqlList() {
        List<String> old = sqlList;
        sqlList = new ArrayList<>();
        return old;
    }
}
