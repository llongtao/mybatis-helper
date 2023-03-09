<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${entity.baseMapperClassName}">
    <resultMap id="BaseResultMap" type="${entity.entityClassName}">
    <#list entity.primaryKeyList as primaryKey>
        <id column="${primaryKey.columnName}" jdbcType="${primaryKey.jdbcType}" property="${primaryKey.name}"/>
    </#list>
    <#list entity.columnList as column>
        <result column="${column.columnName}" jdbcType="${column.jdbcType}" property="${column.name}" <#if column.typeHandler??  >typeHandler="${column.typeHandler}"</#if> />
    </#list>
    </resultMap>
    <sql id="BaseColumn">
    <#list entity.primaryKeyList as column >
        ${split}${column.columnName}${split},
    </#list>
    <#list entity.columnList as column >
        ${split}${column.columnName}${split}<#if column_has_next>,</#if>
    </#list>
    </sql>
    <sql id="BaseNoPkColumn">
    <#list entity.columnList as column >
        ${split}${column.columnName}${split}<#if column_has_next>,</#if>
    </#list>
    </sql>
    <select id="queryById" resultMap="BaseResultMap">
        select
        <include refid="BaseColumn"/>
        from
        ${split}${entity.tableName}${split}
        <where>
        <#list entity.primaryKeyList as column >
            and ${split}${column.columnName}${split} = ${r'#'}{${column.name}}
        </#list>
        </where>
    </select>
    <delete id="deleteById">
        delete
        from
        ${split}${entity.tableName}${split}
        <where>
        <#list entity.primaryKeyList as column >
            and ${split}${column.columnName}${split} = ${r'#'}{${column.name}}
        </#list>
        </where>
    </delete>
    <insert id="insert" parameterType="com.gree.sparepart.basis.entity.DemoEntity">
        insert into
        ${split}${entity.tableName}${split}
        <trim prefix="(" suffix=")" suffixOverrides=",">
        <#list entity.primaryKeyList as column >
            <if test="${column.name}!=null">
                ${split}${column.columnName}${split},
            </if>
        </#list>
        <#list entity.columnList as column >
            <if test="${column.name}!=null">
                ${split}${column.columnName}${split},
            </if>
        </#list>
        </trim>
        values
        <trim prefix="(" suffix=")" suffixOverrides=",">
        <#list entity.primaryKeyList as column >
            <if test="${column.name}!=null">
                ${r'#'}{${column.name}},
            </if>
        </#list>
        <#list entity.columnList as column >
            <if test="${column.name}!=null">
                ${r'#'}{${column.name}},
            </if>
        </#list>
        </trim>
    </insert>
    <update id="updateSelective" parameterType="com.gree.sparepart.basis.entity.DemoEntity">
        update
        ${split}${entity.tableName}${split}
        <set>
        <#list entity.columnList as column >
            <if test="${column.name}!=null">
                ${split}${column.columnName}${split} = ${r'#'}{${column.name}},
            </if>
        </#list>
        </set>
        <where>
        <#list entity.primaryKeyList as column >
            and ${split}${column.columnName}${split} = ${r'#'}{${column.name}}
        </#list>
        </where>

    </update>
    <update id="update" parameterType="com.gree.sparepart.basis.entity.DemoEntity">
        update
        ${split}${entity.tableName}${split}
        <set>
        <#list entity.columnList as column >
            ${split}${column.columnName}${split} = ${r'#'}{${column.name},jdbcType = ${column.jdbcType}},
        </#list>
        </set>
        <where>
        <#list entity.primaryKeyList as column >
            and ${split}${column.columnName}${split} = ${r'#'}{${column.name}}
        </#list>
        </where>
    </update>
    <select id="query" parameterType="com.gree.sparepart.basis.entity.DemoEntity" resultMap="BaseResultMap">
        select
        <include refid="BaseColumn"/>
        from
        ${split}${entity.tableName}${split}
        <where>
        <#list entity.primaryKeyList as column >
            <if test="${column.name}!=null">
                ${split}${column.columnName}${split} = ${r'#'}{${column.name}},
            </if>
        </#list>
        <#list entity.columnList as column >
            <if test="${column.name}!=null">
                ${split}${column.columnName}${split} = ${r'#'}{${column.name}},
            </if>
        </#list>
        </where>
    </select>
    <insert id="insertList" parameterType="com.gree.sparepart.basis.entity.DemoEntity">
        <if test="list!=null and list.size&gt;0">
            insert into
            ${split}${entity.tableName}${split}
            <trim prefix="(" suffix=")" suffixOverrides=",">
                <include refid="BaseColumn"/>
            </trim>
            values
            <foreach collection="list" item="item" separator=",">
                (
            <#list entity.primaryKeyList as column >
                ${r'#'}{${column.name},jdbcType = ${column.jdbcType}},
            </#list>
            <#list entity.columnList as column >
                 ${r'#'}{${column.name},jdbcType = ${column.jdbcType}}<#if column_has_next>,</#if>
            </#list>
                )
            </foreach>
        </if>
        <if test="list==null or list.size==0">
            select 0 from ${split}${entity.tableName}${split}
        </if>
    </insert>
    <update id="updateList" parameterType="com.gree.sparepart.basis.entity.DemoEntity">
        <if test="list!=null and list.size&gt;0">
            <foreach collection="list" item="item" separator=";">
                update
                ${split}${entity.tableName}${split}
                <set>
                <#list entity.columnList as column >
                    ${split}${column.columnName}${split} = ${r'#'}{${column.name},jdbcType = ${column.jdbcType}},
                </#list>
                </set>
                <where>
                <#list entity.primaryKeyList as column >
                    and ${split}${column.columnName}${split} = ${r'#'}{${column.name}}
                </#list>
                </where>
            </foreach>
        </if>
        <if test="list==null or list.size==0">
            select 0 from ${split}${entity.tableName}${split}
        </if>
    </update>
</mapper>
