// package com.yimao.cloud.base.validator.constraint;
//
// import com.yimao.cloud.base.validator.annotation.TimeFormat;
// import jodd.vtor.ValidationConstraint;
// import jodd.vtor.ValidationConstraintContext;
// import org.joda.time.format.DateTimeFormat;
//
//
// public class TimeFormatConstraint implements ValidationConstraint<TimeFormat> {
//     private String format = "yyyy-MM-dd";
//
//     public TimeFormatConstraint() {
//     }
//
//     public TimeFormatConstraint(String paramString) {
//         this.format = paramString;
//     }
//
//     public void configure(TimeFormat paramTimeFormat) {
//         this.format = paramTimeFormat.value();
//     }
//
//     public boolean isValid(ValidationConstraintContext paramValidationConstraintContext, Object paramObject) {
//         return validate(paramObject);
//     }
//
//     public boolean validate(Object paramObject) {
//         if (paramObject == null) {
//             return true;
//         }
//         try {
//             DateTimeFormat.forPattern(this.format).parseDateTime((String) paramObject);
//         } catch (IllegalArgumentException localIllegalArgumentException) {
//             return false;
//         }
//         return true;
//     }
// }