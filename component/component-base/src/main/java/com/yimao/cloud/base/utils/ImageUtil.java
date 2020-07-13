package com.yimao.cloud.base.utils;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.exception.YimaoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 图片保存公共类
 *
 * @author liuhao@yimaokeji.com
 */
@Slf4j
public class ImageUtil {

    // 水印位置（无、左上、右上、居中、左下、右下）
    public enum WatermarkPosition {
        no, topLeft, topRight, center, bottomLeft, bottomRight
    }

    // /**
    //  * 删除图片
    //  *
    //  * @return
    //  */
    // public static Object deleteImage(
    //         @RequestParam(required = false) String path,
    //         @RequestParam(required = false) String fileName) {
    //     try {
    //         System.out.println(path + fileName);
    //         FileUtil.cleanFile(path, fileName);
    //         return CommonResult.build(400, "删除失败");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return CommonResult.build(500, "操作失败，请稍后重试！");
    //     }
    // }

    /**
     * 保存图片
     *
     * @param request
     * @param filePath         文件路径
     * @param saveFilePathName 保存路径
     * @param saveFileName     名称
     * @param extendes         不允许的扩展名
     * @return
     * @throws IOException
     */
    public static Map saveFileToServer(HttpServletRequest request, String filePath, String saveFilePathName, String saveFileName, String[] extendes) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        List<MultipartFile> file2 = multipartRequest.getFiles(filePath);

//        //CommonsMultipartFile file3 = (CommonsMultipartFile) multipartRequest.getFile(filePath);
//        MultipartFile file = multipartRequest.getFile(filePath);

