package com.yimao.cloud.system.po;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "materials_adaptation_model")
public class MaterialsAdaptationModel {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer goodsMaterialsId;

    private String deviceModelName;

    private Integer productCategoryId;

}