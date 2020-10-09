package top.aexp.mybatishelper.core.model;

import top.aexp.mybatishelper.core.log.ResultLog;
import lombok.Data;

import java.util.List;

@Data
public class BuildResult {
    private boolean succeed;
    private Exception e;
    private int total;
    private List<Log> logs;

    public static BuildResult succeed(int i) {
        BuildResult buildResult = new BuildResult();
        buildResult.succeed = true;
        buildResult.total = i;
        buildResult.logs = ResultLog.getLogList();
        ResultLog.clear();
        return buildResult;
    }
    public  static BuildResult error(Exception e) {
        BuildResult buildResult = new BuildResult();
        buildResult.total = 0;
        buildResult.e=e;
        buildResult.logs = ResultLog.getLogList();
        ResultLog.clear();
        return buildResult;
    }
}
