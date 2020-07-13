package com.yimao.cloud.out.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;


public class DecryptSupport {

    public static String decrypt(String src) {
        if (src == null || src.length() == 0) {
            return null;
        }
        String[] array = src.split("#");
        if (array.length < 2) {
            return null;
        }
        byte[] md5Bytes = getMd5Bytes(array[0]);
        if (md5Bytes == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, SecretKeyFactory.getInstance("DES")
                    .generateSecret(new DESKeySpec(md5Bytes)));
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(array[1]));
            return new String(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getMd5Bytes(String source) {
        if (source == null) {
            return null;
        }
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            digest = md5.digest(source.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (digest == null) {
            return null;
        }
        return digest;

    }

}
