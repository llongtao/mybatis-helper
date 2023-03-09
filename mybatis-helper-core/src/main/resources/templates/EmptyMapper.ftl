package ${entity.mapperPackage};

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import ${entity.entityClassName};
import ${entity.baseMapperClassName};


/**
 * @author MybatisHelper ${now}
 */
@Mapper
public interface ${entity.entityName}Mapper extends ${entity.baseMapperName} {

    //更新时这个类不会被覆盖

}
