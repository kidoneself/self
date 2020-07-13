package com.yimao.cloud.base.entity;

import java.io.File;
import java.io.Serializable;

/**
 * @author Zhang Bo
 * @date 2018/5/16.
 */
public class FileItem implements Serializable {

    private static final long serialVersionUID = 5805962698187671984L;

    private String fieldName;
    private File file;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
