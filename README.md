# mybatis-helper

由javaBean 自动生成数据库表和Mapper映射的工具,对你的项目0入侵

# 使用方式

在你的实体类javadoc注释里加入 .auto 并在主键field上的javadoc注释里加入.主键
```java
/**
 *.auto
 */
public class user{

/**
 *.主键
 */
private Integer id;

private String name;

}
```

在项目resources\config.json里配置文件夹和数据库连接

运行Main.main
