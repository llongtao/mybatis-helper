package top.aexp.mybatishelper.demo.entity;

import top.aexp.mybatishelper.demo.enums.Sex;

import java.math.BigDecimal;

/**
 *  auto表示开启自动生成
 * .auto
 * @author LILONGTAO
 * @date 2020-09-28
 */
public class User {

    /**
     *  主键与自增,非自增情况不需要添加incr
     * .key incr
     */
    private Integer id;

    //可以不写注释
    private String name;

    /**
     * .desc 邮箱
     * .len 128
     */
    private String email;

    /**
     *  自定义typeHandler
     * .jdbcType VARCHAR
     * .len 1
     * .typeHandler com.llt.mybatishelper.demo.typehanlder.SexTypeHandler
     */
    private Sex sex;

    /**
     *  指定长度
     * .len 19,2
     *
     */
    private BigDecimal money;

    //非基本类型无指定typeHandler不生成
    private Home home;

    /**
     *  忽略
     * .ignore
     */
    private String password;

}
