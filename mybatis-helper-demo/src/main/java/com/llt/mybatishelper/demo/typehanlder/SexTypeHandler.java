package com.llt.mybatishelper.demo.typehanlder;

import com.llt.mybatishelper.demo.enums.Sex;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author LILONGTAO
 * @date 2020-09-28
 */
public class SexTypeHandler implements TypeHandler<Sex> {
    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, Sex sex, JdbcType jdbcType) throws SQLException {

    }

    @Override
    public Sex getResult(ResultSet resultSet, String s) throws SQLException {
        return null;
    }

    @Override
    public Sex getResult(ResultSet resultSet, int i) throws SQLException {
        return null;
    }

    @Override
    public Sex getResult(CallableStatement callableStatement, int i) throws SQLException {
        return null;
    }
}
