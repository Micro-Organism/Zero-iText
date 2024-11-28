<h1>Zero-iText-Boot</h1>


# 1. 概述
> Zero-iText-Boot 是一个基于 Spring Boot 的 iText 生成 PDF 的项目，它提供了一些常用的 PDF 生成功能，
> 包括生成简单的 PDF 文档、添加图片、添加表格、添加水印等。 通过该项目，您可以快速地生成 PDF 文档，并将其用于各种场景中。

# 2. 功能
## 2.1. 使用itextPDF实现PDF电子公章工具类
### 2.1.1. 电子公章的制作
> 做章网站：http://seal.biaozhiku.com/
### 2.1.2. 依赖文件
```xml
<dependencies>
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>itextpdf</artifactId>
        <version>5.5.13.2</version>
    </dependency>
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>itext-asian</artifactId>
        <version>5.2.0</version>
    </dependency>
    <dependency>
        <groupId>org.bouncycastle</groupId>
        <artifactId>bcprov-jdk15on</artifactId>
        <version>1.68</version>
    </dependency>
    <dependency>
        <groupId>org.bouncycastle</groupId>
        <artifactId>bcpkix-jdk15on</artifactId>
        <version>1.68</version>
    </dependency>
</dependencies>
```
### 2.1.3. 相关配置及数字签名的配置
1. 涉及到加密算法就需要数字签名了（数字签名格式，CMS,CADE），我们就需要一个文件（我的命名是：server.p12）这个东西需要我们自己电脑生成数字签名

2. java工具keytool生成p12数字证书文件
```shell
# 生成数字文件，在命令行输入
keytool -genkeypair -alias dest -keypass 111111 -storepass 111111 -keyalg RSA -keysize 2048 -validity 3650 -keystore D:\files\keystore\src.keystore -dname "C=CN,ST=SD,L=QD,O=haier,OU=dev,CN=haier.com"
# 转换为p12格式
keytool -importkeystore -srckeystore D:\files\keystore\src.keystore -destkeystore D:\files\keystore\dest.p12 -srcalias dest -destalias serverkey -srcstoretype jks -deststoretype pkcs12 -srcstorepass 111111 -deststorepass 111111 -noprompt

# 生成数字文件，在命令行输入
keytool -genkeypair -alias zero -keypass 111111 -storepass 111111 -keyalg RSA -keysize 2048 -storetype PKCS12 -validity 3650 -keystore D:\files\keystore\zero.p12 -dname "C=CN,ST=SD,L=QD,O=haier,OU=dev,CN=haier.com"
```
> - storepass keystore 文件存储密码 
> - keypass 私钥加解密密码 
> - alias 实体别名(包括证书私钥)
> - dname 证书个人信息 
> - keyalt 采用公钥算法，默认是DSA keysize **长度(DSA算法对应的默认算法是sha1withDSA，不支持2048长度，此时需指定RSA)
> - validity 有效期 
> - keystore 指定keystore文件