package com.yimao.cloud.base.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zhang Bo
 * @date 2018/6/11.
 */
@Slf4j
public class SFTPUtil {

    private static final String STATIC = "/static/";
    /**
     * SFTP 登录用户名
     */
    private static final String username = "mysftp";
    /**
     * SFTP 登录密码
     */
    private static final String password = "mysftp";
    /**
     * SFTP 端口
     */
    private static final int port = 22;
    /**
     * 生产环境FTP内网地址
     */
    private static final String SFTP_HOST_PRO = "172.16.2.194";
    /**
     * 测试环境FTP内网地址
     */
    private static final String SFTP_HOST_TEST = "172.16.1.59";
    /**
     * 开发环境FTP内网地址
     */
    private static final String SFTP_HOST_DEV = "192.168.10.63";

    /**
     * 关闭连接 server
     */
    private static void logout(ChannelSftp sftp, Session session) {
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    /**
     * 将输入流的数据上传到sftp作为文件。
     *
     * @param in        文件
     * @param directory 存放目录
     * @return
     * @throws Exception
     */
    public static String uploadZipFile(InputStream in, String directory, String fileName, String prefixName1, String prefixName2, String zipPasswd) throws Exception {
        // 文件名校验
        if (StringUtil.isBlank(fileName)) {
            throw new BadRequestException("文件名不能为空。");
        }
        // 文件格式校验
        // Pattern pattern = Pattern.compile(".*(.jpg|.JPG|.png|.PNG|.gif|.GIF|.pdf|.PDF|.xls|.XLS|.xlsx|.XLSX|.mp4|.webm|.ogv)$");
        // Matcher matcher = pattern.matcher(fileName);
        // if (!matcher.matches()) {
        //     throw new BadRequestException("不支持该文件格式上传。");
        // }
        String host;
        if (EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_PRO;
        } else if (EnvironmentEnum.TEST.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_TEST;
        } else if (EnvironmentEnum.DEV.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_DEV;
        } else {
            // String path = "D:\\head\\";
            // if (StringUtil.isEmpty(fileName)) {
            //     fileName = "head.jpg";
            // }
            // return FileUploadUtil.saveImage(in, fileName, path);
//            throw new YimaoException("只有测试和生产环境支持上传文件。");
            host = SFTP_HOST_DEV;
        }

        //连接sftp服务器
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftp = (ChannelSftp) channel;

        try {
            String prefixName = "";
            if (StringUtil.isNotEmpty(prefixName1)) {
                prefixName += prefixName1 + "-";
            }
            if (StringUtil.isNotEmpty(prefixName2)) {
                prefixName += prefixName2 + "-";
            }

            //目录和文件名设置
            String filepath = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
            String path = STATIC + directory + "/" + filepath;
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = prefixName + UUIDUtil.uuid() + suffix;

            // 生成不存在的子目录
            String[] dirs = path.split("/");
            for (String dir : dirs) {
                if (StringUtil.isEmpty(dir)) continue;
                try {
                    sftp.cd(dir);
                } catch (SftpException ex) {
                    try {
                        sftp.mkdir(dir);
                    } catch (SftpException exx) {
                        throw new YimaoException("请先联系系统管理员在服务器上创建");
                    }
                    sftp.cd(dir);
                }
            }

            File zipFile;
            if (EnvironmentEnum.LOCAL.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
                zipFile = new File("D:" + File.separator + "tmp" + File.separator + newFileName);
            } else {
                zipFile = new File("/tmp/" + newFileName);
            }

            FileOutputStream fos = new FileOutputStream(zipFile);
            int size;
            byte[] buffer = new byte[1024];
            while ((size = in.read(buffer)) != -1) {
                fos.write(buffer, 0, size);
            }
            in.close();
            fos.flush();
            fos.close();

            FileInputStream zipFileInputStream = new FileInputStream(zipFile);

            sftp.put(zipFileInputStream, newFileName);  //上传文件
            zipFileInputStream.close();

            //如果是zip文件
            if (Objects.equals(suffix.substring(1).toUpperCase(), "ZIP")) {
                String url = "";
                File[] files = Zip4jUtil.unzip(zipFile, zipPasswd);
                for (File f : files) {
                    String fName = f.getName();
                    String tmpType = fName.substring(fName.lastIndexOf("."));
                    String tmpName = prefixName + UUIDUtil.uuid() + tmpType;
                    File renameFile;
                    if (EnvironmentEnum.LOCAL.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
                        renameFile = new File("D:" + File.separator + "tmp" + File.separator + tmpName);
                    } else {
                        renameFile = new File("/tmp/" + tmpName);
                    }
                    boolean r = f.renameTo(renameFile);
                    if (r) {
                        FileInputStream tmpInputStream = new FileInputStream(renameFile);
                        sftp.put(tmpInputStream, tmpName);
                        tmpInputStream.close();
                        if (renameFile.exists()) {
                            renameFile.delete();
                        }
                        url += (path + "/" + tmpName).replaceAll("\\\\", "/") + ",";
                    }
                }
                //删除zip文件，节省磁盘空间
                if (zipFile.exists()) {
                    zipFile.delete();
                }
                return StringUtil.isEmpty(url) ? null : url.substring(0, url.length() - 1);
            }

            return path + "/" + newFileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            //断开连接sftp服务器
            logout(sftp, session);
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 将输入流的数据上传到sftp作为文件。
     *
     * @param in        文件
     * @param directory 存放目录
     * @return
     * @throws Exception
     */
    public static String upload(InputStream in, String directory, String fileName, String deleteUrl) throws Exception {
        // 文件名校验
        if (StringUtil.isBlank(fileName)) {
            throw new BadRequestException("文件名不能为空。");
        }
        // 文件格式校验
        Pattern pattern = Pattern.compile(".*(.jpg|.JPG|.png|.PNG|.gif|.GIF|.pdf|.PDF|.xls|.XLS|.xlsx|.XLSX|.mp4|.webm|.ogv)$");
        Matcher matcher = pattern.matcher(fileName);
        if (!matcher.matches()) {
            throw new BadRequestException("不支持该文件格式上传。");
        }
        String host;
        if (EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_PRO;
        } else if (EnvironmentEnum.TEST.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_TEST;
        } else if (EnvironmentEnum.DEV.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_DEV;
        } else {
            // String path = "D:\\head\\";
            // if (StringUtil.isEmpty(fileName)) {
            //     fileName = "head.jpg";
            // }
            // return FileUploadUtil.saveImage(in, fileName, path);
//            throw new YimaoException("只有测试和生产环境支持上传文件。");
            host = SFTP_HOST_DEV;
        }

        //连接sftp服务器
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftp = (ChannelSftp) channel;
        //解决并发下读不到文件内容造成的超时问题
        byte[] byteArrayFile = IOUtils.toByteArray(in);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayFile);
        try {
            //删除旧文件
            if (StringUtil.isNotBlank(deleteUrl) && deleteUrl.contains(STATIC + directory)) {
                delete(sftp, deleteUrl);
                sftp.cd("/");//回到sftp根目录
            }

            //目录和文件名设置
            String filepath = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String path = STATIC + directory;
            String newFileName = fileName;
            if (!"qrcode".equalsIgnoreCase(directory)) {
                path += "/" + filepath;
                newFileName = UUIDUtil.uuid() + suffix;
            }

            // 生成不存在的子目录
            String[] dirs = path.split("/");
            for (String dir : dirs) {
                if (StringUtil.isEmpty(dir)) continue;
                try {
                    sftp.cd(dir);
                } catch (SftpException ex) {
                    try {
                        sftp.mkdir(dir);
                    } catch (SftpException exx) {
                        log.info("SFTPUtil.upload.path=" + path);
                        log.error("SFTPUtil.upload.path.dir=" + dir);
                        log.error(exx.getMessage(), exx);
                        throw new YimaoException("请先联系系统管理员在服务器上创建");
                    }
                    sftp.cd(dir);
                }
            }

            sftp.put(byteArrayInputStream, newFileName);  //上传文件
            byteArrayInputStream.close();
            in.close();
            //断开连接sftp服务器
            return path + "/" + newFileName;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            logout(sftp, session);
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 将SXSSFWorkbook的数据上传到sftp作为文件。
     */
    public static String upload(SXSSFWorkbook workbook, String title) throws Exception {
        // 文件名校验
        if (StringUtil.isBlank(title)) {
            throw new BadRequestException("文件名不能为空。");
        }
        String fileName = title + " " + DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_03);
        // 文件格式校验
        if (!fileName.endsWith(".xlsx")) {
            fileName = fileName + ".xlsx";
        }
        Pattern pattern = Pattern.compile(".*(.xls|.XLS|.xlsx|.XLSX)$");
        Matcher matcher = pattern.matcher(fileName);
        if (!matcher.matches()) {
            throw new BadRequestException("不支持该文件格式上传。");
        }
        String host;
        if (EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_PRO;
        } else if (EnvironmentEnum.TEST.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_TEST;
        } else if (EnvironmentEnum.DEV.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_DEV;
        } else {
            host = SFTP_HOST_DEV;
        }

        //连接sftp服务器
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftp = (ChannelSftp) channel;
        try {
            //目录和文件名设置
            String path = STATIC + "common/excel";
            // 生成不存在的子目录
            String[] dirs = path.split("/");
            for (String dir : dirs) {
                if (StringUtil.isEmpty(dir)) continue;
                try {
                    sftp.cd(dir);
                } catch (SftpException ex) {
                    try {
                        sftp.mkdir(dir);
                    } catch (SftpException exx) {
                        log.info("SFTPUtil.upload.path=" + path);
                        log.error("SFTPUtil.upload.path.dir=" + dir);
                        log.error(exx.getMessage(), exx);
                        throw new YimaoException("请先联系系统管理员在服务器上创建");
                    }
                    sftp.cd(dir);
                }
            }
            //将文件写入到ftp服务器上
            log.info("导出文件名为={}", fileName);
            workbook.write(sftp.put(fileName));
            OutputStream outputStream = sftp.getOutputStream();
            outputStream.flush();
            outputStream.close();
            return path + "/" + fileName;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            logout(sftp, session);
        }
    }

    /**
     * sftp删除文件
     *
     * @param path 文件路径
     */
    public static void delete(String path) {
        try {
            // 文件名校验
            if (StringUtil.isBlank(path)) {
                throw new BadRequestException("文件名不能为空。");
            }
            String host;
            if (EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
                host = SFTP_HOST_PRO;
            } else if (EnvironmentEnum.TEST.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
                host = SFTP_HOST_TEST;
            } else if (EnvironmentEnum.DEV.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
                host = SFTP_HOST_DEV;
            } else {
                // throw new YimaoException("只有测试和生产环境支持删除文件。");
                host = SFTP_HOST_DEV;
            }
            //连接sftp服务器
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;
            //删除旧文件
            if (StringUtil.isNotBlank(path) && path.contains(STATIC)) {
                delete(sftp, path);
            }
            logout(sftp, session);
        } catch (Exception e) {
            log.error("未能删除文件===" + path);
        }
    }

    /**
     * 删除文件
     *
     * @param sftp sftp
     * @param path 文件路径
     */
    private static void delete(ChannelSftp sftp, String path) {
        try {
            path = path.substring(path.lastIndexOf(STATIC));
            String[] dirs = path.split("/");
            int len = dirs.length;
            for (int i = 0; i < len; i++) {
                if (StringUtil.isEmpty(dirs[i])) continue;
                if (i == len - 1) {
                    sftp.rm(dirs[i]);
                } else {
                    sftp.cd(dirs[i]);
                }
            }
        } catch (Exception e) {
            log.error("未能删除文件===" + path);
        }
    }

    /**
     * 文件下载
     *
     * @param directory
     * @param downloadFile
     * @param file
     * @return
     * @throws Exception
     */
    public static File download(String directory, String downloadFile, File file) throws Exception {
        String host;
        if (EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_PRO;
        } else if (EnvironmentEnum.TEST.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_TEST;
        } else if (EnvironmentEnum.DEV.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_DEV;
        } else {
            host = SFTP_HOST_DEV;
//            throw new YimaoException("只有测试和生产环境支持下载文件。");
        }
        //连接sftp服务器
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftp = (ChannelSftp) channel;

        FileOutputStream fos = null;
        try {
            if (StringUtil.isNotEmpty(directory)) {
                sftp.cd(STATIC.substring(1, STATIC.length() - 1));
                sftp.cd(directory);
                fos = new FileOutputStream(file);
                sftp.get(downloadFile, fos);
                fos.flush();
                fos.close();
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            logout(sftp, session);
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 客户端弹窗--文件下载到本地
     *
     * @param directory
     * @param downloadFile
     * @param file
     * @return
     * @throws Exception
     */
    public static boolean downloadLocal(String directory, String downloadFile, File file, HttpServletResponse response) throws Exception {
        String host;
        if (EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_PRO;
        } else if (EnvironmentEnum.TEST.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_TEST;
        } else if (EnvironmentEnum.DEV.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            host = SFTP_HOST_DEV;
        } else {
            host = SFTP_HOST_DEV;
//            throw new YimaoException("只有测试和生产环境支持下载文件。");
        }
        //连接sftp服务器
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + new String(downloadFile.getBytes("gb2312"), "iso8859-1"));
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        ChannelSftp sftp = (ChannelSftp) channel;
        FileOutputStream fos = null;

        InputStream in = null;
        ServletOutputStream os = null;
        try {
            if (StringUtil.isNotEmpty(directory)) {
                sftp.cd(STATIC.substring(1, STATIC.length() - 1));
                sftp.cd(directory);
                fos = new FileOutputStream(file);
                sftp.get(downloadFile, fos);
                in = new FileInputStream(file);
                os = response.getOutputStream();
                int len = 1;
                byte[] b = new byte[10];

                while ((len = in.read(b)) != -1) {
                    os.write(b, 0, len);
                }
                fos.flush();
                fos.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            logout(sftp, session);
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException ignored) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception var20) {
                    log.debug(var20.getMessage(), var20);
                }
            }
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (Exception var19) {
                    log.debug(var19.getMessage(), var19);
                }
            }
        }
    }

}
