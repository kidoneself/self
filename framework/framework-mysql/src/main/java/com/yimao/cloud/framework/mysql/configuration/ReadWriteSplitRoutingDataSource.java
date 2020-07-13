// package com.yimao.cloud.framework.mysql.configuration;
//
// import com.yimao.cloud.base.utils.StringUtil;
// import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Random;
//
// /**
//  * @author Zhang Bo
//  * @date 2018/9/27.
//  */
// public class ReadWriteSplitRoutingDataSource extends AbstractRoutingDataSource {
//
//     public static final String MASTER = "master";
//     public static final String SLAVE1 = "slave1";
//     public static final String SLAVE2 = "slave2";
//
//     private static List<String> dataSourceKeyList = new ArrayList<>();
//
//     private static final ThreadLocal<String> dataSourceKey = new InheritableThreadLocal<>();
//
//     static {
//         dataSourceKeyList.add(SLAVE1);
//         dataSourceKeyList.add(SLAVE2);
//     }
//
//     public static void setKey(String key) {
//         if (key == null || StringUtil.isBlank(key)) {
//             Random random = new Random();
//             int pos = random.nextInt(dataSourceKeyList.size());
//             key = dataSourceKeyList.get(pos);
//         }
//         dataSourceKey.set(key);
//     }
//
//     public static void clear() {
//         dataSourceKey.remove();
//     }
//
//     @Override
//     protected Object determineCurrentLookupKey() {
//         return dataSourceKey.get() == null ? MASTER : dataSourceKey.get();
//     }
//
// }
