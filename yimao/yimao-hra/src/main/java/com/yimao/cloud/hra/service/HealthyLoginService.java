package com.yimao.cloud.hra.service;


import com.yimao.cloud.pojo.dto.user.UserDTO;

/**
 * @author Zhang Bo
 * @date 2018/10/31.
 */
public interface HealthyLoginService {
    UserDTO getUserInfoByCode(String code, String encryptedData, String iv, Integer sharerId);
}
