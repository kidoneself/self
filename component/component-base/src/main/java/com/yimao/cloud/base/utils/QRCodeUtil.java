package com.yimao.cloud.base.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.yimao.cloud.base.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Hashtable;

/**
 * @author Zhang Bo
 * @date 2018/5/16.
 */
@Slf4j
public class QRCodeUtil {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPEG";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 245;
    // LOGO宽度
    private static final int WIDTH = 45;
    // LOGO高度
    private static final int HEIGHT = 45;

    public static void createQrCode(String content, File destFile, String imgPath, boolean needCompress, int x, int y) throws Exception {
        createQrCode(content, destFile, imgPath, needCompress, x, y, 100);
    }

    /**
     * @param content      二维码内容
     * @param destFile     生成的二维码图片文件位置
     * @param imgPath      LOGO图片地址（网络）
     * @param needCompress 是否压缩
     * @return
     * @throws Exception
     */
    private static void createQrCode(String content, File destFile, String imgPath, boolean needCompress, int x, int y, int alpha) throws Exception {

        //设置二维码纠错级别map
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        //创建比特矩阵(位矩阵)的QR码编码的字符串
        BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        log.info("------------------------------------6----------------------------------------------");
        // 使BufferedImage勾画QRCode  (matrixWidth 是行二维码像素点)
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage qrcodeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = qrcodeImage.createGraphics();
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, width, height);
        // 使用比特矩阵画并保存图像
        graphics2D.setColor(Color.BLACK);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                qrcodeImage.setRGB(i, j, bitMatrix.get(i, j) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        graphics2D.dispose();
        log.info("------------------------------------7----------------------------------------------");

        if (StringUtil.isNotEmpty(imgPath)) {
            log.info("------------------------------------8----------------------------------------------");
            Image logoImage = ImageIO.read(new URL(imgPath));
            log.info("------------------------------------9----------------------------------------------");
            if (logoImage != null) {
                log.info("------------------------------------10----------------------------------------------");
                QRCodeUtil.insertLogoImage(qrcodeImage, logoImage, needCompress);
            }
        }
        log.info("------------------------------------11----------------------------------------------");
        File srcFile = new File(Constant.IMAGE_TEMP_FOLDER + "qrcode_template.png");
        if (!srcFile.exists()) {
            log.info("------------------------------------12----------------------------------------------");
            SFTPUtil.download("qrcode", "qrcode_template.png", srcFile);
        }
        if (!srcFile.exists()) {
            log.info("------------------------------------13----------------------------------------------");
            return;
        }

        BufferedImage srcImage = ImageIO.read(srcFile);
        log.info("------------------------------------14----------------------------------------------");
        int watermarkImageWidth = qrcodeImage.getWidth();
        int watermarkImageHeight = qrcodeImage.getHeight();

        Graphics2D graph = srcImage.createGraphics();
        graph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha / 100.0F));
        graph.drawImage(qrcodeImage, x, y, watermarkImageWidth, watermarkImageHeight, null);
        Shape shape = new RoundRectangle2D.Float(x, y, watermarkImageWidth, watermarkImageHeight, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
        log.info("------------------------------------15----------------------------------------------");
        ImageIO.write(srcImage, FORMAT_NAME, destFile);
        log.info("------------------------------------16----------------------------------------------");
        //释放内存
        graphics2D = null;
        graph = null;
        srcImage = null;
        qrcodeImage = null;
    }

    /**
     * 插入LOGO
     *
     * @param qrcodeImage  二维码图片
     * @param logoImage    LOGO图片地址
     * @param needCompress 是否压缩
     * @throws Exception
     */
    private static void insertLogoImage(BufferedImage qrcodeImage, Image logoImage, boolean needCompress) throws Exception {
        int width = logoImage.getWidth(null);
        int height = logoImage.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = logoImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = tag.createGraphics();
            graphics2D.drawImage(image, 0, 0, null); // 绘制缩小后的图
            graphics2D.dispose();
            graphics2D = null;
            logoImage = image;
        }
        // 插入LOGO
        Graphics2D graph = qrcodeImage.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(logoImage, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, height, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
        graph = null;
    }

    /**
     * @param srcFile
     * @param destFile
     * @param watermarkFile
     */
    public static void imageWatermark(File srcFile, File destFile, File watermarkFile, int x, int y) {
        imageWatermark(srcFile, destFile, watermarkFile, x, y, 100);
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
            if (srcFile == null || !srcFile.exists()) {
                logger.error("源文件不存在！");
                return;
            }
            if (watermarkFile == null || !watermarkFile.exists()) {
                logger.error("水印文件不存在！");
                return;
            }
            BufferedImage watermarkImage = ImageIO.read(watermarkFile);
            int watermarkImageWidth = watermarkImage.getWidth();
            int watermarkImageHeight = watermarkImage.getHeight();

            BufferedImage srcImage = ImageIO.read(srcFile);
            Graphics2D graph = srcImage.createGraphics();
            graph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha / 100.0F));
            graph.drawImage(watermarkImage, x, y, watermarkImageWidth, watermarkImageHeight, null);
            Shape shape = new RoundRectangle2D.Float(x, y, watermarkImageWidth, watermarkImageHeight, 6, 6);
            graph.setStroke(new BasicStroke(3f));
            graph.draw(shape);
            graph.dispose();

            ImageIO.write(srcImage, FORMAT_NAME, destFile);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("添加二维码水印时出错！");
            logger.error(e.getMessage(), e);
        }
    }

}
