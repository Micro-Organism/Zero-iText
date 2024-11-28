package com.zero.itext.service.impl;


import com.zero.itext.common.model.KeyWordBean;
import com.zero.itext.common.model.SignPDFBean;
import com.zero.itext.common.model.SignPDFRequestBean;
import com.zero.itext.common.util.EncryptPDFUtil;
import com.zero.itext.common.util.KeywordPDFUtils;
import com.zero.itext.common.util.SignPDFUtils;
import com.zero.itext.service.SignService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;


@Service
public class SignServiceImpl implements SignService {

    // 程序执行路径
    String dirPath = System.getProperty("user.dir");

    @Override
    public String uploadSign(String img) {
        //String idCard="3000";
        // 生成jpeg图片 idCard.asString()
        String url = dirPath + "\\src\\main\\resources\\src\\img\\sign.png";

        try {
            // 图像数据为空
            if (img == null) {
                return "no";
            }
            //获取前缀data:image/gif;base64,的坐标
            int i = img.indexOf("base64,") + 7;
            //去除前缀
            String newImage = img.substring(i, img.length());

            // Base64解码
            byte[] bytes = Base64.getDecoder().decode(newImage);

            for (int j = 0; j < bytes.length; ++j) {
                // 调整异常数据
                if (bytes[j] < 0) {
                    bytes[j] += 256;
                }
            }

            OutputStream out = new FileOutputStream(url);

            out.write(bytes);
            out.flush();
            out.close();
            return "yes";
        } catch (Exception e) {
            return "no";
        }
    }

    @Override
    public String sign() {
            try {
                //初始化文件
                String srcPath = dirPath + "\\src\\main\\resources\\src\\pdf\\src.pdf";
                //输出文件
                String outPath = dirPath + "\\src\\main\\resources\\out\\pdf\\signOut.pdf";
                //添加关键字
                signImg(dirPath +  "\\src\\main\\resources\\src\\img\\sign.png", srcPath, "批准人", outPath);
                //加密
                EncryptPDFUtil.encryptPDF(outPath, "EncryptPDF");
                return "yes";
            } catch (Exception e) {
                return "no";
            }
    }

    public static void signImg(String imgurl, String pdfurl, String keywords, String outPDFPath) throws IOException {
        List list = new ArrayList();
        SignPDFBean bean1 = new SignPDFBean();
        bean1.setKeyStorePass("111111");
        bean1.setKeyStorePath("src/main/resources/src/sign/dest.p12");
        bean1.setKeyWord(keywords);
        bean1.setSealPath(imgurl);
        bean1.setSignLocation(keywords);
        bean1.setSignReason("Zero签字");
        list.add(bean1);

        SignPDFRequestBean requestBean = new SignPDFRequestBean();
        requestBean.setSrcPDFPath(pdfurl);
        requestBean.setOutPDFPath(outPDFPath);
        requestBean.setSignPDFBeans(list);

        long startTime = System.currentTimeMillis();
        // 1.解析pdf文件
        Map<Integer, List<KeyWordBean>> map = KeywordPDFUtils.getPDFText(requestBean.getSrcPDFPath());
        // 2.获取关键字坐标
        List<SignPDFBean> beans = requestBean.getSignPDFBeans();
        byte[] fileData = null;
        InputStream in = null;
        for (int i = 0; i < beans.size(); i++) {
            SignPDFBean pdfBean = beans.get(i);
            KeyWordBean bean = KeywordPDFUtils.getKeyWordXY1(map, pdfBean.getKeyWord());
            if (null == bean) {
                System.out.println("未查询到关键字。。。");
            }
            System.out.println("111" + bean.toString());
            long keyTime = System.currentTimeMillis();
            if (i == 0) {
                in = new FileInputStream(requestBean.getSrcPDFPath());
            } else {
                in = new ByteArrayInputStream(fileData);
            }
            // 3.进行盖章
            fileData = SignPDFUtils.sign(pdfBean.getKeyStorePass(), pdfBean.getKeyStorePath(), in, pdfBean.getSealPath(), bean.getX(), bean.getY(), bean.getPage(), pdfBean.getSignReason(), pdfBean.getSignLocation());
            long signTime = System.currentTimeMillis();
        }
        // 4.输出盖章后pdf文件
        FileOutputStream f = new FileOutputStream(new File(requestBean.getOutPDFPath()));
        f.write(fileData);
        f.close();
        in.close();

        long endTime = System.currentTimeMillis();
        System.out.println("总时间：" + (endTime - startTime));
    }

}
