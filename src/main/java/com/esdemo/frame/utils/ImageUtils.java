package com.esdemo.frame.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 激活码工具类
 */
@Slf4j
public class ImageUtils {

    //二维码颜色
    private static final int BLACK = 0xFF000000;
    //二维码颜色
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * 生成带背景二维码图片
     *
     * @param text    二维码内容 例：https://www.baidu.com
     * @param width   二维码宽度
     * @param height  二维码高度
     * @param bigPath 背景图路径
     * @param x       二维码距离左边像素
     * @param y       二维码距离顶部像素
     * @return 2018年11月23日
     */
    public static void zxingCodeCreateImage(String text, int width, int height, String bigPath, String x, String y) {
        Map<EncodeHintType, String> his = new HashMap<>();
        //设置编码字符集
        his.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            //1、生成二维码
            BitMatrix qrCode = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, his);
            //去白边
            BufferedImage image = deleteWhite(qrCode);
            mergeImage(bigPath, image, x, y);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("二维码图片生成失败");
        }
    }

    /**
     * 二维码去除白边
     *
     * @param matrix
     * @return
     */
    public static BufferedImage deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1]))
                    resMatrix.set(i, j);
            }
        }

        int width = resMatrix.getWidth();
        int height = resMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, resMatrix.get(x, y) ? BLACK
                        : WHITE);
            }
        }
        return image;
    }

    /**
     * 图片切圆角
     *
     * @param srcImage
     * @param radius
     * @return
     */
    public static BufferedImage setClip(BufferedImage srcImage, int radius) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gs = image.createGraphics();

        gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gs.setClip(new RoundRectangle2D.Double(0, 0, width, height, radius, radius));
        gs.drawImage(srcImage, 0, 0, null);
        gs.dispose();
        return image;
    }

    /**
     * 将二维码绘图到图片中
     *
     * @param bigPath
     * @param small
     * @param x
     * @param y
     * @throws IOException 2018年11月23日
     */
    public static void mergeImage(String bigPath, BufferedImage small, String x, String y) throws IOException {
        try {
            BufferedImage big = ImageIO.read(new File(bigPath));
            Graphics2D g = big.createGraphics();
            float fx = Float.parseFloat(x);
            float fy = Float.parseFloat(y);
            int x_i = (int) fx;
            int y_i = (int) fy;
            g.drawImage(small, x_i, y_i, small.getWidth(), small.getHeight(), null);
            g.dispose();
            ImageIO.write(big, "png", new File(bigPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
