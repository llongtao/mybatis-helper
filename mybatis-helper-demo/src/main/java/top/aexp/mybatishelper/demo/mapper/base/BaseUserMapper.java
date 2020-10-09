package top.aexp.mybatishelper.demo.mapper.base;

import java.lang.*;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import top.aexp.mybatishelper.demo.entity.User;

/**
 * @author MybatisHelper 2020-09-28 15:32:22
 */
@Mapper
public interface BaseUserMapper {

    /**
     * 插入
     * @param user 需要插入的实体
     * @return 修改行数
     */
    int insertUser(User user);

    /**
     * 批量插入
     * @param userList 需要插入的实体列表
     * @return 修改行数
     */
    int insertUserList(List<User> userList);

    /**
     * 更新
     * @param user 需要更新的实体
     * @return 修改行数
     */
    int updateUser(User user);

    /**
     * 批量更新
     * @param userList 需要更新的实体列表
     * @return 修改行数
     */
    int updateUserList(List<User> userList);

    /**
     * 修改有值的列
     * @param user 需要修改的实体
     * @return 修改行数
     */
    int updateSelective(User user);

    /**
     * 查询
     * @param user 查询条件实体
     * @return 列表
     */
    List<User> queryUser(User user);

    /**
     * 根据id查询
     * @param id 主键
     * @return 实体
     */
    User queryByPrimaryKey(Integer id);

    /**
     * 根据id删除
     * @param id 主键
     * @return 修改行数
     */
    int deleteByPrimaryKey(Integer id);
}
