package com.llt.mybatishelper.core.service.impl;


import com.llt.mybatishelper.core.model.EntityModel;
import com.llt.mybatishelper.core.service.BaseMybatisHelper;

/**
 * @author LILONGTAO
 * @date 2019-08-01
 */
public class NoDbMybatisHelper extends BaseMybatisHelper {

    @Override
    protected void updateTable(EntityModel entityModel, String dataSourceUrl) {
        //不对数据库做改动
    }

}
