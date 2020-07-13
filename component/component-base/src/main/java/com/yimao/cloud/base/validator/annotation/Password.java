// package com.yimao.cloud.base.validator.annotation;
//
// import jodd.vtor.Constraint;
// import jodd.vtor.constraint.NotBlankConstraint;
//
// import java.lang.annotation.Retention;
// import java.lang.annotation.RetentionPolicy;
// import java.lang.annotation.Target;
//
// @Retention(RetentionPolicy.RUNTIME)
// @Target({java.lang.annotation.ElementType.FIELD})
// @Constraint(NotBlankConstraint.class)
// public @interface Password {
//     int minLength() default 4;
//
//     int maxLenght() default 20;
//
//     int level() default 255;
// }