package com.zero.itext.common.util;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.UUID;

public class SignPDFUtils {

    /**
     * @param password     秘钥密码
     * @param keyStorePath 秘钥文件路径
     * @param signImage    签名图片文件
     * @param x            x坐标
     * @param y            y坐标
     * @return  字节
     */
    public static byte[] sign(String password, String keyStorePath, InputStream inputStream, String signImage, float x, float y, int page, String reason, String location) {
        PdfReader reader = null;
        ByteArrayOutputStream signPDFData = null;
        PdfStamper stp = null;
        FileInputStream fos = null;
        try {
            BouncyCastleProvider provider = new BouncyCastleProvider();
            Security.addProvider(provider);
            KeyStore ks = KeyStore.getInstance("PKCS12", new BouncyCastleProvider());
            // 私钥密码 为Pkcs生成证书是的私钥密码 123456
            fos = new FileInputStream(keyStorePath);
            ks.load(fos, password.toCharArray());
            String alias = (String) ks.aliases().nextElement();
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
            Certificate[] chain = ks.getCertificateChain(alias);
            reader = new PdfReader(inputStream);
            signPDFData = new ByteArrayOutputStream();
            stp = PdfStamper.createSignature(reader, signPDFData, '\0', null, true);
            stp.setFullCompression();
            PdfSignatureAppearance sap = stp.getSignatureAppearance();
            sap.setReason(reason);
            sap.setLocation(location);
            Image image = Image.getInstance(signImage);
            sap.setImageScale(0);
            sap.setSignatureGraphic(image);
            sap.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
            sap.setVisibleSignature(new Rectangle(x - 30, y - 15, x + 185, y + 68), page, UUID.randomUUID().toString().replaceAll("-", ""));
            stp.getWriter().setCompressionLevel(5);
            ExternalDigest digest = new BouncyCastleDigest();
            ExternalSignature signature = new PrivateKeySignature(privateKey, DigestAlgorithms.SHA512, provider.getName());
            MakeSignature.signDetached(sap, digest, signature, chain, null, null, null, 0, MakeSignature.CryptoStandard.CADES);
            reader.close();
            return signPDFData.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (signPDFData != null) {
                try {
                    signPDFData.close();
                } catch (IOException e) {

                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {

                }
            }
        }
        return null;
    }

}
