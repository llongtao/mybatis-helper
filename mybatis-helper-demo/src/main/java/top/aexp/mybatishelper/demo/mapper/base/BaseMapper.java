package top.aexp.mybatishelper.demo.mapper.base;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author MybatisHelper 2020-11-18 18:17:56
 */
@Mapper
public interface BaseMapper<T,PK> {

    /**
     * 插入
     * @param entity 需要插入的实体
     * @return 修改行数
     */
    int insert(T entity);

    /**
     * 批量插入
     * @param list 需要插入的实体列表
     * @return 修改行数
     */
    int insertList(List<T> list);

    /**
     * 更新
     * @param entity 需要更新的实体
     * @return 修改行数
     */
    int update(T entity);

    /**
     * 批量更新
     * @param list 需要更新的实体列表
     * @return 修改行数
     */
    int updateList(List<T> list);

    /**
     * 修改有值的列
     * @param entity 需要修改的实体
     * @return 修改行数
     */
    int updateSelective(T entity);

    /**
     * 查询
     * @param entity 查询条件实体
     * @return 列表
     */
    List<T> query(T entity);

    /**
     * 根据id查询
     * @param id 主键
     * @return 实体
     */
    T queryByPrimaryKey(PK id);

    /**
     * 根据id删除
     * @param id 主键
     * @return 修改行数
     */
    int deleteByPrimaryKey(PK id);
}
