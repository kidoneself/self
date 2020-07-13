package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;

public interface ExportRecordService {
    ExportRecordDTO save(String url, String title);

    void update(ExportRecordDTO recordDTO);

    ExportRecordDTO getById(Integer id);
}
