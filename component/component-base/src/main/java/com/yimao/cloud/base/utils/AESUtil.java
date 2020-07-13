package com.yimao.cloud.base.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Zhang Bo
 * @date 2017/12/10.
 */
public class AESUtil {

    public static final String ENCODE_RULES = "SCNCcTJtdmoldk07dUVXMw==";

    /**
     * 加密
     *
     * @param content 明文
     * @return
     */
    public static String AESEncode(String content) {
        try {
//            content = contentAdd(content);
            Base64 base64 = new Base64();
            byte[] raw = base64.decode(ENCODE_RULES);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(content.getBytes("utf-8"));

            return base64.encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
//            return parseByte2HexStr(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     *
     * @param content 密文
     * @return
     */
    public static String AESDncode(String content) {
        try {
            content = content.replaceAll(" ", "+");
            Base64 base64 = new Base64();
            byte[] raw = base64.decode(ENCODE_RULES);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted = base64.decode(content);//先用base64解密
            byte[] original = cipher.doFinal(encrypted);
//            byte[] original = cipher.doFinal(parseHexStr2Byte(content));
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   // public static void main(String[] args) throws IOException {
   //     String s = "{\"deviceId\" : \"HRA-11707085\"}";
   //     System.out.println("加密后：" + AESEncode(s));
   //     System.out.println("解密后：" + AESDncode("gAhqMgvd834kARWNFDTB2NG7FphtM0Aj/j61yUpomGxeyQ+974dgytumkVrcfxLwr9QqegpF7HHBl81DdF1wgM4rkxS0FJetEBnB9/TbW2A="));
   // }

}
