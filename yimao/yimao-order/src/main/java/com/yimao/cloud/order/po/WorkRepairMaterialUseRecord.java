package com.yimao.cloud.order.po;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "work_repair_material_use_record")
public class WorkRepairMaterialUseRecord {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer workRepairOrderId;

    private Integer materialId;

    private String materialName;

    private Integer materialCount;
    
    private String firstCategoryName;

    private String secondCategoryName;
}