package com.zero.itext;

import com.google.zxing.WriterException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.zero.itext.common.model.KeyWordBean;
import com.zero.itext.common.model.SignatureInfo;
import com.zero.itext.common.util.ItextUtils;
import com.zero.itext.common.util.QrCodeUtils;
import com.zero.itext.common.util.WaterMarkUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zero.itext.common.util.ItextUtils.PASSWORD;
import static com.zero.itext.common.util.KeywordPDFUtils.getPDFText;
import static com.zero.itext.common.util.QrCodeUtils.QR_CODE_IMAGE_PATH;

@SpringBootTest
class ZeroItextBootApplicationTests {

    /**
     * 测试电子签章
     */
    @Test
    void testPdfStamp() {
        try {
            // 程序执行路径
            String dirPath = System.getProperty("user.dir");
            // 将证书文件放入指定路径，并读取keystore ，获得私钥和证书链
            String pkPath = "src/main/resources/src/sign/dest.p12";
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(pkPath), PASSWORD);
            String alias = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
            // 得到证书链
            Certificate[] chain = ks.getCertificateChain(alias);
            // 需要进行签章的pdf
            String path = dirPath + "\\src\\main\\resources\\src\\pdf\\src.pdf";
            // 封装签章信息
            SignatureInfo signInfo = new SignatureInfo();
            signInfo.setReason("理由");
            signInfo.setLocation("位置");
            signInfo.setPk(pk);
            signInfo.setChain(chain);
            signInfo.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
            signInfo.setDigestAlgorithm(DigestAlgorithms.SHA1);
            signInfo.setFieldName("demo");

            // 签章图片
            signInfo.setImagePath(dirPath + "\\src\\main\\resources\\src\\pdf\\chapter.png");
            signInfo.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
            //appearance.setVisibleSignature(new Rectangle(280, 220, 140, 600), 1, "sig1");
            // 值越大，代表向x轴坐标平移 缩小 （反之，值越小，印章会放大）
            signInfo.setRectllx(350);
            // 值越大，代表向y轴坐标向上平移（大小不变）
            signInfo.setRectlly(200);
            // 值越大  代表向x轴坐标向右平移  （大小不变）
            signInfo.setRecturx(500);
            // 值越大，代表向y轴坐标向上平移（大小不变）
            signInfo.setRectury(500);
            // 页码
            signInfo.setPage(1);

            //签章后的pdf路径
            ItextUtils.sign(path, dirPath + "\\src\\main\\resources\\out\\pdf\\out.pdf", signInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试PDF提取关键字
     */
    @Test
    void testKeywordPDF() {
        // 获取执行路径
        String path = System.getProperty("user.dir");
        // 获取PDF文本内容
        Map<Integer, List<KeyWordBean>> keyWords = getPDFText(path + "\\src\\main\\resources\\src\\pdf\\src.pdf");
        System.out.println(keyWords.size());
    }

    /**
     * 测试生成二维码图片
     */
    @Test
    void testQRCodeImage() {
        try {
            String qrContent = "https://www.baidu.com/";
            qrContent = "https://www.wolai.com/6MjBCdAq3mmGXBcb62V4DE";
            QrCodeUtils.generateQRCodeImage(qrContent, 350, 350, QR_CODE_IMAGE_PATH);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试二维码签名
     */
    @Test
    void testQRSign() {
        //文本内容map
        Map<String, Object> map = new HashMap<String, Object>();

        //二维码map
        Map<String, Object> qrcodeFields = new HashMap<String, Object>();
        String qrContent = "https://www.baidu.com/";
        qrContent = "https://www.wolai.com/6MjBCdAq3mmGXBcb62V4DE";
        qrcodeFields.put("img", qrContent);

        //组装map传过去
        Map<String, Object> o = new HashMap<String, Object>();
        o.put("qrcodeFields", qrcodeFields);
        //执行
        QrCodeUtils.pdfOut(o);
    }

    /**
     * 测试添加水印
     */
    @Test
    void testWaterMark() {
        // 程序执行路径
        String dirPath = System.getProperty("user.dir");
        String srcPdfPath = dirPath + "\\src\\main\\resources\\src\\pdf\\src.pdf";
        String tarPdfPath = dirPath + "\\src\\main\\resources\\out\\pdf\\WaterMark.pdf";
        String WaterMarkContent = "zero";
        int numberOfPage = 3;
        try {
            WaterMarkUtils.addWaterMark(srcPdfPath, tarPdfPath, WaterMarkContent, numberOfPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
