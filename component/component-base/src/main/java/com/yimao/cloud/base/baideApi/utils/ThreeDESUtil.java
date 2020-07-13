package com.yimao.cloud.base.baideApi.utils;

import com.yimao.cloud.base.baideApi.utils.Base64;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;

@Slf4j
public final class ThreeDESUtil {
    private static final String IV = "12345678-";
    private static final String ALGORITHM = "DESede";

    public ThreeDESUtil() {
    }

    public static String encrypt(String data, String secretKey) {
        SecretKeySpec deskey = new SecretKeySpec(secretKey.getBytes(), "DESede");

        try {
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(1, deskey);
            byte[] output = cipher.doFinal(data.getBytes("UTF-8"));
            return org.apache.commons.codec.binary.Base64.encodeBase64String(output);
        } catch (NoSuchAlgorithmException var5) {
            var5.printStackTrace();
        } catch (NoSuchPaddingException var6) {
            var6.printStackTrace();
        } catch (InvalidKeyException var7) {
            var7.printStackTrace();
        } catch (IllegalBlockSizeException var8) {
            var8.printStackTrace();
        } catch (BadPaddingException var9) {
            var9.printStackTrace();
        } catch (UnsupportedEncodingException var10) {
            var10.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String data, String secretKey) {
        SecretKeySpec deskey = new SecretKeySpec(secretKey.getBytes(), "DESede");

        try {
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(2, deskey);
            byte[] output = cipher.doFinal(Base64.decode(data));
            return new String(output, "UTF-8");
        } catch (NoSuchAlgorithmException var5) {
            var5.printStackTrace();
        } catch (NoSuchPaddingException var6) {
            var6.printStackTrace();
        } catch (InvalidKeyException var7) {
            var7.printStackTrace();
        } catch (IllegalBlockSizeException var8) {
            var8.printStackTrace();
        } catch (BadPaddingException var9) {
            var9.printStackTrace();
        } catch (UnsupportedEncodingException var10) {
            var10.printStackTrace();
        }

        return null;
    }

    public static byte[] unZip(byte[] data) {
        log.info(data.length + "  size", new Object[0]);
        byte[] b = null;

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ZipInputStream zip = new ZipInputStream(bis);

            while(zip.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                // int num = true;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int num;
                while((num = zip.read(buf, 0, buf.length)) != -1) {
                    baos.write(buf, 0, num);
                }

                b = baos.toByteArray();
                baos.flush();
                baos.close();
            }

            zip.close();
            bis.close();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return b;
    }

    public static byte[] gZip(byte[] data) {
        byte[] b = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(data);
            gzip.finish();
            gzip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return b;
    }

    public static byte[] unGZip(byte[] data) {
        byte[] b = null;

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[1024];
            // int num = true;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int num;
            while((num = gzip.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }

            b = baos.toByteArray();
            baos.flush();
            baos.close();
            gzip.close();
            bis.close();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return b;
    }
}
