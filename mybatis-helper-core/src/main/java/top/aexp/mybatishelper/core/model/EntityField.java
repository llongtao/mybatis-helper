package top.aexp.mybatishelper.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.sql.JDBCType;
import java.util.Objects;

/**
 * @author LILONGTAO
 */
@Data
@AllArgsConstructor
public class EntityField {

    private String name;

    private String columnName;

    private String javaType;

    private String type;

    private JDBCType jdbcType;

    private String define;

    private Boolean incr;

    private String length;

    private String defaultValue;

    private Boolean nullable;

    private String description;

    private String typeHandler;

    public EntityField() {
    }

    public EntityField(EntityField item) {
        this.name = item.getName();
        this.columnName = item.getColumnName();
        this.type = item.getType();
        this.length = item.getLength();
        this.defaultValue = item.getDefaultValue();
        this.description = item.getDescription();
        this.incr = item.getIncr();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityField)) {
            return false;
        }
        EntityField that = (EntityField) o;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
