package com.zero.itext.common.util;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Map;

public class QrCodeUtils {
    // 程序执行路径
    public static final String dirPath = System.getProperty("user.dir");
    public static final String QR_CODE_IMAGE_PATH = dirPath + "\\src\\main\\resources\\out\\qr\\MyQRCode.png";

    // 利用模板生成pdf
    public static void pdfOut(Map<String, Object> map) {
        // 模板路径
        String templatePath = dirPath + "\\src\\main\\resources\\src\\pdf\\src.pdf";
        // 生成的新文件路径
        String newPDFPath = dirPath + "\\src\\main\\resources\\out\\qr\\qr.pdf";
        PdfReader reader;
        FileOutputStream out;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
        try {
            //给表单添加中文字体 这里采用系统字体。不设置的话，中文可能无法显示
            BaseFont bf = BaseFont.createFont(dirPath + "\\src\\main\\resources\\src\\font\\SIMYOU.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font FontChinese = new Font(bf, 5, Font.NORMAL);

            // 输出流
            out = new FileOutputStream(newPDFPath);

            // 读取pdf模板
            reader = new PdfReader(templatePath);
            bos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, bos);

            AcroFields form = stamper.getAcroFields();
            Map<String, Object> qrcodeFields = (Map<String, Object>) map.get("qrcodeFields");

            //遍历二维码字段
            for (Map.Entry<String, Object> entry : qrcodeFields.entrySet()) {

                String key = entry.getKey();
                Object value = entry.getValue();

                // 获取属性的类型
                if (value != null && form.getField(key) != null) {
                    //获取位置(左上右下)
                    AcroFields.FieldPosition fieldPosition = form.getFieldPositions(key).get(0);

                    //绘制二维码
                    float width = fieldPosition.position.getRight() - fieldPosition.position.getLeft();
                    BarcodeQRCode pdf417 = new BarcodeQRCode(value.toString(), (int) width, (int) width, null);

                    //生成二维码图像
                    Image image128 = pdf417.getImage();

                    //绘制在第一页
                    PdfContentByte cb = stamper.getOverContent(1);

                    //左边距(居中处理)
                    float marginLeft = (fieldPosition.position.getRight() - fieldPosition.position.getLeft() - image128.getWidth()) / 2;

                    //条码位置
                    image128.setAbsolutePosition(fieldPosition.position.getLeft() + marginLeft, fieldPosition.position.getBottom());

                    //加入条码
                    cb.addImage(image128);
                }
            }

            // 如果为false，生成的PDF文件可以编辑，如果为true，生成的PDF文件不可以编辑
            stamper.setFormFlattening(true);
            stamper.close();
            Document doc = new Document();
            Font font = new Font(bf, 20);
            PdfCopy copy = new PdfCopy(doc, out);
            doc.open();
            PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), 1);
            copy.addPage(importPage);
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成二维码图片
     * @param text  二维码内容 内容可以是二维码 也可以是内容
     * @param width 二维码图片宽度
     * @param height    二维码图片高度
     * @param filePath   二维码图片路径
     * @throws WriterException   WriterException
     * @throws IOException   IOException
     */
    public static void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    /**
     * 生成二维码
     *
     * @param contents 二维码的内容
     * @param width    二维码图片宽度
     * @param height   二维码图片高度
     */
    public static BufferedImage createQrCodeBufferdImage(String contents, int width, int height) {
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BufferedImage image = null;
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
            image = toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

}
