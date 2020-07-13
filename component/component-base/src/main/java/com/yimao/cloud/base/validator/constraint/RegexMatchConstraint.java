// package com.yimao.cloud.base.validator.constraint;
//
// import com.yimao.cloud.base.validator.annotation.RegexMatch;
// import jodd.vtor.ValidationConstraint;
// import jodd.vtor.ValidationConstraintContext;
//
// import java.util.regex.Pattern;
//
// public class RegexMatchConstraint implements ValidationConstraint<RegexMatch> {
//     private Pattern pattern;
//
//     public RegexMatchConstraint(String paramString) {
//         this.pattern = Pattern.compile(paramString);
//     }
//
//     public void configure(RegexMatch paramRegexMatch) {
//         this.pattern = Pattern.compile(paramRegexMatch.value());
//     }
//
//     public boolean isValid(ValidationConstraintContext paramValidationConstraintContext, Object paramObject) {
//         if (paramObject == null) {
//             return true;
//         }
//         return this.pattern.matcher((String) paramObject).matches();
//     }
// }