package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.CustomerAssistantTypeDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 客服问答类型
 *
 * @author liuhao@yimaokeji.com
 * @date 2018/5/14
 */
@Table(name = "t_customer_assistant_type")
@Data
public class CustomerAssistantType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String typeName;//类型名称
    private Integer typeCode;//类型code
    private Boolean deleteFlag;//是否删除
    private Integer terminal;//展示端
    private Integer sorts;//排序


    public CustomerAssistantType() {
    }

    /**
     * 用业务对象CustomerAssistantTypeDTO初始化数据库对象CustomerAssistantType。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public CustomerAssistantType(CustomerAssistantTypeDTO dto) {
        this.id = dto.getId();
        this.typeName = dto.getTypeName();
        this.typeCode = dto.getTypeCode();
        this.deleteFlag = dto.getDeleteFlag();
        this.terminal = dto.getTerminal();
        this.sorts = dto.getSorts();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象CustomerAssistantTypeDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(CustomerAssistantTypeDTO dto) {
        dto.setId(this.id);
        dto.setTypeName(this.typeName);
        dto.setTypeCode(this.typeCode);
        dto.setDeleteFlag(this.deleteFlag);
        dto.setTerminal(this.terminal);
        dto.setSorts(this.sorts);
    }
}
