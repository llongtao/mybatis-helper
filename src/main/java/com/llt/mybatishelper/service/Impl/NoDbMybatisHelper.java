package com.llt.mybatishelper.service.Impl;

import com.llt.mybatishelper.model.EntityModel;
import com.llt.mybatishelper.service.BaseMybatisHelper;

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
