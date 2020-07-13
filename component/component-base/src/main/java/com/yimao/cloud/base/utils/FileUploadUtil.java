package com.yimao.cloud.base.utils;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.exception.BadRequestException;
import net.lingala.zip4j.exception.ZipException;

import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

/**
 * 文件上传工具类
 *
 * @author
 */
public class FileUploadUtil {

    private static final String FS = File.separator;

    private static final String FTP_SERVER = "172.16.1.58";
    private static final int FTP_PORT = 21;
    private static final String FTP_USERNAME = "anonymous";
    private static final String FTP_PASSWORD = "ftpuser1";

//    /**
//     * 文件上传到FTP服务器
//     *
//     * @param stream
//     * @param filename
//     * @param prefixPath
//     * @param typeFolder
//     * @param deviceId
//     * @param ticketNo
//     * @return
//     */
//    public static String saveToFtp(Part part, String typeFolder, String prefixName1, String prefixName2) throws IOException {
//
//        String rootPath;
//        // 验证
//        if (Constant.LINUX_SERVER) {//Linux服务器
//            rootPath = FS + "usr" + FS + "local" + FS + "webroot" + FS + "static";
//        } else {
//            rootPath = "D:" + FS + "tmp";
//        }
//
//        String filename = part.getSubmittedFileName();
//        String filetype = filename.substring(filename.lastIndexOf("."));
//
////        String rootPath = "/usr/local/webroot/static";
//        String path = "";
//        // 验证
//        FTPClient client = connect();
//        try {
//            // 切换到主目录
//            if (client.changeWorkingDirectory(rootPath)) {
//                if (StringUtil.isNotEmpty(typeFolder)) {
//                    if (!client.changeWorkingDirectory(typeFolder)) {
//                        if (!client.makeDirectory(typeFolder)) {
//                            throw new RuntimeException("创建目录失败");
//                        } else {
//                            client.changeWorkingDirectory(typeFolder);
//                        }
//                    }
//                    // 生成不存在的子目录
//                    String filepath = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
//                    String[] dirs = filepath.split("/");
//                    //System.out.println("======" + dirs[0] + "-" + dirs[1] + "-" + dirs[2] + "======");
//                    for (String dir : dirs) {
//                        if (StringUtil.isEmpty(dir)) {
//                            continue;
//                        }
//                        if (!client.changeWorkingDirectory(dir)) {
//                            if (!client.makeDirectory(dir)) {
//                                throw new RuntimeException("创建目录失败");
//                            } else {
//                                client.changeWorkingDirectory(dir);
//                            }
//                        }
//                    }
//
////                    String newFileName = "";
////                    if (StringUtil.isNotEmpty(prefixName1)) {
////                        newFileName += prefixName1 + "-";
////                    }
////                    if (StringUtil.isNotEmpty(prefixName2)) {
////                        newFileName += prefixName2 + "-";
////                    }
//                    String newFileName = UUIDUtil.uuid() + filetype;
//
//                    InputStream is = part.getInputStream();
//
//                    // 上传文件
//                    client.setFileType(FTP.BINARY_FILE_TYPE);
//                    boolean saveFlag = client.storeFile(newFileName, is);
//                    client.enterLocalPassiveMode();
//                    is.close();
//                    if (saveFlag) {
//                        System.out.println("===============FTP文件上传成功===============");
//                    } else {
//                        System.out.println("===============FTP文件上传失败===============");
//                    }
//
//                    path = Constant.BASE_DOMAIN + "/" + "static/" + typeFolder + "/" + filepath + "/" + newFileName;
//                    System.out.println(path);
//                    client.logout();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("上传到FTP服务器失败");
//        } finally {
//            try {
//                client.disconnect();
//            } catch (IOException e) {
//                System.out.println("关闭FTP服务时出错。");
//                e.printStackTrace();
//            }
//        }
//        return path;
//    }
//
//    public static FTPClient connect() throws IOException {
//        FTPClient client = new FTPClient();
//
//        // 连接登录服务器
//        client.connect(FTP_SERVER, FTP_PORT);
//        client.login(FTP_USERNAME, FTP_PASSWORD);
//
//        int reply = client.getReplyCode();
//        if (!FTPReply.isPositiveCompletion(reply)) {
//            client.disconnect();
//            throw new RuntimeException("isPositiveCompletion:" + FTPReply.isPositiveCompletion(reply));
//        }
//        return client;
//    }

//    public static String saveToLocal(Part part, String typeFolder, String prefixName1, String prefixName2) throws Exception {
//
//        String rootPath;
//        // 验证
//        if (Constant.LINUX_SERVER) {//Linux服务器
//            rootPath = FS + "usr" + FS + "local" + FS + "webroot";
//        } else {
//            rootPath = "D:" + FS + "tmp";
//        }
//
////        String cd = part.getHeader("Content-Disposition");
////        System.out.println("Content-Disposition = " + cd);
////        String[] cds = cd.split(";");
////        String filename = cds[2].substring(cds[2].indexOf("=") + 1).substring(cds[2].lastIndexOf("//") + 1).replace("\"", "");
//
//        String filename = part.getSubmittedFileName();
//        String filetype = filename.substring(filename.lastIndexOf("."));
//
//        String prefixName = "";
//        if (StringUtil.isNotEmpty(prefixName1)) {
//            prefixName += prefixName1 + "-";
//        }
//        if (StringUtil.isNotEmpty(prefixName2)) {
//            prefixName += prefixName2 + "-";
//        }
//
//        String newFileName = prefixName + UUIDUtil.uuid() + filetype;
//
//        String destPath = FS + "static" + FS + typeFolder + getFilePath();
//        String destFile = FS + "static" + FS + typeFolder + getFilePath() + newFileName;
//
//        File folder = new File(rootPath + destPath);
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }
//
//        File f = new File(rootPath + destFile);
//
//        InputStream is = part.getInputStream();
//
//        FileOutputStream fos = new FileOutputStream(f);
//        int size;
//        byte[] buffer = new byte[1024];
//        while ((size = is.read(buffer)) != -1) {
//            fos.write(buffer, 0, size);
//        }
//        is.close();
//        fos.close();
//
//        String url = Constant.BASE_DOMAIN + destFile.replaceAll("\\\\", "/");
//        System.out.println("url is : " + url);
//
//        return url;
//    }

    public static String saveZipToLocal(Part part, String typeFolder, String zipPasswd, String prefixName1, String prefixName2) throws IOException, ZipException {

        String rootPath;
        // 验证
        if (Constant.LINUX_SERVER) {//Linux服务器
//            rootPath = FS + "usr" + FS + "local" + FS + "webroot";
            rootPath = FS + "data";
        } else {
            rootPath = "D:" + FS + "tmp";
        }

//        String cd = part.getHeader("Content-Disposition");
//        System.out.println("Content-Disposition = " + cd);
//        String[] cds = cd.split(";");
//        String filename = cds[2].substring(cds[2].indexOf("=") + 1).substring(cds[2].lastIndexOf("//") + 1).replace("\"", "");

        String filename = part.getSubmittedFileName();
        String filetype = filename.substring(filename.lastIndexOf("."));

        String prefixName = "";
        if (StringUtil.isNotEmpty(prefixName1)) {
            prefixName += prefixName1 + "-";
        }
        if (StringUtil.isNotEmpty(prefixName2)) {
            prefixName += prefixName2 + "-";
        }

        String newFileName = prefixName + UUIDUtil.uuid() + filetype;

        String destPath = FS + "static" + FS + typeFolder + getFilePath();
        String destFile = destPath + newFileName;

        File folder = new File(rootPath + destPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File zipFile = new File(rootPath + destFile);

        InputStream is = part.getInputStream();

        FileOutputStream fos = new FileOutputStream(zipFile);
        int size;
        byte[] buffer = new byte[1024];
        while ((size = is.read(buffer)) != -1) {
            fos.write(buffer, 0, size);
        }
        is.close();
        fos.close();

        //System.out.println("zip : " + Constant.BASE_DOMAIN + destFile.replaceAll("\\\\", "/"));

        String url = "";
        if (Objects.equals(filetype.substring(1).toUpperCase(), "ZIP")) {
            File[] files = Zip4jUtil.unzip(zipFile, zipPasswd);
            for (File f : files) {
                String fileName = f.getName();
                String tmpType = fileName.substring(fileName.lastIndexOf("."));
                String tmpName = prefixName + UUIDUtil.uuid() + tmpType;
                File renameFile = new File(rootPath + destPath + tmpName);
                boolean r = f.renameTo(renameFile);
                if (r) {
                    String tmpUrl = (destPath + tmpName).replaceAll("\\\\", "/");
                    //System.out.println(tmpUrl);
                    url += tmpUrl + ",";
                }
            }
            //删除zip文件，节省磁盘空间
            zipFile.deleteOnExit();
        }
        return StringUtil.isEmpty(url) ? null : url.substring(0, url.length() - 1);
    }

    /**
     * 根据日期来生成目录，防止单一目录中文件过多
     *
     * @return
     */
    private static String getFilePath() {
        return new SimpleDateFormat(FS + "yyyy" + FS + "MM" + FS + "dd" + FS).format(Calendar.getInstance().getTime());
    }

//    public static String getDestFile(String realPath, String fileName, String typeFolder) {
//        //上传至临时文件根目录
//        String basepath = FS + "static" + FS + typeFolder;
//        String filetype = fileName.substring(fileName.lastIndexOf("."));
//        String filename = UUIDUtil.uuid() + Calendar.getInstance().getTimeInMillis() + filetype;
//
//        File folder = new File(realPath + basepath + getFilePath());
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }
//        return basepath + getFilePath() + filename;
//    }


    /**
     * 图片保存
     *
     * @param fileName 图片名称
     * @param path     保存路径
     * @return
     */
    public static String saveImage(InputStream inputStream, String fileName, String path) {
        if (inputStream == null || StringUtil.isEmpty(fileName) || StringUtil.isEmpty(path)) {
            throw new BadRequestException("缺少必要参数");
        }

        OutputStream os = null;
        try {
            byte[] bs = new byte[1024];
            int len;
            File tempFile = new File(path);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            os = new FileOutputStream(tempFile.getPath() + File.separator + fileName);
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            return path + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return "图片上传失败";
        } finally {
            // 完毕，关闭所有链接
            try {
                if(os != null){
                    os.close();
                }
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
