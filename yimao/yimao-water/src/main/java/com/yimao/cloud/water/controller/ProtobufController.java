package com.yimao.cloud.water.controller;

import com.google.protobuf.ByteString;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.water.proto.BaiduProto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/1/28 15:30
 * @Version 1.0
 */
@RestController
@Api(tags = "ProtobufController")
@Slf4j
public class ProtobufController {

    @Resource
    private RestTemplate restTemplate;

    @RequestMapping(value = "/protobuf", method = RequestMethod.POST)
    @ApiOperation(value = "测试protobuf", notes = "测试protobuf")
    public Object save() {
        BaiduProto.TsApiRequest.Builder tsApiRequest = BaiduProto.TsApiRequest.newBuilder();
        //1-基础参数
        //1-1广告请求ID
        tsApiRequest.setRequestId(ByteString.copyFromUtf8(UUIDUtil.longuuid32()));
        //1-2接口版本，6.4.0
        tsApiRequest.setApiVersion(BaiduProto.Version.newBuilder().setMajor(6).setMinor(4).setMicro(0).build());
        //2-媒体参数
        //2-1资源方ID
        tsApiRequest.setAppId(ByteString.copyFromUtf8("b45f78ff"));//标识资源方，平台生成
        //3-广告位参数
        BaiduProto.SlotInfo.Builder slotInfo = BaiduProto.SlotInfo.newBuilder();
        //3-1广告位ID
        slotInfo.setAdslotId(ByteString.copyFromUtf8("4744335"));//广告位id，平台生成
        tsApiRequest.setSlot(slotInfo.build());
        //4-设备参数
        BaiduProto.Device.Builder device = BaiduProto.Device.newBuilder();
        BaiduProto.UdId.Builder udId = BaiduProto.UdId.newBuilder();
        //4-1设备ID类型
        udId.setIdType(BaiduProto.UdIdType.MEDIA_ID);
        //4-2设备ID
        // udIdBuilder.setId(ByteString.copyFromUtf8("1c:7e:5f:c8:f3:61"));
        udId.setId(ByteString.copyFromUtf8("0010010028"));
        device.setUdid(udId.build());
        //4-3操作系统
        device.setOsType(BaiduProto.OsType.ANDROID);
        //4-4操作系统版本
        device.setOsVersion(BaiduProto.Version.newBuilder().setMajor(6).build());
        //4-5设备厂商
        device.setVendor(ByteString.copyFromUtf8("Vendor"));
        //4-6机型
        device.setModel(ByteString.copyFromUtf8("Model"));
        BaiduProto.Size.Builder sizeBuilder = BaiduProto.Size.newBuilder();
        sizeBuilder.setWidth(1920);
        sizeBuilder.setHeight(1080);
        //4-7设备屏幕尺寸
        device.setScreenSize(sizeBuilder.build());
        tsApiRequest.setDevice(device.build());
        //5-移动网络参数
        BaiduProto.Network.Builder network = BaiduProto.Network.newBuilder();
        //5-1IPv4地址
        network.setIpv4(ByteString.copyFromUtf8("223.64.152.69"));
        //5-2网络类型
        network.setConnectionType(BaiduProto.Network.ConnectionType.UNKNOWN_NETWORK);
        //5-3运营商ID
        network.setOperatorType(BaiduProto.Network.OperatorType.ISP_UNKNOWN);
        tsApiRequest.setNetwork(network.build());
        //6-连接 wifi ap 参数（注：ap 参数选填；如选择填写，则以下参数均为必填）
        //7-探针参数：当前时刻探测到的全部设备（注：探针参数选填；如选择填写，则至少填写 1 个设备）
        //8-GPS 参数（注：GPS 参数选填；如选择填写，则以下参数均为必填）
        BaiduProto.Gps.Builder gpsBuilder = BaiduProto.Gps.newBuilder();
        //8-1GPS坐标类型
        gpsBuilder.setCoordinateType(BaiduProto.Gps.CoordinateType.BD09);
        //8-2GPS坐标经度
        gpsBuilder.setLongitude(41.3411606788D);
        //8-3GPS坐标纬度
        gpsBuilder.setLatitude(66.8025170953D);
        tsApiRequest.setGps(gpsBuilder.build());
        //9-用户参数
        //10-扫描 wifi ap 参数（注：ap 参数选填；如选择填写，则以下参数均为必填）

        BaiduProto.TsApiRequest req = tsApiRequest.build();
        System.out.println("============================");
        System.out.println(req);
        System.out.println("============================");

        String url = "http://jpaccess.baidu.com/api_6";
        try {
            byte[] resp = restTemplate.postForObject(url, req.toByteArray(), byte[].class);

            if (resp != null) {
                BaiduProto.TsApiResponse response = BaiduProto.TsApiResponse.parseFrom(resp);
                System.out.println(response);
                System.out.println("============================");
                return ResponseEntity.ok(response.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("操作失败");
    }

}
