package com.yimao.cloud.hra.service;


import com.yimao.cloud.hra.msg.HraResult;

import javax.servlet.http.Part;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2017/12/6.
 */
public interface HraAesService {

    HraResult serviceStation(String aesContent) throws Exception;

    HraResult deviceTest(String aesContent) throws Exception;

    HraResult getCustomerByTicketNo(String aesContent) throws Exception;

    HraResult ticketBindCustomer(String aesContent) throws Exception;

    HraResult ticketValidate(String aesContent) throws Exception;

    HraResult ticketMarkupUsed(String aesContent) throws Exception;

    HraResult reportUpload(String aesContent, Map<String, Part> partMap) throws Exception;
}
