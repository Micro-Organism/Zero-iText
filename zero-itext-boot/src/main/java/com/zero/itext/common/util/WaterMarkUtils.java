package com.zero.itext.common.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.FileOutputStream;

public class WaterMarkUtils {

    /**
     * pdf生成水印
     *
     * @param srcPdfPath       插入前的文件路径
     * @param tarPdfPath       插入后的文件路径
     * @param WaterMarkContent 水印文案
     * @param numberOfPage     每页需要插入的条数
     */
    public static void addWaterMark(String srcPdfPath, String tarPdfPath, String WaterMarkContent, int numberOfPage) throws Exception {
        PdfReader reader = new PdfReader(srcPdfPath);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(tarPdfPath));
        PdfGState gs = new PdfGState();
        System.out.println("adksjskalfklsdk");
        //设置字体
        BaseFont font = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

        // 设置透明度
        gs.setFillOpacity(0.4f);

        int total = reader.getNumberOfPages() + 1;
        PdfContentByte content;
        for (int i = 1; i < total; i++) {
            content = stamper.getOverContent(i);
            content.beginText();
            content.setGState(gs);
            //水印颜色
            content.setColorFill(BaseColor.DARK_GRAY);
            //水印字体样式和大小
            content.setFontAndSize(font, 35);
            //插入水印  循环每页插入的条数
            for (int j = 0; j < numberOfPage; j++) {
                content.showTextAligned(Element.ALIGN_CENTER, WaterMarkContent, 300, 200 * (j + 1), 30);
            }
            content.endText();
        }
        stamper.close();
        reader.close();

//        //暂时不删除源文件
//        boolean b = deleteFile(srcPdfPath);

        System.out.println("PDF水印添加完成！");
    }

    /**
     * 删除没有用的文件
     * @param path  路径
     * @return  boolean
     */
    public static boolean deleteFile(String path) {
        boolean result = false;
        File file = new File(path);
        if (file.isFile() && file.exists()) {
            int tryCount = 0;
            while (!result && tryCount++ < 10) {
                System.gc();
                result = file.delete();
            }
        }

        return result;
    }


}
