package com.zero.itext.common.model;

import lombok.Data;

import java.util.List;

@Data
public class SignPDFRequestBean {

    // 待签章pdf文件路径
    private String srcPDFPath;
    // 签章后输出的pdf文件路径
    private String outPDFPath;
    // 签章信息
    private List<SignPDFBean> SignPDFBeans;
}
