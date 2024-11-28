package com.zero.itext.common.model;

import lombok.Data;

@Data
public class SignPDFBean {
    // 签章关键字
    private String keyWord;
    // 关键字所在页数
    private int page;
    // 取第n次出现的关键字
    private int num;
    // 印章图片路径
    private String sealPath;
    // 证书文件路径
    private String keyStorePath;
    // 证书密码
    private String keyStorePass;
    // 设置签章原因，可以为空
    private String signReason;
    // 设置签章地点
    private String signLocation;
}
