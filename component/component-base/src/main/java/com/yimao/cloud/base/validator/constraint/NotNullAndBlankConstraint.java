// package com.yimao.cloud.base.validator.constraint;
//
// import com.yimao.cloud.base.validator.annotation.NotNullAndBlank;
// import jodd.util.StringUtil;
// import jodd.vtor.ValidationConstraint;
// import jodd.vtor.ValidationConstraintContext;
//
// public class NotNullAndBlankConstraint implements ValidationConstraint<NotNullAndBlank> {
//     @Override
//     public void configure(NotNullAndBlank paramNotNullAndBlank) {
//     }
//
//     @Override
//     public boolean isValid(ValidationConstraintContext paramValidationConstraintContext, Object paramObject) {
//         return validate(paramObject);
//     }
//
//     public static boolean validate(Object paramObject) {
//         return paramObject != null && StringUtil.isNotBlank(paramObject.toString());
//     }
// }