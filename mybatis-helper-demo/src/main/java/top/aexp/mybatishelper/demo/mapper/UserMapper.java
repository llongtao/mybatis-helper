package top.aexp.mybatishelper.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.aexp.mybatishelper.demo.mapper.base.BaseMapper;
import top.aexp.mybatishelper.demo.entity.User;

/**
 * @author MybatisHelper 2020-11-18 18:03:14
 */
@Mapper
public interface UserMapper extends BaseMapper<User, Integer> {
    // 自己的查询请写在这里,更新时这个类不会被覆盖
}
