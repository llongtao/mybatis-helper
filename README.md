# mybatis-helper

[![License](https://img.shields.io/badge/license-GPL-blue)](https://github.com/llongtao/mybatis-helper/blob/master/LICENSE)


#### 项目介绍

由javaBean 自动生成数据库表和Mapper映射的工具,对你的项目0入侵
目前实现 mysql/pgsql

#### idea插件版
https://github.com/llongtao/mybatis-pro-max-plug

#### 软件架构

JDK 1.8
java swing
javaparser
dom4j


#### 使用方式

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


运行Main.main

按照ui界面填写,点击生成即可
![ui界面](https://gz.bcebos.com/v1/longtao/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20200423151122.png)


注:
1.若不生成表结构可不填数据库信息

2.若实体类未继承基类可不填基类信息

#### 更多关键字
- class注释

  - **.auto 必选** 包含该字段才会自动生成
  - .tableName xxx 可选 自定义表名,默认使用类名下划线形式
  - .entityName xxx 可选 自定义实体名,默认使用类名
  - .keyType xxx 可选 当主键在基类时指定基类主键类型,默认使用基类主键类型
  - .desc xxx 可选 自定义该表描述

- field注释
  - .key 可选 表示该field为主键
  - .column 可选 自定义列名,默认使用field名的下划线格式
  - .jdbcType xxx 可选 自定义映射的jdbcType
  - .len xx 可选 自定义长度
  - .desc xxx 可选 自定义字段描述
  - .notNull 可选 非空
  - .default xxx 可选 默认值
  - .ignore 可选 忽略该字段
  - .typeHandler xx 可选 填写全类名

- 其他细节
  - 当主键不存在时自动使用field名为id的列为主键
  - 当主键只有一个且为整形时默认自增
  - 不允许主键不存在
  - 枚举类必须指定转换type,如String int,或直接指定jdbcType VARCHAR,否则不生成
  - 静态成员不会计入列
  - 父类成员不会计入列,需要在基类字段填写
  - mapper会在你指定的目录下创建base文件夹,并生成baseMapper,不影响现有mapper,现有mapper继承baseMapper即可(会自动继承)
  - 若指定文件夹下可能存在与自动生成同名baseMapper慎重使用,会直接覆盖
  - 因为会创建子目录所以mapper-location 请设置为允许子目录,例如"classpath\*:mappers/\*\*/\*.xml"
  - 不允许不规范的javadoc注释,例如注释下有空行,或者多个javadoc注释

修改记录
 - v1.1.0
   - 支持自定义表名与实体名
   - 长度修改为字符串
   - 分离view与core
 - v1.2.0
   - 支持pgsql 构建xml mapper
   - 支持typeHandler
   - 支持勾选配置
   - 删除enum注释,改用type或JDBCType+typeHandler表示
 - v1.3.0
   - 优化表结构修改,现在每次生成会删除已删除的列,并修改已存在的列
   - 添加.define注释,支持自定义列定义,优先级最高可覆盖JDBC_TYPE\LEN\NO_NULL\DEFAULT\DESC注释
   - 修改部分JDBCType到数据库类型的映射
   - .key注释修改为可追加incr属性,表示数据库自增
   - core执行api允许自定义EntityBuilder,MapperBuilder,XmlBuilder
   - 添加sql执行日志,默认保存在xml目录下sql文件夹
   - 添加demo演示模块
 - v2.0.0
   - UI由javaFX替换为java swing 以兼容arm架构机器
   - 完善pgsql建表操作,目前已支持pgsql全部功能
   - 多主键支持修改为只取单主键,存在多个主键仅支持第一个(多主键场景很少大部分仅作为中间表)
   - baseMapper 整合为一个,统一提供增删改查接口(旧项目切换时请谨慎操作,因为修改了基本的api)




