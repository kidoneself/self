package com.yimao.cloud.pojo.query.station;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 解决远程调用直接传List数据量大的问题，用对象接收
 */
@Data
public class ExclusiveQuery implements Serializable {

    private static final long serialVersionUID = -3133651248655434576L;

    private List<Integer> engineerIds;
}
