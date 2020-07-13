package com.yimao.cloud.pojo.dto.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yimao.cloud.pojo.dto.user.UserChangeRecordDTO;
import com.yimao.cloud.pojo.dto.user.UserCompanyDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户变更纪录
 * @author yaoweijun
 *
 */

@ApiModel(description = "用户变更纪录列表DTO")
@Getter
@Setter
@ToString
public class UserChangeRecordListDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -8377033686732152040L;
	
	@ApiModelProperty(position = 1, value = "主要节点纪录")
	private List<UserChangeRecordDTO> mainPointChangeRecord; 
	@ApiModelProperty(position = 2, value = "注册纪录")
	private UserChangeRecordDTO registRecord;
	@ApiModelProperty(position = 3, value = "续费纪录")
	private List<UserChangeRecordDTO> renewRecord;
	@ApiModelProperty(position = 4, value = "升级纪录")
	private List<UserChangeRecordDTO> upgradeRecord;
	@ApiModelProperty(position = 5, value = "转让纪录")
	private List<UserChangeRecordDTO> transferRecord;
	@ApiModelProperty(position = 6, value = "其他变更纪录")
	private List<UserChangeRecordDTO> otherChangeRecord;
	
	public UserChangeRecordListDTO() {

	}

	public UserChangeRecordListDTO(List<UserChangeRecordDTO> mainPointChangeRecord,
			UserChangeRecordDTO registRecord,
			List<UserChangeRecordDTO> renewRecord, 
			List<UserChangeRecordDTO> upgradeRecord,
			List<UserChangeRecordDTO> transferRecord, 
			List<UserChangeRecordDTO> otherChangeRecord) {

		this.mainPointChangeRecord = mainPointChangeRecord;
		this.registRecord = registRecord;
		this.renewRecord = renewRecord;
		this.upgradeRecord = upgradeRecord;
		this.transferRecord = transferRecord;
		this.otherChangeRecord = otherChangeRecord;
	}
	
	

}
