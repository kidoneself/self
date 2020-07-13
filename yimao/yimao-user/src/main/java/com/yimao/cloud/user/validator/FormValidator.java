// package com.yimao.cloud.user.validator;
//
// import com.yimao.cloud.base.validator.constraint.NotNullAndBlankConstraint;
// import com.yimao.cloud.base.validator.constraint.RegexMatchConstraint;
// import com.yimao.cloud.pojo.dto.user.UserAddressDTO;
// import com.yimao.cloud.pojo.dto.user.UserDTO;
// import jodd.vtor.Check;
// import jodd.vtor.ValidationContext;
// import jodd.vtor.Vtor;
//
// /**
//  * 验证
//  * created by liuhao@yimaokeji.com
//  * 2017102017/10/19
//  *
//  * @author liuhao@yimaokeji.com
//  */
// public class FormValidator {
//
//     private static final String MOBILE_REGEX = "^1[3|4|5|6|7|8|9][0-9]{9}$";
//
//     /**
//      * 地址验证
//      *
//      * @param address 地址
//      * @return boolean
//      */
//     public static boolean addressValidator(UserAddressDTO address) {
//         ValidationContext vctx = new ValidationContext();
//         vctx.add(new Check("contact", new NotNullAndBlankConstraint()));
//         vctx.add(new Check("mobile", new NotNullAndBlankConstraint()));
//         vctx.add(new Check("province", new NotNullAndBlankConstraint()));
//         vctx.add(new Check("city", new NotNullAndBlankConstraint()));
//         vctx.add(new Check("region", new NotNullAndBlankConstraint()));
//         vctx.add(new Check("street", new NotNullAndBlankConstraint()));
//         Vtor vtor = Vtor.create();
//         vtor.validate(vctx, address);
//         return !vtor.hasViolations();
//     }
//
//     /**
//      * 注册验证
//      *
//      * @param userDTO 用户
//      * @return boolean
//      */
//     public static boolean mobileValidator(UserDTO userDTO) {
//         ValidationContext vctx = new ValidationContext();
//         vctx.add(new Check("mobile", new NotNullAndBlankConstraint()));
//         vctx.add(new Check("mobile", new RegexMatchConstraint(MOBILE_REGEX)));
//         Vtor vtor = Vtor.create();
//         vtor.validate(vctx, userDTO);
//         return !vtor.hasViolations();
//     }
// }
