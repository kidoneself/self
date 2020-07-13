// package com.yimao.cloud.hra.validator;
//
// import com.yimao.cloud.base.validator.constraint.NotNullAndBlankConstraint;
// import com.yimao.cloud.hra.po.Reserve;
// import com.yimao.cloud.pojo.dto.hra.ReserveDTO;
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
// public class HraValidator {
//
//     /**
//      * 预约验证
//      *
//      * @param reserve 需要进行属性验证的对象
//      * @return
//      */
//     public static boolean reserveValidator(Reserve reserve) {
//         ValidationContext vctx = new ValidationContext();
//         vctx.add(new Check("name", new NotNullAndBlankConstraint()));       //评估人
//         vctx.add(new Check("mobile", new NotNullAndBlankConstraint()));     //手机号
//         vctx.add(new Check("stationId", new NotNullAndBlankConstraint()));  //服务站编号
//         vctx.add(new Check("reserveTime", new NotNullAndBlankConstraint()));//预约评估时间
//         vctx.add(new Check("code", new NotNullAndBlankConstraint()));       //评估唯一code
//         Vtor vtor = Vtor.create();
//         vtor.validate(vctx, reserve);
//         return !vtor.hasViolations();
//     }
//
//     /**
//      * 预约验证
//      *
//      * @param reserveDTO
//      * @return
//      */
//     public static boolean reserveValidator(ReserveDTO reserveDTO) {
//         ValidationContext vctx = new ValidationContext();
//         vctx.add(new Check("ticketNo", new NotNullAndBlankConstraint()));
//         vctx.add(new Check("userName", new NotNullAndBlankConstraint()));
//         vctx.add(new Check("phone", new NotNullAndBlankConstraint()));
//         vctx.add(new Check("stationId", new NotNullAndBlankConstraint()));
//         vctx.add(new Check("reserveTime", new NotNullAndBlankConstraint()));
//         vctx.add(new Check("birthDate", new NotNullAndBlankConstraint()));
//         Vtor vtor = Vtor.create();
//         vtor.validate(vctx, reserveDTO);
//         return !vtor.hasViolations();
//     }
//
// }
