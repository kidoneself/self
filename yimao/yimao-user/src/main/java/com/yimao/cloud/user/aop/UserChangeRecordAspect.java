/*
package com.yimao.cloud.user.aop;

import com.yimao.cloud.base.context.BaseContextHandler;
import com.yimao.cloud.base.enums.DistributorOrderType;
import com.yimao.cloud.base.enums.UserChangeRecordEnum;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.UserChangeRecordDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.user.aop.annotation.UserChangeRecordLog;
import com.yimao.cloud.user.mapper.DistributorMapper;
import com.yimao.cloud.user.mapper.DistributorOrderMapper;
import com.yimao.cloud.user.mapper.UserMapper;
import com.yimao.cloud.user.po.Distributor;
import com.yimao.cloud.user.po.DistributorOrder;
import com.yimao.cloud.user.service.UserChangeService;
import com.yimao.cloud.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;

*/
/**
 * 记录用户身份变化
 *
 * @author hhf
 * @date 2019/1/10
 *//*

@Aspect
@Component
@Slf4j
public class UserChangeRecordAspect {

    @Resource
    private DistributorOrderMapper distributorOrderMapper;
    @Resource
    private UserChangeService userChangeService;
    @Resource
    private DistributorMapper distributorMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;

    @Before("@annotation(userChangeRecordLog)")
    public void doBefore(JoinPoint joinPoint, UserChangeRecordLog userChangeRecordLog) {
        Integer origId = null;
        String updater = (String) joinPoint.getArgs()[0];//操作人
        Object arg = joinPoint.getArgs()[1];//操作对象
        if (joinPoint.getArgs().length == 3) {
            origId = (Integer) joinPoint.getArgs()[2];
        }
        UserChangeRecordDTO changeRecordDTO = new UserChangeRecordDTO();
        if (arg instanceof DistributorDTO) {
            Distributor distributor = new Distributor((DistributorDTO) arg);
            Integer userType = null;
            UserDTO user = userService.getBasicUserByMid(distributor.getId());
            if (null != user)
                userType = user.getUserType();
            if (null == origId) {
                setUserChangeRecord(updater, distributor, changeRecordDTO, userType, userChangeRecordLog, null);
            } else {
                Distributor oriDistributor = distributorMapper.selectByPrimaryKey(origId);
                setUserChangeRecord(updater, oriDistributor, changeRecordDTO, userType, userChangeRecordLog, distributor);
            }
        }

    }

    @AfterReturning(pointcut = "@annotation(userChangeRecordLog)", returning = "object")
    public void doAfterReturing(Object object, UserChangeRecordLog userChangeRecordLog) {
        if (object instanceof Distributor) {

            Integer orderType = null;
            Distributor distributor = ((Distributor) object);
            Integer terminal = distributor.getTerminal();
            // app端 生成订单
            boolean sign = (terminal != null && terminal != 3);

            UserChangeRecordDTO changeRecordDTO = (UserChangeRecordDTO) BaseContextHandler.get("userChangeRecord");

            UserChangeRecordEnum userChangeRecord = UserChangeRecordEnum.getUserChangeRecord(changeRecordDTO.getType());
            if (null != userChangeRecord) {
                switch (userChangeRecord) {
                    case REGISTERED_EVENT:
                        orderType = DistributorOrderType.REGISTER.value;
                        break;
                    case RENEWAL_EVENT:
                        orderType = DistributorOrderType.UPGRADE.value;
                        break;
                    case UPGRADE_EVENT:
                        orderType = DistributorOrderType.RENEW.value;
                        break;
                }
            }
            if (null != orderType && sign) {
                Example example = new Example(Distributor.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("orderType", orderType);
                criteria.andEqualTo("distributorId", distributor.getId());
                DistributorOrder order = distributorOrderMapper.selectOneByExample(example);
                if (null != order) {
                    changeRecordDTO.setOrderId(order.getOrderId());
                }
            }
            userChangeService.saveCommon(changeRecordDTO);
        }

    }

    private void setUserChangeRecord(String updater, Distributor distributor, UserChangeRecordDTO changeRecordDTO, Integer userType, UserChangeRecordLog userChangeRecordLog, Distributor destDistributor) {
        changeRecordDTO.setCreator(updater);
        UserDTO user = userService.getBasicUserByMid(distributor.getId());
        Integer userId = null;
        if (null != user){
            userId = user.getId();
        }
        changeRecordDTO.setOrigUserId(userId);
        changeRecordDTO.setOrigDistributorId(distributor.getId());
        changeRecordDTO.setOrigAccount(distributor.getUserName());
        changeRecordDTO.setOrigPhone(distributor.getPhone());
        changeRecordDTO.setOrigUserType(userType);
        changeRecordDTO.setType(userChangeRecordLog.changeType().value);
        changeRecordDTO.setTime(new Date());
        changeRecordDTO.setTerminal(distributor.getTerminal());
        if (destDistributor != null) {
            UserDTO userByMid = userService.getBasicUserByMid(destDistributor.getId());
            Integer destUserId = null;
            if (null != userByMid){
                destUserId = userByMid.getId();
            }
            changeRecordDTO.setDestUserId(destUserId);
            changeRecordDTO.setDestDistributorId(destDistributor.getId());
            changeRecordDTO.setDestAccount(destDistributor.getUserName());
            changeRecordDTO.setDestPhone(destDistributor.getPhone());
        }
        BaseContextHandler.set("userChangeRecord", changeRecordDTO);
    }

}
*/
