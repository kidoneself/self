// package com.yimao.cloud.base.validator.constraint;
//
// import com.yimao.cloud.base.validator.annotation.Password;
// import jodd.vtor.ValidationConstraint;
// import jodd.vtor.ValidationConstraintContext;
//
// import java.util.HashSet;
// import java.util.regex.Pattern;
//
//
// public class PasswordConstraint implements ValidationConstraint<Password> {
//     private int verifyLevel = 255;
//     private int minLength = 0;
//     private int maxLength = 20;
//     public static final int NOT_UNIQUE = 1;
//     public static final int NOT_ORDER_NUM = 2;
//     public static final int HAS_NUM = 4;
//     public static final int HAS_CHAR = 8;
//     public static final int HAS_UPPER_CHAR = 16;
//     public static final int HAS_LOWER_CHAR = 32;
//     public static final int HAS_SYMBOL = 64;
//     public static final int STRICT = 255;
//     public static final int STRONG = 55;
//     public static final int MIDDLE = 15;
//     public static final int WEAK = 0;
//     private static final Pattern hasChar = Pattern.compile("[A-Za-z]");
//     private static final Pattern hasUpperChar = Pattern.compile("[A-Z]");
//     private static final Pattern hasLowerChar = Pattern.compile("[a-z]");
//     private static final Pattern hasNum = Pattern.compile("[0-9]");
//     private static final Pattern hasSymbol = Pattern.compile("[^a-zA-Z0-9]");
//     private static final Pattern allIsNum = Pattern.compile("\\d+");
//
//     public PasswordConstraint() {
//     }
//
//     public PasswordConstraint(int paramInt1, int paramInt2) {
//         this.minLength = paramInt1;
//         this.maxLength = paramInt2;
//     }
//
//     public PasswordConstraint(int paramInt1, int paramInt2, int paramInt3) {
//         this.minLength = paramInt1;
//         this.maxLength = paramInt2;
//         this.verifyLevel = paramInt3;
//     }
//
//     @Override
//     public void configure(Password paramPassword) {
//         this.minLength = paramPassword.minLength();
//         this.maxLength = paramPassword.maxLenght();
//         this.verifyLevel = paramPassword.level();
//     }
//
//     @Override
//     public boolean isValid(ValidationConstraintContext paramValidationConstraintContext, Object paramObject) {
//         if (paramObject == null) {
//             return false;
//         }
//         String str = (String) paramObject;
//         if (str.length() < this.minLength || str.length() > this.maxLength) {
//             return false;
//         }
//         if (isMask(1) && isAllSame(str)) {
//             return false;
//         }
//         if (isMask(2) && isOrderedNumber(str)) {
//             return false;
//         }
//         if (isMask(8) && !hasChar.matcher(str).find()) {
//             return false;
//         }
//         if (isMask(16) && !hasUpperChar.matcher(str).find()) {
//             return false;
//         }
//         if (isMask(32) && !hasLowerChar.matcher(str).find()) {
//             return false;
//         }
//         if (isMask(4) && !hasNum.matcher(str).find()) {
//             return false;
//         }
//         if (isMask(64) && !hasSymbol.matcher(str).find()) {
//             return false;
//         }
//         return true;
//     }
//
//     private boolean isMask(int paramInt) {
//         return (this.verifyLevel & paramInt) == paramInt;
//     }
//
//     private boolean isOrderedNumber(String paramString) {
//         if (paramString.length() <= 2) {
//             return false;
//         }
//         if (!allIsNum.matcher(paramString).matches()) {
//             return false;
//         }
//         int i = charToInt(paramString.charAt(0));
//         int j = charToInt(paramString.charAt(1));
//         int k = j - i > 0 ? 1 : 0;
//         for (int m = 1; m < paramString.length(); m++) {
//             if (k != 0) {
//                 if (charToInt(paramString.charAt(m)) - charToInt(paramString.charAt(m - 1)) != 1) {
//                     return false;
//                 }
//             } else if (charToInt(paramString.charAt(m - 1)) - charToInt(paramString.charAt(m)) != 1) {
//                 return false;
//             }
//         }
//         return true;
//     }
//
//     private int charToInt(char paramChar) {
//         return paramChar - '0';
//     }
//
//     private boolean isAllSame(String paramString) {
//         HashSet<Character> localHashSet = new HashSet<>(paramString.length());
//         localHashSet.add(paramString.charAt(0));
//         for (int i = 1; i < paramString.length(); i++) {
//             if (localHashSet.add(paramString.charAt(i))) {
//                 return false;
//             }
//         }
//         return true;
//     }
// }