        Map<Object, Map<Object, Object>> bigMap = new HashMap<>();
//        StringBuffer buffer=new StringBuffer();
        Map<Object, Object> map = new HashMap<>();
        for (MultipartFile file : file2) {
            if (file != null && !file.isEmpty()) {
                //扩展名
                String extend = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
                if (saveFileName == null || saveFileName.trim().equals("")) {
                    //没有指定图片名称，自动生成
                    saveFileName = UUID.randomUUID().toString() + "." + extend;
                }
                if (saveFileName.lastIndexOf(".") < 0) {
                    saveFileName = saveFileName + "." + extend;
                }
                //图片大小
                float fileSize = (float) file.getSize();
                List<String> errors = new ArrayList<>();
                boolean flag = true;
                if (extendes != null) {
                    for (String s : extendes) {
                        if (Objects.equals(extend.toLowerCase(), s))
                            flag = true;
                    }
                }
                //路径不存在自动生成
                if (flag) {
                    File path = new File(saveFilePathName);
                    if (!path.exists()) {
                        path.mkdir();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    DataOutputStream out = new DataOutputStream(new FileOutputStream(saveFilePathName + File.separator + saveFileName));
                    InputStream is = null;
                    try {
                        //上传操作
                        is = file.getInputStream();
                        int size = (int) fileSize;
                        byte[] buffer = new byte[size];
                        while (is.read(buffer) > 0) {
                            out.write(buffer);
                        }

                    } catch (IOException exception) {
                        exception.printStackTrace();
                    } finally {
                        //流关闭
                        if (is != null) {
                            is.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                    }
                    //如果是图片拓展名
                    if (isImg(extend)) {
                        File img = new File(saveFilePathName + File.separator + saveFileName);
                        try {
                            BufferedImage bis = ImageIO.read(img);
                            int w = bis.getWidth();
                            int h = bis.getHeight();
                            map.put("width", w);
                            map.put("height", h);
                        } catch (Exception localException) {
                        }
                    }
                    map.put("mime", extend);
                    map.put("fileName", saveFileName);
                    map.put("fileSize", fileSize);
                    map.put("error", errors);
                    map.put("oldName", file.getOriginalFilename());
                } else {
                    errors.add("不允许的扩展名");
                }

            } else {
                map.put("width", 0);
                map.put("height", 0);
                map.put("mime", "");
                map.put("fileName", saveFileName);
                map.put("fileSize", 0.0F);
                map.put("oldName", file.getOriginalFilename());
            }
            bigMap.put(file.getOriginalFilename(), map);
            saveFileName = "";
        }
        return bigMap;
    }


    /**
     * 扩展名
     *
     * @param extend
     * @return
     */
    public static boolean isImg(String extend) {
        boolean ret = false;
        List<String> list = new ArrayList<>();
        list.add("jpg");
        list.add("jpeg");
        list.add("bmp");
        list.add("gif");
        list.add("png");
        list.add("tif");
        for (String s : list) {
            if (Objects.equals(s, extend))
                ret = true;
        }
        return ret;
    }


    /**
     * 图片缩放(图片等比例缩放为指定大小，空白部分以白色填充)
     *
     * @param srcBufferedImage 源图片
     * @param destFile         缩放后的图片文件
     */
    public static void zoom(BufferedImage srcBufferedImage, File destFile, int destHeight, int destWidth) {
        try {
            int imgWidth = destWidth;
            int imgHeight = destHeight;
            int srcWidth = srcBufferedImage.getWidth();
            int srcHeight = srcBufferedImage.getHeight();
            if (srcHeight >= srcWidth) {
                imgWidth = (int) Math.round(destHeight * 1.0 / srcHeight * srcWidth);
            } else {
                imgHeight = (int) Math.round(destWidth * 1.0 / srcWidth * srcHeight);
            }
            BufferedImage destBufferedImage = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = destBufferedImage.createGraphics();
            graphics2D.setBackground(Color.WHITE);
            graphics2D.clearRect(0, 0, destWidth, destHeight);
            graphics2D.drawImage(srcBufferedImage.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH), destWidth / 2 - imgWidth / 2, destHeight / 2 - imgHeight / 2, null);
            graphics2D.dispose();
            ImageIO.write(destBufferedImage, "JPEG", destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param srcFile
     * @param destFile
     * @param watermarkFile
     */
    public static void imageWatermark(File srcFile, File destFile, File watermarkFile, int x, int y) {
        ImageUtil.imageWatermark(srcFile, destFile, watermarkFile, x, y, 100);
    }

    /**
     * 添加图片水印
     *
     * @param srcFile       处理前的图片文件
     * @param destFile      处理后的图片文件
     * @param watermarkFile 水印图片文件
     * @param x             水印图片水平偏移量
     * @param y             水印图片垂直偏移量
     * @param alpha         透明度
     */
    private static void imageWatermark(File srcFile, File destFile, File watermarkFile, int x, int y, int alpha) {
        try {
            if (srcFile != null && srcFile.exists()) {
                BufferedImage srcBufferedImage = ImageIO.read(srcFile);
                int srcWidth = srcBufferedImage.getWidth();
                int srcHeight = srcBufferedImage.getHeight();
                BufferedImage destBufferedImage = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = destBufferedImage.createGraphics();
                graphics2D.setBackground(Color.WHITE);
                graphics2D.clearRect(0, 0, srcWidth, srcHeight);
                graphics2D.drawImage(srcBufferedImage.getScaledInstance(srcWidth, srcHeight, Image.SCALE_SMOOTH), 0, 0, null);

                if (watermarkFile != null && watermarkFile.exists()) {
                    BufferedImage watermarkBufferedImage = ImageIO.read(watermarkFile);
                    int watermarkImageWidth = watermarkBufferedImage.getWidth();
                    int watermarkImageHeight = watermarkBufferedImage.getHeight();
                    graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha / 100.0F));
                    graphics2D.drawImage(watermarkBufferedImage, x, y, watermarkImageWidth, watermarkImageHeight, null);
                }
                graphics2D.dispose();
                ImageIO.write(destBufferedImage, "JPEG", destFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片缩放并添加图片水印(图片等比例缩放为指定大小，空白部分以白色填充)
     *
     * @param srcBufferedImage  需要处理的图片
     * @param destFile          处理后的图片文件
     * @param watermarkFile     水印图片文件
     * @param watermarkPosition 水印位置
     * @param alpha             透明度
     */
    public static void zoomAndWatermark(BufferedImage srcBufferedImage, File destFile, int destHeight, int destWidth, File watermarkFile, WatermarkPosition watermarkPosition, int alpha) {
        try {
            int imgWidth = destWidth;
            int imgHeight = destHeight;
            int srcWidth = srcBufferedImage.getWidth();
            int srcHeight = srcBufferedImage.getHeight();
            if (srcHeight >= srcWidth) {
                imgWidth = (int) Math.round(destHeight * 1.0 / srcHeight * srcWidth);
            } else {
                imgHeight = (int) Math.round(destWidth * 1.0 / srcWidth * srcHeight);
            }

            BufferedImage destBufferedImage = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = destBufferedImage.createGraphics();
            graphics2D.setBackground(Color.WHITE);
            graphics2D.clearRect(0, 0, destWidth, destHeight);
            graphics2D.drawImage(srcBufferedImage.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH), destWidth / 2 - imgWidth / 2, destHeight / 2 - imgHeight / 2, null);
            if (watermarkFile != null && watermarkFile.exists() && watermarkPosition != null && watermarkPosition != WatermarkPosition.no) {
                BufferedImage watermarkBufferedImage = ImageIO.read(watermarkFile);
                int watermarkImageWidth = watermarkBufferedImage.getWidth();
                int watermarkImageHeight = watermarkBufferedImage.getHeight();
                graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha / 100.0F));
                int x = 0;
                int y = 0;
                if (watermarkPosition == WatermarkPosition.topLeft) {
                    x = 0;
                    y = 0;
                } else if (watermarkPosition == WatermarkPosition.topRight) {
                    x = destWidth - watermarkImageWidth;
                    y = 0;
                } else if (watermarkPosition == WatermarkPosition.center) {
                    x = (destWidth - watermarkImageWidth) / 2;
                    y = (destHeight - watermarkImageHeight) / 2;
                } else if (watermarkPosition == WatermarkPosition.bottomLeft) {
                    x = 0;
                    y = destHeight - watermarkImageHeight;
                } else if (watermarkPosition == WatermarkPosition.bottomRight) {
                    x = destWidth - watermarkImageWidth;
                    y = destHeight - watermarkImageHeight;
                }
                graphics2D.drawImage(watermarkBufferedImage, x, y, watermarkImageWidth, watermarkImageHeight, null);
            }
            graphics2D.dispose();
            ImageIO.write(destBufferedImage, "JPEG", destFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片文件的类型.
     *
     * @param imageFile 图片文件对象.
     * @return 图片文件类型
     */
    public static String getImageFormatName(File imageFile) {
        try {
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(imageFile);
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);
            if (!iterator.hasNext()) {
                return null;
            }
            ImageReader imageReader = iterator.next();
            imageInputStream.close();
            return imageReader.getFormatName().toLowerCase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    /**
//     * 将图片写入到磁盘
//     *
//     * @param img      图片数据流
//     * @param fileName 文件保存时的名称
//     */
//    public static File writeImageToDisk(String imgUrl, String folder, String fileName) {
//        try {
//            byte[] img = getImageFromNetByUrl(imgUrl);
//            File file = new File(folder + fileName);
//            FileOutputStream fops = new FileOutputStream(file);
//            fops.write(img);
//            fops.flush();
//            fops.close();
//            return file;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

//    /**
//     * 根据地址获得数据的字节流
//     *
//     * @param imgUrl 网络连接地址
//     * @return
//     */
//    private static byte[] getImageFromNetByUrl(String imgUrl) throws Exception {
//        URL url = new URL(imgUrl);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//        conn.setConnectTimeout(5 * 1000);
//        InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
//        return readInputStream(inStream);
//    }
//
//    /**
//     * 从输入流中获取数据
//     *
//     * @param inStream 输入流
//     * @return
//     * @throws Exception
//     */
//    private static byte[] readInputStream(InputStream inStream) throws Exception {
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[1024];
//        int len = 0;
//        while ((len = inStream.read(buffer)) != -1) {
//            outStream.write(buffer, 0, len);
//        }
//        inStream.close();
//        return outStream.toByteArray();
//    }

    /**
     * 将图片写入到磁盘
     *
     * @param img      图片数据流
     * @param fileName 文件保存时的名称
     */
    public static File writeImageToDisk(String imgUrl, String folder, String fileName) {
        try {
            BufferedImage srcImg = ImageIO.read(new URL(imgUrl));
            File file = new File(folder + fileName);
            FileOutputStream fops = new FileOutputStream(file);
            ImageIO.write(srcImg, "PNG", fops);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 为小程序码写入头像logo
     *
     * @param wxacodeFile
     * @param headImgUrl
     * @throws IOException
     */
    public static void writeLogoToWxacode(File wxacodeFile, String headImgUrl) throws IOException {
        URL url;
        try {
            url = new URL(headImgUrl);
        } catch (IOException e) {
            log.error("生成小程序码-为小程序码设置用户头像时出错-头像URL={}不可用，将使用默认头像。", headImgUrl);
            try {
                if (EnvironmentEnum.LOCAL.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
                    headImgUrl = "http://192.168.10.63" + Constant.DEFAULT_HEADIMAGE;
                } else {
                    headImgUrl = "https://wxtest.yimaokeji.com" + Constant.DEFAULT_HEADIMAGE;
                }
                url = new URL(headImgUrl);
            } catch (IOException e1) {
                log.error("生成小程序码-使用默认头像为小程序码设置用户头像时出错。");
                return;
            }
        }
        BufferedImage avatarImage = ImageIO.read(url);
        if (avatarImage != null) {
            int width = 200; // 透明底的图片
            BufferedImage formatAvatarImage = new BufferedImage(width, width, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics = formatAvatarImage.createGraphics();
            //把图片切成一个圓
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //留一个像素的空白区域，这个很重要，画圆的时候把这个覆盖
            int border = 1;
            //图片是一个圆型
            Ellipse2D.Double shape = new Ellipse2D.Double(border, border, width - border * 2, width - border * 2);
            //需要保留的区域
            graphics.setClip(shape);
            graphics.drawImage(avatarImage, border, border, width - border * 2, width - border * 2, null);
            graphics.dispose();

            //在圆图外面再画一个圆
            //新创建一个graphics，这样画的圆不会有锯齿
            graphics = formatAvatarImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            border = 3;
            //画笔是4.5个像素，BasicStroke的使用可以查看下面的参考文档
            //使画笔时基本会像外延伸一定像素，具体可以自己使用的时候测试
            Stroke s = new BasicStroke(4.5F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(s);
            graphics.setColor(Color.WHITE);
            graphics.drawOval(border, border, width - border * 2, width - border * 2);
            graphics.dispose();
            BufferedImage srcImg = ImageIO.read(wxacodeFile);
            if (srcImg == null) {
                throw new YimaoException("为小程序码写入头像logo时出错，图片文件为空。" + wxacodeFile.exists());
            }
            //scrImg加载完之后没有任何颜色
            BufferedImage blankImage = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_INT_RGB);
            graphics = blankImage.createGraphics();
            graphics.drawImage(srcImg, 0, 0, null);
            int x = (blankImage.getWidth() - width) / 2;
            int y = (blankImage.getHeight() - width) / 2;
            graphics.drawImage(formatAvatarImage, x, y, width, width, null);
            graphics.dispose();
            ImageIO.write(blankImage, "PNG", wxacodeFile);

            graphics = null;
            formatAvatarImage = null;
            avatarImage = null;
            srcImg = null;
            blankImage = null;
        }
    }

}
