# mybatis-helper

由javaBean 自动生成数据库表和Mapper映射的工具,对你的项目0入侵

目前仅实现mysql

## 使用方式

在你的实体类javadoc注释里加入 .auto 并在主键field上的javadoc注释里加入.key
```java
/**
 *.auto
 */
public class User{

/**
 *.key
 */
private Integer id;

private String name;

}
```

在项目resources\config.json里配置文件夹和数据库连接

运行Main.main

## 更多关键字
###class注释

- **.auto 必选** 包含该字段才会自动生成

- .tableName xxx 可选 自定义表名,若不自定义使用类名下划线形式

- .desc xxx 可选 自定义该表描述

###field注释
- .key 可选 表示该field为主键
- .column 可选 自定义列名,默认使用field名的下划线格式
- .jdbcType xxx 可选 自定义映射的jdbcType
- .len xx 可选 自定义长度
- .desc xxx 可选 自定义字段描述
- .notNull 可选 非空
- .default xxx 可选 默认值
- .ignore 可选 忽略该字段

###其他细节
- 当主键不存在时自动使用field名为id的列为主键
- 当主键只有一个且为整形时默认自增
- 不允许主键不存在
- 静态成员不会计入列
- 父类成员不会计入列,entity一般不会继承其他类
- mapper会在你制定的目录下创建base文件夹,并生成baseMapper,不影响现有mapper,现有mapper继承baseMapper即可(会自动继承)
- 若指定文件夹下可能存在与自动生成同名baseMapper慎重使用,会直接覆盖
- 因为会创建子目录所以mapper-location 请设置为允许子目录,例如"classpath\*:mappers/\*\*/\*.xml"
- 不允许不规范的javadoc注释,例如注释下有空行,或者多个javadoc注释




