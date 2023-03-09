package ${entity.baseMapperPackage};

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import ${entity.entityClassName};



/**
* @author MybatisHelper ${now}
*/
@Mapper
public interface ${entity.baseMapperName} {

    /**
    * 插入
    * @param entity 需要插入的实体
    * @return 修改行数
    */
    int insert(${entity.className} entity);

    /**
    * 批量插入
    * @param list 需要插入的实体列表
    * @return 修改行数
    */
    int insertList(List<${entity.className}> list);

    /**
    * 更新
    * @param entity 需要更新的实体
    * @return 修改行数
    */
    int update(${entity.className} entity);

    /**
    * 批量更新
    * @param list 需要更新的实体列表
    * @return 修改行数
    */
    int updateList(List<${entity.className}> list);

     /**
     * 修改有值的列
     * @param entity 需要修改的实体
     * @return 修改行数
     */
     int updateSelective(${entity.className} entity);

     /**
     * 查询
     * @param entity 查询条件实体
     * @return 列表
     */
     List<${entity.className}> query(${entity.className} entity);

     /**
     * 根据id查询
     * @param id 主键
     * @return 实体
     */
     ${entity.className} queryById(${pkType} id);

     /**
     * 根据id删除
     * @param id 主键
     * @return 修改行数
     */
     int deleteById(${pkType} id);
}
