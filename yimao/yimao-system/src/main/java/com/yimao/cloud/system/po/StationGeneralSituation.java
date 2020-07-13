package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.StationGeneralSituationDTO;
import lombok.Data;

/**
 * @author Lizhqiang
 * @date 2019/2/19
 */
@Data
public class StationGeneralSituation {

    private Integer stationCompanyNum;

    private Integer stationNum;

    private Integer contractNum;

    private Integer openNum;


    public StationGeneralSituation() {
    }

    /**
     * 用业务对象StationMessageDTO初始化数据库对象StationMessage。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StationGeneralSituation(StationGeneralSituationDTO dto) {
        this.stationCompanyNum = dto.getStationCompanyNum();
        this.stationNum = dto.getStationNum();
        this.contractNum = dto.getContractNum();
        this.openNum = dto.getOpenNum();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StationMessageDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StationGeneralSituationDTO dto) {
        dto.setStationCompanyNum(this.stationCompanyNum);
        dto.setStationNum(this.stationNum);
        dto.setContractNum(this.contractNum);
        dto.setOpenNum(this.openNum);
    }
}
