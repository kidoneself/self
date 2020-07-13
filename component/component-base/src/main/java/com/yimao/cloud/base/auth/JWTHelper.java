// package com.yimao.cloud.base.auth;
//
// import com.alibaba.fastjson.JSONObject;
// import com.yimao.cloud.base.utils.DateUtil;
// import com.yimao.cloud.base.utils.RSAUtil;
// import com.yimao.cloud.base.utils.UUIDUtil;
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.JwtBuilder;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
//
// import java.util.Date;
//
// /**
//  * @author Zhang Bo
//  * @date 2018/7/31.
//  */
// public class JWTHelper {
//
//     /**
//      * 私钥加密jwt token<br/>
//      * JWT过期时间设置在凌晨，使用概率比较低的时间点，避免用户正在使用token过期，数据丢失
//      *
//      * @param jwtInfo 需要加密的内容
//      * @param priKey  私钥
//      * @param expire  过期时间（单位：秒）
//      */
//     public static String generateJWTInfo(JWTInfo jwtInfo, String priKey, int expire) {
//         //创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
//         // Map<String, Object> claims = new HashMap<>();
//         // claims.put(AuthConstants.JWT_ID, jwtInfo.getId());
//         // claims.put(AuthConstants.JWT_REALNAME, jwtInfo.getRealName());
//         // claims.put(AuthConstants.JWT_TYPE, jwtInfo.getType());
//         //jwt的签发时间
//         Date now = new Date();
//         //JWT过期时间
//         Date expireDate = DateUtil.JWTTokenExpirationTime(now, expire);
//         JwtBuilder builder = Jwts.builder()
//                 //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
//                 // .setClaims(claims)
//                 //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
//                 .setId(UUIDUtil.longuuid32())
//                 .setIssuedAt(now)
//                 //sub(Subject)：代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userId，roldid之类的，作为用户的唯一标志。
//                 .setSubject(JSONObject.toJSONString(jwtInfo))
//                 .signWith(SignatureAlgorithm.RS256, RSAUtil.loadPrivateKey(priKey))
//                 .setExpiration(expireDate);
//         return builder.compact();
//     }
//
//     /**
//      * 获取jwt token中的用户信息
//      *
//      * @param token  token
//      * @param pubKey 公钥
//      */
//     public static JWTInfo getJWTInfoFromToken(String token, String pubKey) {
//         //公钥解析token
//         Claims body = getClaimFromToken(token, pubKey);
//         String subject = body.getSubject();
//         return JSONObject.parseObject(subject, JWTInfo.class);
//     }
//
//     /**
//      * 获取jwt token中的过期时间
//      *
//      * @param token  token
//      * @param pubKey 公钥
//      */
//     public static Date getExpirationDate(String token, String pubKey) {
//         return getClaimFromToken(token, pubKey).getExpiration();
//     }
//
//     /**
//      * 获取jwt的payload部分
//      *
//      * @param token  token
//      * @param pubKey 公钥
//      */
//     private static Claims getClaimFromToken(String token, String pubKey) {
//         return Jwts.parser().setSigningKey(RSAUtil.loadPublicKey(pubKey)).parseClaimsJws(token).getBody();
//     }
//
//     // private static String getObjectValue(Object obj) {
//     //     return obj == null ? "" : obj.toString();
//     // }
// }
