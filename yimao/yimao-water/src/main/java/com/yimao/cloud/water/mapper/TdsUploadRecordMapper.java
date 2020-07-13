package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.query.water.TdsUploadRecordQuery;
import com.yimao.cloud.pojo.vo.water.TdsUploadRecordVO;
import com.yimao.cloud.water.po.TdsUploadRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface TdsUploadRecordMapper extends Mapper<TdsUploadRecord> {
    Page<TdsUploadRecordVO> selectPage(TdsUploadRecordQuery query);

    TdsUploadRecordVO getDetailById(@Param("id") Integer id);
}
