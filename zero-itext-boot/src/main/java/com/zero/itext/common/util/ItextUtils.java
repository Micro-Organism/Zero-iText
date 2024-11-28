package com.zero.itext.common.util;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.*;
import com.zero.itext.common.model.SignatureInfo;

import java.io.*;

/**
 * @author Micro-Organism
 */
public class ItextUtils {

    // keystore密码
    public static final char[] PASSWORD = "111111".toCharArray();

    /**
     * 单多次签章通用
     *
     * @param src   源文件
     * @param target    目标文件
     * @param signatureInfo 签章信息
     */
    @SuppressWarnings("resource")
    public static void sign(String src, String target, SignatureInfo signatureInfo) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            inputStream = new FileInputStream(src);
            ByteArrayOutputStream tempArrayOutputStream = new ByteArrayOutputStream();
            PdfReader reader = new PdfReader(inputStream);
            // 创建签章工具PdfStamper ，最后一个boolean参数是否允许被追加签名
            // false的话，pdf文件只允许被签名一次，多次签名，最后一次有效
            // true的话，pdf可以被追加签名，验签工具可以识别出每次签名之后文档是否被修改
            PdfStamper stamper = PdfStamper.createSignature(reader, tempArrayOutputStream, '\0', null, true);
            // 获取数字签章属性对象
            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
            appearance.setReason(signatureInfo.getReason());
            appearance.setLocation(signatureInfo.getLocation());

            // 设置签名的位置，页码，签名域名称，多次追加签名的时候，签名预名称不能一样 图片大小受表单域大小影响（过小导致压缩）
            // 签名的位置，是图章相对于pdf页面的位置坐标，原点为pdf页面左下角
            // 四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
            appearance.setVisibleSignature(new Rectangle(signatureInfo.getRectllx(), signatureInfo.getRectlly(), signatureInfo.getRecturx(), signatureInfo.getRectury()), signatureInfo.getPage(), "sig1");

            // 读取图章图片
            Image image = Image.getInstance(signatureInfo.getImagePath());
            appearance.setSignatureGraphic(image);
            appearance.setCertificationLevel(signatureInfo.getCertificationLevel());
            // 设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
            appearance.setRenderingMode(signatureInfo.getRenderingMode());
            // 这里的itext提供了2个用于签名的接口，可以自己实现，后边着重说这个实现
            // 摘要算法
            ExternalDigest digest = new BouncyCastleDigest();
            // 签名算法
            ExternalSignature signature = new PrivateKeySignature(signatureInfo.getPk(), signatureInfo.getDigestAlgorithm(), null);
            // 调用itext签名方法完成pdf签章 //数字签名格式，CMS,CADE
            MakeSignature.signDetached(appearance, digest, signature, signatureInfo.getChain(), null, null, null, 0, MakeSignature.CryptoStandard.CADES);

            inputStream = new ByteArrayInputStream(tempArrayOutputStream.toByteArray());
            // 定义输入流为生成的输出流内容，以完成多次签章的过程
            result = tempArrayOutputStream;

            outputStream = new FileOutputStream(new File(target));
            outputStream.write(result.toByteArray());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != result) {
                    result.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
