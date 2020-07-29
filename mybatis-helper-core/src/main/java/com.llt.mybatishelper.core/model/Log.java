package com.llt.mybatishelper.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Log {
    private LocalDateTime time;
    private Level level;
    private String msg;

    public enum Level{

        /**
         *
         */
        INFO,
        WARN,
        ERROR
    }
}
