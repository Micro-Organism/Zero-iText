package com.zero.itext.common.util;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.zero.itext.common.model.KeyWordBean;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.util.*;

public class KeywordPDFUtils {

    // 定义返回页码
    private static int i = 0;
    private static com.itextpdf.awt.geom.Rectangle2D.Float boundingRectange = null;
    private static List<KeyWordBean> lists = null;
    private static Map<Integer, List<KeyWordBean>> map = new HashMap<Integer, List<KeyWordBean>>();
    private static int pageNum = 0;

    public static Map<Integer, List<KeyWordBean>> getPDFText(String filePath) {
        try {
            PdfReader pdfReader = new PdfReader(filePath);
            pageNum = pdfReader.getNumberOfPages();
            PdfReaderContentParser pdfReaderContentParser = new PdfReaderContentParser(pdfReader);

            for (i = 1; i <= pageNum; i++) {
                lists = new ArrayList<KeyWordBean>();
                boundingRectange = new com.itextpdf.awt.geom.Rectangle2D.Float();
                pdfReaderContentParser.processContent(i, new RenderListener() {
                    @Override
                    public void renderText(TextRenderInfo textRenderInfo) {
                        String text = textRenderInfo.getText(); // 整页内容
                        boundingRectange = textRenderInfo.getBaseline().getBoundingRectange();
                        KeyWordBean bean = new KeyWordBean();
                        bean.setX(boundingRectange.x);
                        bean.setY(boundingRectange.y);
                        bean.setPage(i);
                        bean.setText(text);
                        lists.add(bean);
                    }

                    @Override
                    public void renderImage(ImageRenderInfo arg0) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void endTextBlock() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void beginTextBlock() {
                        // TODO Auto-generated method stub

                    }
                });
                map.put(i, lists);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static KeyWordBean getKeyWordXY(Map<Integer, List<KeyWordBean>> map2, int page, int num, String keyWord) {
        int keyMatch = 1;
        StringBuilder content = new StringBuilder();
        List<KeyWordBean> list = map2.get(page);
        Collections.sort(list);//正序比较
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            KeyWordBean bean = list.get(i);
            String text = bean.getText();
            if (i + 1 != list.size()) {
                KeyWordBean beanNext = list.get(i + 1);
                float x = beanNext.getX() - bean.getX();
                float y = beanNext.getY() - bean.getY();
                if (y == 0 && x <= 1) {
                    //	System.out.print(“去除因字体加粗产生的重复数字”);
                } else {
                    if (StringUtils.contains(content.toString(), keyWord) || StringUtils.contains(text, keyWord)) {
                        if (keyMatch == num) {
                            return bean;
                        } else {
                            keyMatch++;
                        }
                    } else if ((!StringUtils.isEmpty(text) && keyWord.startsWith(text)) || content.length() > 0) {
                        content.append(text);
                        if (content.length() >= keyWord.length()) {
                            if (StringUtils.contains(content.toString(), keyWord)) {
                                if (keyMatch == num) {
                                    return bean;
                                } else {
                                    keyMatch++;
                                }
                            }
                            content = new StringBuilder();
                        }
                    }
                }
            } else {
                if (StringUtils.contains(content.toString(), keyWord) || StringUtils.contains(text, keyWord)) {
                    if (keyMatch == num) {
                        return bean;
                    } else {
                        keyMatch++;
                    }
                } else if ((!StringUtils.isEmpty(text) && keyWord.startsWith(text)) || content.length() > 0) {
                    content.append(text);
                    if (content.length() >= keyWord.length()) {
                        if (StringUtils.contains(content.toString(), keyWord)) {
                            if (keyMatch == num) {
                                return bean;
                            } else {
                                keyMatch++;
                            }
                        }
                        content = new StringBuilder();
                    }
                }
            }
        }
        return null;
    }

    public static KeyWordBean getKeyWordXY1(Map<Integer, List<KeyWordBean>> map2, String keyWord) {
        StringBuilder content = new StringBuilder();
        for (int i = 1; i <= pageNum; i++) {
            List<KeyWordBean> list = map2.get(i);
            Collections.sort(list);//正序比较
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < list.size(); j++) {
                KeyWordBean bean = list.get(j);
                String text = bean.getText();
                if (j + 1 != list.size()) {
                    KeyWordBean beanNext = list.get(j + 1);
                    float x = beanNext.getX() - bean.getX();
                    float y = beanNext.getY() - bean.getY();
                    if (y == 0 && x <= 1) {
                        //	System.out.print(“去除因字体加粗产生的重复数字”);
                    } else {
                        if (StringUtils.contains(content.toString(), keyWord) || StringUtils.contains(text, keyWord)) {
                            return bean;
                        } else if ((!StringUtils.isEmpty(text) && keyWord.startsWith(text)) || content.length() > 0) {
                            content.append(text);
                            if (content.length() >= keyWord.length()) {
                                if (StringUtils.contains(content.toString(), keyWord)) {
                                    return bean;
                                }
                                content = new StringBuilder();
                            }
                        }
                    }
                } else {
                    if (StringUtils.contains(content.toString(), keyWord) || StringUtils.contains(text, keyWord)) {
                        return bean;
                    } else if ((!StringUtils.isEmpty(text) && keyWord.startsWith(text)) || content.length() > 0) {
                        content.append(text);
                        if (content.length() >= keyWord.length()) {
                            if (StringUtils.contains(content.toString(), keyWord)) {
                                return bean;
                            }
                            content = new StringBuilder();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static KeyWordBean getKeyWordBean(KeyWordBean bean, StringBuilder content, String keyWord, String text, int keyMatch, int num) {
        if (StringUtils.contains(content.toString(), keyWord) || StringUtils.contains(text, keyWord)) {
            if (keyMatch == num) {
                return bean;
            } else {
                keyMatch++;
            }
        } else if ((!StringUtils.isEmpty(text) && keyWord.startsWith(text)) || content.length() > 0) {
            content.append(text);
            if (content.length() >= keyWord.length()) {
                if (StringUtils.contains(content.toString(), keyWord)) {
                    if (keyMatch == num) {
                        return bean;
                    } else {
                        keyMatch++;
                    }
                }
                content = new StringBuilder();
            }
        }
        return null;
    }


}
