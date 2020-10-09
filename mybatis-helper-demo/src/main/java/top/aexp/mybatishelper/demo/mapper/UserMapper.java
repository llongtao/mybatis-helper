package top.aexp.mybatishelper.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.aexp.mybatishelper.demo.mapper.base.BaseUserMapper;

/**
 * @author MybatisHelper 2020-09-28 14:42:45
 */
@Mapper
public interface UserMapper extends BaseUserMapper {
    // 自己的查询请写在这里,更新时这个类不会被覆盖
}
