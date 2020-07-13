// package com.yimao.cloud.base.auth;
//
// import lombok.extern.slf4j.Slf4j;
//
// /**
//  * @author Zhang Bo
//  * @date 2018/7/31.
//  */
// @Slf4j
// public class RsaKeyHelper {
//
//     private static final String RSA = "RSA";
//
//     // /**
//     //  * 获取公钥
//     //  *
//     //  * @param publicKey 公钥字符
//     //  */
//     // PublicKey getPublicKey(String publicKey) {
//     //     try {
//     //         byte[] bytes = Base64.decodeBase64(publicKey);
//     //         X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
//     //         KeyFactory kf = KeyFactory.getInstance(RSA);
//     //         return kf.generatePublic(spec);
//     //     } catch (Exception e) {
//     //         log.error("获取RSA公钥失败。");
//     //         throw new YimaoException("获取RSA公钥失败。");
//     //     }
//     // }
//
//     // /**
//     //  * 获取私钥
//     //  *
//     //  * @param privateKey 私钥字符
//     //  */
//     // PrivateKey getPrivateKey(String privateKey) {
//     //     try {
//     //         byte[] bytes = Base64.decodeBase64(privateKey);
//     //         PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
//     //         KeyFactory kf = KeyFactory.getInstance(RSA);
//     //         return kf.generatePrivate(spec);
//     //     } catch (Exception e) {
//     //         log.error("获取RSA私钥失败。");
//     //         throw new YimaoException("获取RSA私钥失败。");
//     //     }
//     // }
//
//     // /**
//     //  * 生成RSA公钥和私钥
//     //  *
//     //  * @param password 密钥字符
//     //  */
//     // public static Map<String, String> generateKey(String password) {
//     //     try {
//     //         KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
//     //         SecureRandom secureRandom = new SecureRandom(password.getBytes());
//     //         keyPairGenerator.initialize(1024, secureRandom);
//     //         KeyPair keyPair = keyPairGenerator.genKeyPair();
//     //         byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
//     //         byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
//     //         Map<String, String> map = new HashMap<>();
//     //         map.put(AuthConstants.PUB, Base64.encodeBase64String(publicKeyBytes));
//     //         map.put(AuthConstants.PRI, Base64.encodeBase64String(privateKeyBytes));
//     //         return map;
//     //     } catch (Exception e) {
//     //         log.error("生成RSA公钥和私钥失败。");
//     //         throw new YimaoException("生成RSA公钥和私钥失败。");
//     //     }
//     // }
//
//     // public static String toHexString(byte[] b) {
//     //     // return new BASE64Encoder().encodeBuffer(b);
//     //     return Base64.encodeBase64String(b);
//     // }
//     //
//     // public static final byte[] toBytes(String s) {
//     //     // return new BASE64Decoder().decodeBuffer(s);
//     //     return Base64.decodeBase64(s);
//     // }
//
//     // /**
//     //  * 获取公钥
//     //  *
//     //  * @param filename
//     //  * @return
//     //  * @throws Exception
//     //  */
//     // public PublicKey getPublicKey(String filename) throws Exception {
//     //     InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(filename);
//     //     DataInputStream dis = new DataInputStream(resourceAsStream);
//     //     byte[] keyBytes = new byte[resourceAsStream.available()];
//     //     dis.readFully(keyBytes);
//     //     dis.close();
//     //     X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
//     //     KeyFactory kf = KeyFactory.getInstance(RSA);
//     //     return kf.generatePublic(spec);
//     // }
//     //
//     // /**
//     //  * 获取密钥
//     //  *
//     //  * @param filename
//     //  * @return
//     //  * @throws Exception
//     //  */
//     // public PrivateKey getPrivateKey(String filename) throws Exception {
//     //     InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(filename);
//     //     DataInputStream dis = new DataInputStream(resourceAsStream);
//     //     byte[] keyBytes = new byte[resourceAsStream.available()];
//     //     dis.readFully(keyBytes);
//     //     dis.close();
//     //     PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
//     //     KeyFactory kf = KeyFactory.getInstance(RSA);
//     //     return kf.generatePrivate(spec);
//     // }
//
//     // /**
//     //  * 生成rsa公钥和密钥
//     //  *
//     //  * @param publicKeyFilename
//     //  * @param privateKeyFilename
//     //  * @param password
//     //  * @throws IOException
//     //  * @throws NoSuchAlgorithmException
//     //  */
//     // public void generateKey(String publicKeyFilename, String privateKeyFilename, String password) throws IOException, NoSuchAlgorithmException {
//     //     KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
//     //     SecureRandom secureRandom = new SecureRandom(password.getBytes());
//     //     keyPairGenerator.initialize(1024, secureRandom);
//     //     KeyPair keyPair = keyPairGenerator.genKeyPair();
//     //     byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
//     //     FileOutputStream fos = new FileOutputStream(publicKeyFilename);
//     //     fos.write(publicKeyBytes);
//     //     fos.close();
//     //     byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
//     //     fos = new FileOutputStream(privateKeyFilename);
//     //     fos.write(privateKeyBytes);
//     //     fos.close();
//     // }
//     //
//     // /**
//     //  * 生存rsa公钥
//     //  *
//     //  * @param password
//     //  * @throws NoSuchAlgorithmException
//     //  */
//     // public static byte[] generatePublicKey(String password) throws NoSuchAlgorithmException {
//     //     KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
//     //     SecureRandom secureRandom = new SecureRandom(password.getBytes());
//     //     keyPairGenerator.initialize(1024, secureRandom);
//     //     KeyPair keyPair = keyPairGenerator.genKeyPair();
//     //     return keyPair.getPublic().getEncoded();
//     // }
//     //
//     // /**
//     //  * 生存rsa公钥
//     //  *
//     //  * @param password
//     //  * @throws NoSuchAlgorithmException
//     //  */
//     // public static byte[] generatePrivateKey(String password) throws NoSuchAlgorithmException {
//     //     KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
//     //     SecureRandom secureRandom = new SecureRandom(password.getBytes());
//     //     keyPairGenerator.initialize(1024, secureRandom);
//     //     KeyPair keyPair = keyPairGenerator.genKeyPair();
//     //     return keyPair.getPrivate().getEncoded();
//     // }
//
//     // public static void main(String[] args) throws NoSuchAlgorithmException {
//     //     KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
//     //     SecureRandom secureRandom = new SecureRandom("123".getBytes());
//     //     keyPairGenerator.initialize(1024, secureRandom);
//     //     KeyPair keyPair = keyPairGenerator.genKeyPair();
//     //     System.out.println(keyPair.getPublic().getEncoded());
//     // }
//
// }
//
