package com.zero.itext.common.model;

import com.itextpdf.text.pdf.PdfSignatureAppearance;
import lombok.Data;

import java.security.PrivateKey;
import java.security.cert.Certificate;

@Data
public class SignatureInfo {
    //签名的原因，显示在pdf签名属性中
    private String reason;
    //签名的地点，显示在pdf签名属性中
    private String location;
    //摘要算法名称，例如SHA-1
    private String digestAlgorithm;
    //图章路径
    private String imagePath;
    //表单域名称
    private String fieldName;
    //证书链
    private Certificate[] chain;
    //签名私钥
    private PrivateKey pk;
    //批准签章
    private int certificationLevel = 0;
    //表现形式：仅描述，仅图片，图片和描述，签章者和描述
    private PdfSignatureAppearance.RenderingMode renderingMode;
    //图章属性
    //图章左下角x
    private float rectllx;
    //图章左下角y
    private float rectlly;
    //图章右上角x
    private float recturx;
    //图章右上角y
    private float rectury;
    //页码
    private int page;
}

